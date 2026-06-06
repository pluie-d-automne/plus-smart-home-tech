package collector.service.handler.sensor;

import collector.service.handler.KafkaTopics;
import collector.service.handler.SensorEventHandler;
import collector.service.handler.KafkaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
public abstract class BaseSensorEventHandler<D extends SpecificRecordBase> implements SensorEventHandler {
    private final KafkaEventProducer producer;

    public BaseSensorEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    public void handle(SensorEventProto sensorEventProto) {
        SpecificRecordBase eventAvro = mapToAvro(sensorEventProto);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(KafkaTopics.TELEMETRY_SENSORS_TOPIC,
                sensorEventProto.getHubId(),
                eventAvro);
        log.info("Отправляю событие сенсора в топик");
        producer.send(record);
    }

    public abstract SensorEventProto.PayloadCase getMessageType();

    protected abstract SpecificRecordBase mapToAvro(SensorEventProto event);
}
