package club.tulane.commonerror.section01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 第一，ConcurrentHashMap提供的那些针对单一Key读写的API可以认为是线程安全的，但是诸如putAll这种涉及到多个Key的操作，并发读取可能无法确保读取到完整的数据。
 * 第二，ConcurrentHashMap只能确保提供的API是线程安全的，但是使用者组合使用多个API，ConcurrentHashMap无法从内部确保使用过程中的状态一致。
 *
 *
 * 使用ThreadLocal<Random>其实是两个过程，先是从ThreadLocal获得Random，这个开销其实不少的，而ThreadLocalRandom是直接使用unsafe从thread上去拿信息的，2步变为1步，这个开销不得不考虑
 */
@Slf4j
@RestController
@RequestMapping("/concurrent")
public class ConcurrentController {

    // 线程个数
    private static int THREAD_COUNT = 10;
    // 总元素
    private static int ITEM_COUNT = 1000;

    // 帮助方法, 用来获得一个指定元素数量模拟数据的 ConcurrentHashMap
    private ConcurrentHashMap<String, Long> getData(int count) {
        return LongStream.rangeClosed(1, count)
                .boxed()
                .collect(Collectors.toConcurrentMap(
                        i -> UUID.randomUUID().toString(),
                        Function.identity(),
                        (o1, o2) -> o1,
                        ConcurrentHashMap::new));
    }

    /**
     * 诸如 size、isEmpty 和 containsValue 等聚合方法，在并发情况下可能会反映 ConcurrentHashMap 的中间状态
     * 在并发情况下，这些方法的返回值只能用作参考，而不能用于流程控制。显然，利用 size 方法计算差异值，是一个流程控制
     * 诸如 putAll 这样的聚合方法也不能确保原子性，在 putAll 的过程中去获取数据可能会获取到部分数据
     *
     * @throws InterruptedException
     */
    @GetMapping("wrong")
    public String wrong() throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        // 初始 900 个元素
        log.info("init size:{}", concurrentHashMap.size());

        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        // 使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            // 查询还需要补充多少个元素
            int gap = ITEM_COUNT - concurrentHashMap.size();
            log.info("gao size:{}", gap);
            // 补充元素
            concurrentHashMap.putAll(getData(gap));
        }));
        // 等待所有任务完成
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        // 最后元素个数会是1000?
        log.info("finish size:{}", concurrentHashMap.size());
        return "OK";
    }

    /**
     * 使size, putAll 原子性
     * @return
     * @throws InterruptedException
     */
    @GetMapping("right")
    public String right() throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        log.info("init size:{}", concurrentHashMap.size());

        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            // 下面的这段复合逻辑需要锁一下这个 ConcurrentHashMap
            synchronized (concurrentHashMap) {
                int gap = ITEM_COUNT - concurrentHashMap.size();
                log.info("gao size:{}", gap);
                concurrentHashMap.putAll(getData(gap));
            }
        }));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);

        log.info("finish size:{}", concurrentHashMap.size());
        return "OK";
    }

}
