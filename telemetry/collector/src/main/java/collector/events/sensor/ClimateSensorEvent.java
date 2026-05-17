package collector.events.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    private int temperatureC; // Уровень температуры по шкале Цельсия.

    @NotNull
    private int humidity; // Влажность.

    @NotNull
    private int co2Level; // Уровень CO2.

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
