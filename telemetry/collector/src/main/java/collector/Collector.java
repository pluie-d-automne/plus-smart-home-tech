package collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@ConfigurationPropertiesScan
@EnableScheduling
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}
