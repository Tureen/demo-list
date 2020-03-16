package club.tulane.design.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多例模式
 * 固定数量
 */
public class IdGeneratorForPrototypeByFixedNum {

    private AtomicLong id = new AtomicLong(0);
    private static final Map<Long, IdGeneratorForPrototypeByFixedNum> instances = new HashMap<>();

    private static final int SERVER_COUNT = 3;

    static {
        instances.put(1L, new IdGeneratorForPrototypeByFixedNum());
        instances.put(2L, new IdGeneratorForPrototypeByFixedNum());
        instances.put(3L, new IdGeneratorForPrototypeByFixedNum());
    }

    private IdGeneratorForPrototypeByFixedNum() {
    }

    public static IdGeneratorForPrototypeByFixedNum getInstance(long serverNo){
        return instances.get(serverNo);
    }

    public static IdGeneratorForPrototypeByFixedNum getRandomInstance(){
        Random r = new Random();
        int no = r.nextInt(SERVER_COUNT) + 1;
        return instances.get(no);
    }

    public long getId(){
        return id.incrementAndGet();
    }
}
