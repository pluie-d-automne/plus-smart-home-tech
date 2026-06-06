package collector.service.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {
    private final KafkaClient client;
    private final Producer<String, SpecificRecordBase> producer;

    public KafkaEventProducer(KafkaClient client) {
        this.client = client;
        producer = client.getProducer();
    }

    public void stop() {
        client.stop();
    }

    public void send( ProducerRecord<String, SpecificRecordBase> record) {
        producer.send(record);
    }
}
