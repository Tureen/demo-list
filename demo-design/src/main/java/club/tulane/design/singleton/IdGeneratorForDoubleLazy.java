package club.tulane.design.singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程安全单例
 * 双重锁
 */
public class IdGeneratorForDoubleLazy {

    private AtomicLong id = new AtomicLong(0);
    private static IdGeneratorForDoubleLazy instance;

    private IdGeneratorForDoubleLazy() {
    }

    public static IdGeneratorForDoubleLazy getInstance(){
        if(instance == null){
            synchronized (IdGeneratorForDoubleLazy.class) {
                if(instance == null) {
                    /**
                     * new 操作的步骤
                     * 1. 分配内存空间
                     * 2. 初始化实例
                     * 3. 引用指向实例
                     *
                     * 由于旧版本的new操作, 会有2, 3指令重排
                     * 导致instance != null, 但对象未初始化, 另一个线程即有错误
                     *
                     * <p>
                     * java 8 的介绍
                     *
                     * Using localRef, we are reducing the access of volatile variable to just one for positive usecase.
                     * If we do not use localRef, then we would have to access volatile variable twice - once for checking null and then at method return time.
                     * Accessing volatile memory is quite an expensive affair because it involves reaching out to main memory.
                     */
                    instance = new IdGeneratorForDoubleLazy();
                }
            }
        }
        return instance;
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
