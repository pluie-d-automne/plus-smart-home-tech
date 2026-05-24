package collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    private String id; // Идентификатор удаленного устройства.

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
