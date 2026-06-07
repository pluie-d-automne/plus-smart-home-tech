package analyzer.service;

import analyzer.kafka.KafkaClient;
import analyzer.model.Action;
import analyzer.model.ActionType;
import analyzer.model.Condition;
import analyzer.model.ConditionOperation;
import analyzer.model.ConditionType;
import analyzer.model.Scenario;
import analyzer.model.Sensor;
import analyzer.repository.ScenarioRepository;
import analyzer.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties("kafka")
public class HubEventProcessor implements Runnable {
    private final KafkaClient client;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final Properties properties;

    @Override
    public void run() {
        Consumer consumer = client.getConsumer("hubs");

        // Хук для завершения JVM
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            // подписка на топики
            String topic = properties.getProperty("hubs.topic");
            consumer.subscribe(List.of(topic));
            // цикл опроса
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    // Читает и записывает в БД
                    HubEventAvro hubEventAvro = (HubEventAvro) record.value();
                    String hubId = hubEventAvro.getHubId();
                    if (hubEventAvro.getPayload() instanceof DeviceAddedEventAvro) {
                        String sensorId = ((DeviceAddedEventAvro) hubEventAvro.getPayload()).getId();
                        Sensor sensor = new Sensor(sensorId, hubId);
                        log.info("Сохраняю новый сенсор '{}' для хаба '{}'", sensorId, hubId);
                        sensorRepository.save(sensor);
                    } else if (hubEventAvro.getPayload() instanceof DeviceRemovedEventAvro) {
                        String sensorId = ((DeviceRemovedEventAvro) hubEventAvro.getPayload()).getId();
                        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(sensorId, hubId);
                        log.info("Удаляю сенсор '{}' из хаба '{}'", sensorId, hubId);
                        sensor.ifPresentOrElse(
                                v -> sensorRepository.delete(v),
                                () -> log.info("Сенсор с id '{}' не найден в хабе '{}'.", sensorId, hubId)
                        );
                        ;
                    } else if (hubEventAvro.getPayload() instanceof ScenarioAddedEventAvro) {
                        String scenarioName = ((ScenarioAddedEventAvro) hubEventAvro.getPayload()).getName();
                        Map<String, Action> actions = new HashMap<>();
                        Action newAction;

                        for (DeviceActionAvro action : ((ScenarioAddedEventAvro) hubEventAvro.getPayload()).getActions()) {
                            newAction = new Action();
                            newAction.setValue(action.getValue());
                            newAction.setType(EnumMapper.map(action.getType(), ActionType.class));
                            actions.put(action.getSensorId(), newAction);
                        };

                        Map<String, Condition> conditions = new HashMap<>();
                        Condition newCondition;

                        for (ScenarioConditionAvro condition : ((ScenarioAddedEventAvro) hubEventAvro.getPayload()).getConditions()) {
                            newCondition = new Condition();
                            Object val = condition.getValue();
                            Integer value;

                            if (val == null) {
                                value = null;
                            } else if (val instanceof Integer) {
                                value = (Integer) val;
                            } else if (val instanceof Boolean) {
                                value = (Boolean) val ? 1: 0;
                            } else {
                                value = null;
                            }

                            newCondition.setValue(value);
                            newCondition.setType(EnumMapper.map(condition.getType(), ConditionType.class));
                            newCondition.setOperation(EnumMapper.map(condition.getOperation(), ConditionOperation.class));
                            conditions.put(condition.getSensorId(), newCondition);
                        };

                        Scenario scenario = new Scenario();
                        scenario.setHubId(hubId);
                        scenario.setName(scenarioName);
                        scenario.setActions(actions);
                        scenario.setConditions(conditions);

                        log.info("Сохраняю новый сценарий '{}' для хаба '{}'", scenarioName, hubId);
                        scenarioRepository.save(scenario);

                    } else if (hubEventAvro.getPayload() instanceof ScenarioRemovedEventAvro) {
                        String scenarioName = ((ScenarioRemovedEventAvro) hubEventAvro.getPayload()).getName();
                        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
                        log.info("Удаляю сценарий '{}' из хаба '{}'", scenarioName, hubId);
                        scenario.ifPresentOrElse(
                                v -> scenarioRepository.delete(v),
                                () -> log.info("Сценарий с названием '{}' не найден в хабе '{}'.", scenarioName, hubId)
                        );
                    } else {
                        log.info("Получен неизвестный HubEventAvro payload");
                    }
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {

            try {
                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
