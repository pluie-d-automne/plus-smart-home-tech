package collector;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public class SwitchSensorEventHandler implements SensorEventHandler {
    // ...детали реализации опущены...

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ...детали реализации опущены...
    }
}
