package aggregator;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapshotAggregation {
    private static Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private static SensorsSnapshotAvro snapshot;
    private static SensorStateAvro oldState;

    static Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (snapshots.containsKey(event.getHubId())) {
            snapshot = snapshots.get(event.getHubId());
        } else {
            snapshot = new SensorsSnapshotAvro();
        }

        if (snapshot.getSensorsState().containsKey(event.getId())) {
            oldState = snapshot.getSensorsState().get(event.getId());

            // Если новое состояние раньше текущего, то нечего обновлять
            if (oldState.getTimestamp().isAfter(event.getTimestamp())) {
                return Optional.empty();
            }

            // Если ничего не изменилось, тоже нечего обновлять
            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        // если дошли до сюда, значит, пришли новые данные и снапшот нужно обновить
        SensorStateAvro newState = (SensorStateAvro) event.getPayload();
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        sensorStates.put(event.getId(), newState);
        snapshot.setSensorsState(sensorStates);
        snapshot.setTimestamp(event.getTimestamp());
        return Optional.of(snapshot);
    }
}
