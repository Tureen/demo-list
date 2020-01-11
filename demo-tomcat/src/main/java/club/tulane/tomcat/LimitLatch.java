package club.tulane.tomcat;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class LimitLatch {

    private class Sync extends AbstractQueuedSynchronizer {

        /**
         * 判断是否该阻塞, 返回小于0 则阻塞
         * @param arg
         * @return
         */
        @Override
        protected int tryAcquireShared(int arg) {
            long newCount = count.incrementAndGet();
            if(newCount > limit){
                count.decrementAndGet();
                return -1;
            }
            return 1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            count.decrementAndGet();
            return true;
        }

    }
    private final Sync sync;

    private final AtomicLong count;
    private volatile long limit;
    public LimitLatch(long limit) {
        this.limit = limit;
        this.count = new AtomicLong(0);
        this.sync = new Sync();
    }

    public void countUpOrAwait() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public long countDown() {
        sync.releaseShared(0);
        long result = getCount();
        System.out.println("Counting down["+Thread.currentThread().getName()+"] latch="+result);
        return result;
    }

    private long getCount() {
        return count.get();
    }
}
