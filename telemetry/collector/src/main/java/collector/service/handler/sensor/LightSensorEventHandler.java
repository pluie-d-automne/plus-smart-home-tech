package collector.service.handler.sensor;

import collector.service.handler.KafkaEventProducer;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

import java.time.Instant;

@Component(value = "LIGHT_SENSOR")
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {
    private LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }
    Object payload;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public  SpecificRecordBase mapToAvro(SensorEventProto event) {
        payload = LightSensorAvro.newBuilder()
                .setLuminosity(event.getLightSensor().getLuminosity())
                .setLinkQuality(event.getLightSensor().getLinkQuality())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}
