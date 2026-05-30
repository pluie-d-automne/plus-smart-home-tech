package collector.service.handler.hub;

import collector.service.handler.KafkaTopics;
import collector.service.handler.KafkaEventProducer;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import collector.service.handler.HubEventHandler;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public abstract class BaseHubEventHandler<D extends SpecificRecordBase> implements HubEventHandler {
    private final KafkaEventProducer producer;

    public BaseHubEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    public void handle(HubEventProto hubEventProto) {
        SpecificRecordBase eventAvro = mapToAvro(hubEventProto);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(KafkaTopics.TELEMETRY_HUBS_TOPIC,
                hubEventProto.getHubId(),
                eventAvro);
        log.info("Отправляю событие хаба в топик");
        producer.send(record);
    }

    public abstract HubEventProto.PayloadCase getMessageType();

    protected abstract SpecificRecordBase mapToAvro(HubEventProto event);
}
