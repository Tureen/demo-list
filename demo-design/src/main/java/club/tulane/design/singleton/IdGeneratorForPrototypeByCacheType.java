package club.tulane.design.singleton;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多例模式
 * 类型一致返回同一对象
 *
 * 附: 类似枚举类型, 枚举也是每个类型只能生成一个对象
 */
public class IdGeneratorForPrototypeByCacheType {

    private AtomicLong id = new AtomicLong(0);
    private static final ConcurrentHashMap<String, IdGeneratorForPrototypeByCacheType> instances = new ConcurrentHashMap<>();

    private IdGeneratorForPrototypeByCacheType() {
    }

    public static IdGeneratorForPrototypeByCacheType getInstance(Class clazz){
        instances.putIfAbsent(clazz.getName(), new IdGeneratorForPrototypeByCacheType());
        return instances.get(clazz.getName());
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
