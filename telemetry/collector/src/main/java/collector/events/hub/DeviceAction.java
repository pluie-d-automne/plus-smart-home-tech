package collector.events.hub;
import  ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {
    private String sensorId;
    private ActionTypeAvro type;
    private int value;
}
