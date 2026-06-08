package collector.service.handler.sensor;

import collector.service.handler.KafkaEventProducer;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@Component(value = "TEMPERATURE_SENSOR")
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {
    private TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }
    private Object payload;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public  SpecificRecordBase mapToAvro(SensorEventProto event) {
        payload = TemperatureSensorAvro.newBuilder()
                .setTemperatureF(event.getTemperatureSensor().getTemperatureF())
                .setTemperatureC(event.getTemperatureSensor().getTemperatureC())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}
