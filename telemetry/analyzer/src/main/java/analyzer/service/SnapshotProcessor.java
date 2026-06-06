package analyzer.service;

import analyzer.kafka.KafkaClient;
import analyzer.model.Action;
import analyzer.model.Condition;
import analyzer.model.ConditionOperation;
import analyzer.model.ConditionType;
import analyzer.model.Scenario;
import analyzer.repository.ScenarioRepository;
import com.google.protobuf.Timestamp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties("kafka")
public class SnapshotProcessor implements Runnable {
    private final KafkaClient client;
    private final Properties properties;
    private final ActionProducer producer;
    private final ScenarioRepository scenarioRepository;

    @Override
    public void run() {
        Consumer consumer = client.getConsumer("snapshots");

        try {
            // подписка на топики
            String topic = properties.getProperty("snapshots.topic");
            consumer.subscribe(List.of(topic));
            // цикл опроса
            while (true) {
                // ...детали реализации...
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    // Читает и записывает в БД
                    SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
                    String hubId = snapshot.getHubId();
                    Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
                    handleHubScenarios(hubId, sensorStates);
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {

            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private boolean compareCondition(ConditionOperation operation,
                                     Integer snapshotValue,
                                     Integer conditionValue) {
        if (snapshotValue == null) {
            return false;
        }
        switch (operation) {
            case GREATER_THAN -> {return snapshotValue.compareTo(conditionValue) > 0;}
            case EQUALS -> {return snapshotValue.equals(conditionValue);}
            case LOWER_THAN -> {return snapshotValue.compareTo(conditionValue) < 0;}
            default -> {log.info("Неизвестный тип операции: {}", operation);
                        return false;}
        }
    }

    private Integer getSensorValueByType(ConditionType type, Object stateData) {
        switch (type) {
            case MOTION -> {return ((MotionSensorAvro) stateData).getVoltage();}
            case SWITCH -> {return ((SwitchSensorAvro) stateData).getState() ? 1 : 0;}
            case CO2LEVEL -> {return ((ClimateSensorAvro) stateData).getCo2Level();}
            case HUMIDITY -> {return ((ClimateSensorAvro) stateData).getHumidity();}
            case LUMINOSITY -> {return ((LightSensorAvro) stateData).getLuminosity();}
            case TEMPERATURE -> {return ((ClimateSensorAvro) stateData).getTemperatureC();}
            default -> {return null;}
        }
    }

    @Transactional
    private void handleHubScenarios(String hubId, Map<String, SensorStateAvro> sensorStates) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        for (Scenario scenario : scenarios) {
            Map<String, Condition> conditions = scenario.getConditions();

            for (String sensorId : conditions.keySet()) {
                Condition condition = conditions.get(sensorId);
                SensorStateAvro state = sensorStates.get(sensorId);
                if (state == null) {
                    log.info("Сценарий '{}' не проходит, так как отсутствует состояние для сенсора '{}'",
                            scenario.getName(), sensorId );
                    break;
                }
                Integer sensorValue = getSensorValueByType(condition.getType(), state.getData());
                if (! compareCondition(condition.getOperation(), sensorValue, condition.getValue())) {
                    log.info("Сценарий '{}' не проходит по условию '{}' для сенсора '{}'",
                            scenario.getName(), condition.getType(), sensorId );
                    break;
                };
            }

            log.info("Выполнены все условия сценария '{}' на хабе '{}'", scenario.getName(), hubId);
            Map<String, Action> actions = scenario.getActions();
            for (String sensorId : actions.keySet()) {
                Action action = actions.get(sensorId);
                DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setValue(action.getValue())
                        .setType(EnumMapper.map(action.getType(), ActionTypeProto.class))
                        .build();
                Instant now = Instant.now();
                DeviceActionRequest actionRequest = DeviceActionRequest.newBuilder()
                        .setAction(actionProto)
                        .setHubId(hubId)
                        .setScenarioName(scenario.getName())
                        .setTimestamp(Timestamp.newBuilder()
                                .setSeconds(now.getEpochSecond())
                                .setNanos(now.getNano())
                                .build())
                        .build();
                log.info("Send new action Request");
                producer.sendAction(actionRequest);
            }
        }
    }

}
