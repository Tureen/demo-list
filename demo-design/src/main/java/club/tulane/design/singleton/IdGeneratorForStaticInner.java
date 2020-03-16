package club.tulane.design.singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程安全单例
 * 静态内部类
 */
public class IdGeneratorForStaticInner {

    private AtomicLong id = new AtomicLong(0);
    private IdGeneratorForStaticInner() {
    }

    private static class SingletonHolder{
        private static final IdGeneratorForStaticInner instance = new IdGeneratorForStaticInner();
    }

    public static IdGeneratorForStaticInner getInstance(){
        return SingletonHolder.instance;
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
