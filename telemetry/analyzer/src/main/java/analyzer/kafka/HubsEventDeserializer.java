package analyzer.kafka;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubsEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {
    public HubsEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}