package club.tulane.commonerror.section03.threadpoolreuse;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/threadpoolreuse")
public class ThreadPoolReuseController {

    @GetMapping("/wrong")
    public String wrong(){
        ThreadPoolExecutor threadPool = ThreadpoolHelper.getRightThreadPool();
        IntStream.rangeClosed(1, 10).forEach(value -> {
            threadPool.execute(() -> {
                String payload = IntStream.rangeClosed(1, 1000000)
                        .mapToObj(__ -> "a")
                        .collect(Collectors.joining("")) + UUID.randomUUID().toString();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                log.debug(payload);
            });
        });
        return "OK";
    }

    static class ThreadpoolHelper {

        private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10, 50,
                2, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("demo-threadpool-%d").get());

        public static ThreadPoolExecutor getThreadPool() {
            return (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        static ThreadPoolExecutor getRightThreadPool(){
            return threadPoolExecutor;
        }
    }
}
