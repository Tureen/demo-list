package club.tulane.commonerror.section02;

import lombok.extern.slf4j.Slf4j;

/**
 * add() 与 compare() 都上锁, 锁住对象
 */
@Slf4j
public class Interesting {

    volatile int a = 1;
    volatile int b = 1;

    public synchronized void add(){
        log.info("add start");
        for (int i = 0; i < 10000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    public synchronized void compare() {
        log.info("compare start");
        for (int i = 0; i < 10000; i++) {
            // a始终等于b嘛?
            if (a < b) {
                log.info("a:{}, b:{}, {}", a, b, a > b);
                // 最后的 a > b 应该始终是 false 嘛?
            }
        }
        log.info("compare done");
    }

    public static void main(String[] args) {
        Interesting interesting = new Interesting();
        new Thread(interesting::add).start();
        new Thread(() -> interesting.compare()).start();
    }
}
