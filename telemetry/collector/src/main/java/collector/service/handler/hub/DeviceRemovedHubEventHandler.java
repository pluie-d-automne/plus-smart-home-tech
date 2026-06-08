package collector.service.handler.hub;

import collector.service.handler.KafkaEventProducer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Component(value = "DEVICE_REMOVED")
public class DeviceRemovedHubEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    private Object payload;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;

    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        payload = DeviceRemovedEventAvro.newBuilder()
                .setId(event.getDeviceRemoved().getId())
                .build();

        return HubEventAvro.newBuilder()
                .setPayload(payload)
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setHubId(event.getHubId())
                .build();
    }
}
