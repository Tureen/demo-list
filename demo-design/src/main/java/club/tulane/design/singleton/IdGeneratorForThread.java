package club.tulane.design.singleton;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程安全单例
 * 线程唯一的单例
 */
public class IdGeneratorForThread {

    private AtomicLong id = new AtomicLong(0);

    private static final ConcurrentHashMap<Long, IdGeneratorForThread> instances = new ConcurrentHashMap<>();

    private IdGeneratorForThread() {
    }

    public static IdGeneratorForThread getInstance(){
        Long currentThreadId = Thread.currentThread().getId();
        instances.putIfAbsent(currentThreadId, new IdGeneratorForThread());
        return instances.get(currentThreadId);
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
