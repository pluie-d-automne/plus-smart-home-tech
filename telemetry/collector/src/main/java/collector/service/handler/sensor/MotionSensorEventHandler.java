package collector.service.handler.sensor;

import collector.service.handler.KafkaEventProducer;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

import java.time.Instant;

@Component(value = "MOTION_SENSOR")
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    private MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }
    Object payload;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public  SpecificRecordBase mapToAvro(SensorEventProto event) {
        payload = MotionSensorAvro.newBuilder()
                .setMotion(event.getMotionSensor().getMotion())
                .setVoltage(event.getMotionSensor().getVoltage())
                .setLinkQuality(event.getMotionSensor().getLinkQuality())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}
