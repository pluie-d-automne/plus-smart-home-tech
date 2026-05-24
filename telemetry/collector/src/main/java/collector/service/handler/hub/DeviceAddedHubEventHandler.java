package collector.service.handler.hub;

import collector.service.handler.KafkaEventProducer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import collector.utils.EnumMapper;
import org.apache.avro.specific.SpecificRecordBase;

@Component(value = "DEVICE_ADDED")
public class DeviceAddedHubEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;

    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getDeviceAdded().getId())
                .setType(EnumMapper.map(event.getDeviceAdded().getType(), DeviceTypeAvro.class))
                .build();
    }
}
