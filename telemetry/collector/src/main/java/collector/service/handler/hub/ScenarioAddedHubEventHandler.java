package collector.service.handler.hub;

import collector.service.handler.KafkaEventProducer;
import collector.utils.EnumMapper;
import com.google.protobuf.NullValue;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.time.Instant;
import java.util.List;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    private Object payload;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;

    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        List<ScenarioConditionAvro> conditions = event.getScenarioAdded().getConditionsList().stream()
                .map(x -> mapScenarioCondition(x))
                .toList();

        List<DeviceActionAvro> actions = event.getScenarioAdded().getActionsList().stream()
                .map(x-> mapDeviceAction(x))
                .toList();

        payload = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getScenarioAdded().getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();

        return HubEventAvro.newBuilder()
                .setPayload(payload)
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setHubId(event.getHubId())
                .build();
    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioConditionProto condition) {
        Object value;
        if (condition.getValueCase().getNumber() == 4) {
            value = condition.getBoolValue();
        } else if  (condition.getValueCase().getNumber() == 5) {
            value = condition.getIntValue();
        } else {
            value = NullValue.NULL_VALUE;
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                .setValue(value)
                .build();
    }

    private DeviceActionAvro mapDeviceAction(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))
                .setValue(action.getValue())
                .build();
    }
}
