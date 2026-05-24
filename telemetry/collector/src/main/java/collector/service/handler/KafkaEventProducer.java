package collector.service.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {
    private final KafkaClient client;

    public KafkaEventProducer(KafkaClient client) {
        this.client = client;
    }

    public void stop() {
        client.stop();
    }

    public void send( ProducerRecord<String, SpecificRecordBase> record) {
        client.getProducer().send(record);
    }
}
