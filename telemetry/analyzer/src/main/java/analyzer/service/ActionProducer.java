package analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;


@Service
@Slf4j
public class ActionProducer {
    private final HubRouterControllerBlockingStub hubRouterClient;

    public ActionProducer(@GrpcClient("hub-router")
                     HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(DeviceActionRequest action) {
        log.info("Отправляю данные: {}", action.getAllFields());
        hubRouterClient.handleDeviceAction(action);
    }
}
