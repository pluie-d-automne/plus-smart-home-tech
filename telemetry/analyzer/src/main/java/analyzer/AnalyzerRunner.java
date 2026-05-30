package analyzer;

import analyzer.service.HubEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final HubEventProcessor hubEventProcessor;
   //private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        // запускаем в отдельном потоке обработчик событий от пользовательских хабов
//        Thread hubEventsThread = new Thread(hubEventProcessor);
//        hubEventsThread.setName("HubEventHandlerThread");
//        hubEventsThread.start();
        hubEventProcessor.run();
        // В текущем потоке начинаем обработку снимков состояния датчиков
  //      snapshotProcessor.start();
    }
}
