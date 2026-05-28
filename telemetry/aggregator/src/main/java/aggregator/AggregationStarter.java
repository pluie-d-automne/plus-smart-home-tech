package aggregator;

import aggregator.kafka_utils.KafkaClient;
import aggregator.kafka_utils.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Consumer consumer;
    private final Producer producer;
    private final SnapshotAggregation agg;

    @Autowired
    public AggregationStarter(KafkaClient client) {
        //KafkaClientConfiguration config = new KafkaClientConfiguration();
        //KafkaClient client = config.getClient();
        this.consumer = client.getConsumer();
        this.producer = client.getProducer();
        this.agg = new SnapshotAggregation();
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try {
            consumer.subscribe(List.of(KafkaTopics.TELEMETRY_SENSORS_TOPIC));
            // Цикл обработки событий
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    if (record.value() instanceof SensorEventAvro) {
                        SensorEventAvro event = (SensorEventAvro) record.value();
                        Optional<SensorsSnapshotAvro> snapshotAvro = agg.updateState(event);
                        if (snapshotAvro.isPresent()) {
                            SensorsSnapshotAvro snapshot = snapshotAvro.get();
                            ProducerRecord<String, SpecificRecordBase> recordToSend = new ProducerRecord<>(KafkaTopics.TELEMETRY_SNAPSHOTS_TOPIC,
                                    snapshot.getHubId(),
                                    snapshot);
                            producer.send(recordToSend);
                        }
                    }
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедиться,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                producer.flush();
                // здесь нужно вызвать метод консьюмера для фиксации смещений
                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
