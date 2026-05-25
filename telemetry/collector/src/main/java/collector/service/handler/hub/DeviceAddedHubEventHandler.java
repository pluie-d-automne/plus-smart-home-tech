package collector.service.handler.hub;

import collector.service.handler.KafkaEventProducer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import collector.utils.EnumMapper;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Component(value = "DEVICE_ADDED")
public class DeviceAddedHubEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }
    private Object payload;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;

    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        payload = DeviceAddedEventAvro.newBuilder()
                .setId(event.getDeviceAdded().getId())
                .setType(EnumMapper.map(event.getDeviceAdded().getType(), DeviceTypeAvro.class))
                .build();

        return HubEventAvro.newBuilder()
                .setPayload(payload)
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setHubId(event.getHubId())
                .build();
    }
}
