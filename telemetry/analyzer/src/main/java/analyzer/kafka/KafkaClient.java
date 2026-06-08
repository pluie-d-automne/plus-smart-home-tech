package analyzer.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface KafkaClient {
    Consumer<String, SpecificRecordBase> getConsumer(String type);

    void stop();
}
