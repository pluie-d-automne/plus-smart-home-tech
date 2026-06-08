package aggregator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SnapshotAggregation {
    private Map<String, SensorsSnapshotAvro> snapshots;
    private SensorsSnapshotAvro snapshot;
    private SensorStateAvro oldState;
    private Map<String, SensorStateAvro> sensorStates;
    public SnapshotAggregation() {
        snapshots = new HashMap<>();
    }

     Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (snapshots.containsKey(event.getHubId())) {
            snapshot = snapshots.get(event.getHubId());
            sensorStates = snapshot.getSensorsState();
            if (sensorStates.containsKey(event.getId())) {
                oldState = sensorStates.get(event.getId());

                if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                    log.info("Полученное состояние хаба {} раньше текущего. Снапшот не обновляю.", event.getId());
                    return Optional.empty();
                }

                if (oldState.getData().equals(event.getPayload())) {
                    log.info("Состояние хаба {} не изменилось. Снапшот не обновляю.", event.getId());
                    return Optional.empty();
                }
            }
        } else {
            snapshot = new SensorsSnapshotAvro();
            sensorStates = new HashMap<>();
        }

        log.info("Обновляю снапшот для хаба {}.", event.getId());
        SensorStateAvro newState = new SensorStateAvro();
        newState.setData(event.getPayload());
        newState.setTimestamp(event.getTimestamp());
        sensorStates.put(event.getId(), newState);
        snapshot.setSensorsState(sensorStates);
        snapshot.setTimestamp(event.getTimestamp());
        snapshot.setHubId(event.getHubId());
        snapshots.put(event.getHubId(), snapshot);
        return Optional.of(snapshot);
    }
}
