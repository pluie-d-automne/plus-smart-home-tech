package analyzer.kafka;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotsEventDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {
    public SnapshotsEventDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}