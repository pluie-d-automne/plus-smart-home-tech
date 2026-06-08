package aggregator.kafka_utils;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public interface KafkaClient {
    Producer<String, SpecificRecordBase> getProducer();

    Consumer<String, SpecificRecordBase> getConsumer();

    void stop();

    Properties getProperties();
}
