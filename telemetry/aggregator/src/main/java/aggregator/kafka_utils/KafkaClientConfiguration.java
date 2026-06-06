package aggregator.kafka_utils;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class KafkaClientConfiguration {
    @Bean
    public KafkaClient getClient() {
        return new KafkaClient() {

            @Autowired
            private Environment env;

            private Consumer<String, SpecificRecordBase> consumer;

            private Producer<String, SpecificRecordBase> producer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties config = new Properties();
                config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("consumer.bootstrap_servers"));
                config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env.getProperty("consumer.key_deserializer_class"));
                config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, env.getProperty("consumer.value_deserializer_class"));
                config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("consumer.group_id"));
                consumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,  env.getProperty("producer.bootstrap_servers"));
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env.getProperty("producer.key_serializer_class"));
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env.getProperty("producer.value_serializer_class"));

                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (producer != null) {
                    producer.close();
                }
                if (consumer !=null) {
                    consumer.close();
                }
            }
        };
    }
}
