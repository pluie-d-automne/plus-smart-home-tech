package collector.service.handler.sensor;

import collector.service.handler.KafkaEventProducer;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

import java.time.Instant;

@Component(value = "CLIMATE_SENSOR")
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorAvro> {
    private ClimateSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }
    private Object payload;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public  SpecificRecordBase mapToAvro(SensorEventProto event) {
        payload = ClimateSensorAvro.newBuilder()
                .setHumidity(event.getClimateSensor().getHumidity())
                .setCo2Level(event.getClimateSensor().getCo2Level())
                .setTemperatureC(event.getClimateSensor().getTemperatureC())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}
