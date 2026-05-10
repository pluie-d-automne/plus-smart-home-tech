package collector;

import collector.events.hub.DeviceAction;
import collector.events.hub.DeviceAddedEvent;
import collector.events.hub.DeviceRemovedEvent;
import collector.events.hub.HubEvent;
import collector.events.hub.HubEventType;
import collector.events.hub.ScenarioAddedEvent;
import collector.events.hub.ScenarioCondition;
import collector.events.hub.ScenarioRemovedEvent;
import collector.events.sensor.ClimateSensorEvent;
import collector.events.sensor.LightSensorEvent;
import collector.events.sensor.MotionSensorEvent;
import collector.events.sensor.SensorEvent;
import collector.events.sensor.SensorEventType;
import collector.events.sensor.SwitchSensorEvent;
import collector.events.sensor.TemperatureSensorEvent;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class CollectorController {
    private final CollectorClient client;

    @PreDestroy
    public void stop() {
        client.stop();
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        Object payload;
        switch (event.getType()) {
            case SensorEventType.CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent specificEvent = (ClimateSensorEvent) event;
                payload = ClimateSensorAvro.newBuilder()
                    .setTemperatureC(specificEvent.getTemperatureC())
                        .setCo2Level(specificEvent.getCo2Level())
                        .setHumidity(specificEvent.getHumidity())
                        .build();
            }
            case SensorEventType.LIGHT_SENSOR_EVENT -> {
                LightSensorEvent specificEvent = (LightSensorEvent) event;
                payload = LightSensorAvro.newBuilder()
                        .setLinkQuality(specificEvent.getLinkQuality())
                        .setLuminosity(specificEvent.getLuminosity())
                        .build();
            }
            case SensorEventType.MOTION_SENSOR_EVENT -> {
                MotionSensorEvent specificEvent = (MotionSensorEvent) event;
                payload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(specificEvent.getLinkQuality())
                        .setMotion(specificEvent.isMotion())
                        .setVoltage(specificEvent.getVoltage())
                        .build();
            }
            case SensorEventType.SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent specificEvent = (SwitchSensorEvent) event;
                payload = SwitchSensorAvro.newBuilder()
                        .setState(specificEvent.isState())
                        .build();
            }
            case SensorEventType.TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent specificEvent = (TemperatureSensorEvent) event;
                payload = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(specificEvent.getTemperatureC())
                        .setTemperatureF(specificEvent.getTemperatureF())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестное событие: " + event);
        }

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_TOPIC, eventAvro);
        client.getProducer().send(record);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        Object payload;
        switch (event.getType()) {
            case HubEventType.DEVICE_ADDED -> {
                DeviceAddedEvent specificEvent = (DeviceAddedEvent) event;
                payload = DeviceAddedEventAvro.newBuilder()
                        .setId(specificEvent.getId())
                        .setType(specificEvent.getDeviceType())
                        .build();
            }
            case HubEventType.DEVICE_REMOVED -> {
                DeviceRemovedEvent specificEvent = (DeviceRemovedEvent) event;
                payload = DeviceRemovedEventAvro.newBuilder()
                        .setId(specificEvent.getId())
                        .build();
            }
            case HubEventType.SCENARIO_ADDED -> {
                ScenarioAddedEvent specificEvent = (ScenarioAddedEvent) event;
                List<DeviceActionAvro> actions = specificEvent.getActions()
                        .stream()
                        .map(x -> mapDeviceActionAvro(x))
                        .toList();
                List<ScenarioConditionAvro> conditions = specificEvent.getConditions()
                        .stream()
                        .map(x -> mapScenarioCondition(x))
                        .toList();
                payload = ScenarioAddedEventAvro.newBuilder()
                        .setName(specificEvent.getName())
                        .setActions(actions)
                        .setConditions(conditions)
                        .build();
            }
            case HubEventType.SCENARIO_REMOVED -> {
                ScenarioRemovedEvent specificEvent = (ScenarioRemovedEvent) event;
                payload = ScenarioRemovedEventAvro.newBuilder()
                        .setName(specificEvent.getName())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестное событие: " + event);
        }
        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS_TOPIC, eventAvro);
        client.getProducer().send(record);
    }

    private DeviceActionAvro mapDeviceActionAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(action.getType())
                .setValue(action.getValue())
                .build();
    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setOperation(condition.getOperation())
                .setValue(condition.getValue())
                .setType(condition.getType())
                .build();
    }
}
