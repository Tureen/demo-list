package club.tulane.design.singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程安全单例
 * 懒汉
 */
public class IdGeneratorForLazy {

    private AtomicLong id = new AtomicLong(0);
    private static IdGeneratorForLazy instance;

    private IdGeneratorForLazy() {
    }

    public static synchronized IdGeneratorForLazy getInstance(){
        if(instance == null){
            instance = new IdGeneratorForLazy();
        }
        return instance;
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
