package club.tulane.design.singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程安全单例
 * 枚举实现
 */
public enum  IdGeneratorForEnum {

    INSTANCE;
    private AtomicLong id = new AtomicLong(0);

    public long getId(){
        return id.incrementAndGet();
    }
}
