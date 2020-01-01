package club.tulane.tomcat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NioEndpoint {

    private volatile Selector SHARED_SELECTOR;
    private ServerSocketChannel serverSocket = null;
    private Poller[] pollers = null;
    protected Acceptor[] acceptors;
    private BlockingQueue<PollerEvent> events = new LinkedBlockingQueue<>();

    private long selectorTimeout = 1000;
    private int pollerThreadCount = 1;
    private volatile LimitLatch connectionLimitLatch = null;

    private int maxConnections = 1;
    public int  getMaxConnections() { return this.maxConnections; }

    public static final int OP_REGISTER = 0x100;

    private AtomicInteger pollerRotater = new AtomicInteger(0);

    /**
     * 轮询获取Poller池中Poller对象
     * 取绝对值因为, 当超过integer最大值后会转负数
     * @return
     */
    protected Poller getPoller0() {
        int idx = Math.abs(pollerRotater.incrementAndGet()) % pollers.length;
        return pollers[idx];
    }

    /**
     * Name of the thread pool, which will be used for naming child threads.
     */
    private String name = "TP";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Acceptor thread count.
     */
    protected int acceptorThreadCount = 1;

    public void setAcceptorThreadCount(int acceptorThreadCount) {
        this.acceptorThreadCount = acceptorThreadCount;
    }

    public int getAcceptorThreadCount() {
        return acceptorThreadCount;
    }

    protected int threadPriority = Thread.NORM_PRIORITY;

    public void start() throws IOException {
        bind();
        startInternal();
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void bind() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(8000));
        serverSocket.configureBlocking(true);
    }

    public void startInternal() throws IOException {
        // 初始化限制连接
        initializeConnectionLatch();

        pollers = new Poller[pollerThreadCount];
        for (int i = 0; i < pollers.length; i++) {
            pollers[i] = new Poller();
            Thread pollerThread = new Thread(pollers[i], getName() + "-ClientPoller-" + i);
            pollerThread.setPriority(threadPriority);
            pollerThread.setDaemon(true);
            pollerThread.start();
        }
        startAcceptorThreads();
    }

    private LimitLatch initializeConnectionLatch() {
        if(maxConnections == -1) return null;
        if(connectionLimitLatch == null){
            connectionLimitLatch = new LimitLatch(getMaxConnections());
        }
        return connectionLimitLatch;
    }

    protected final void startAcceptorThreads() {
        int count = getAcceptorThreadCount();
        acceptors = new Acceptor[count];

        for (int i = 0; i < count; i++) {
            acceptors[i] = new Acceptor();
            String threadName = getName() + "-Acceptor-" + i;
            acceptors[i].setThreadName(threadName);
            Thread t = new Thread(acceptors[i], threadName);
            t.setPriority(threadPriority);
            t.setDaemon(true);
            t.start();
        }
    }

//    protected Selector getSharedSelector() throws IOException {
//        if(SHARED_SELECTOR == null){
//            synchronized (this){
//                if(SHARED_SELECTOR == null){
//                    SHARED_SELECTOR = Selector.open();
//                }
//            }
//        }
//        return SHARED_SELECTOR;
//    }



    protected class Acceptor implements Runnable {

        private String threadName;

        protected final void setThreadName(final String threadName) {
            this.threadName = threadName;
        }

        protected final String getThreadName() {
            return threadName;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    countUpOrAwaitConnection();

                    SocketChannel socketChannel = serverSocket.accept();
                    setSocket(socketChannel);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void countUpOrAwaitConnection() throws InterruptedException {
        if(maxConnections == -1) return;
        LimitLatch latch = connectionLimitLatch;
        if(latch != null) latch.countUpOrAwait();
    }

    public void setSocket(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);
        NioChannel channel = new NioChannel(socketChannel);
        getPoller0().register(channel);
    }

    protected class PollerEvent implements Runnable {

        private NioChannel socket;
        private int interestOps;

        public PollerEvent(NioChannel socket, int interestOps) {
            this.socket = socket;
            this.interestOps = interestOps;
        }

        @Override
        public void run() {
            if (interestOps == OP_REGISTER) {
                try {
                    socket.getIOChannel().register(socket.getPoller().getSelector(), SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Poller implements Runnable {

        private Selector selector;

        public Poller() throws IOException {
            this.selector = Selector.open();
        }

        public Selector getSelector() {
            return selector;
        }

        private void events() {
            PollerEvent pe = null;
            for (int i = 0, size = events.size(); i < size && (pe = events.poll()) != null; i++) {
                pe.run();
            }
        }

        public void register(final NioChannel socket) {
            socket.setPoller(this);
            PollerEvent r = new PollerEvent(socket, OP_REGISTER);
            events.offer(r);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    events();

                    int readyChannels = selector.select(selectorTimeout); // 获取可用channel数量(阻塞等待)
                    if (readyChannels == 0) continue;

                    Set<SelectionKey> selectionKeySet = selector.selectedKeys(); // 获取可用channel集合
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove(); // 移除Set中的当前selectionKey, 因为selectedKeys()每次返回原有集合

                        new SocketProcessor(selector, selectionKey).run();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            }
        }

    }

    protected class SocketProcessor implements Runnable {

        private Selector selector;
        private SelectionKey selectionKey;

        public SocketProcessor(Selector selector, SelectionKey selectionKey) {
            this.selector = selector;
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String request = readMessageFromChannel(socketChannel);
            System.out.println(request);
            try {
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }

        public String readMessageFromChannel(SocketChannel socketChannel) {
            String request = null;
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                request = "";
                while (socketChannel.read(byteBuffer) > 0) { // 将socketChannel的字节循环写入byteBuffer
                    byteBuffer.flip(); // 切换buffer为读模式
                    request += StandardCharsets.UTF_8.decode(byteBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return request;
        }
    }

    public static void main(String[] args) throws IOException {
        new NioEndpoint().start();
    }
}
