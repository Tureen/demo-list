package com.tulane.gctest.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("service start success");
    }

    //创建线程池，其中有4096个线程。
    private ExecutorService executor = Executors.newFixedThreadPool(500);
    //全局变量，访问它需要加锁。
    private int count;

    //以固定的速率向线程池中加入任务
    @Scheduled(fixedRate = 10)
    public void lockContention() {
        IntStream.range(0, 1000000).forEach(i -> executor.submit(this::incrementSync));
    }

    //具体任务，就是将count数加一
    private synchronized void incrementSync() {
        count = (count + 1) % 10000000;
    }
}
