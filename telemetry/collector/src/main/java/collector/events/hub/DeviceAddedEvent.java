package collector.events.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id; // Идентификатор добавленного устройства.

    @NotBlank
    private DeviceType deviceType; // Тип добавленного устройства.

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
