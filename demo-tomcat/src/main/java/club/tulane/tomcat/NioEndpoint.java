package club.tulane.tomcat;

import java.io.EOFException;
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
        serverSocket.socket().bind(new InetSocketAddress(8080));
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

    private long countDownConnection() {
        if(maxConnections == -1) return -1;
        LimitLatch latch = connectionLimitLatch;
        if(latch != null){
            long result = latch.countDown();
            if(result < 0){
                System.out.println("endpoint.warn.incorrectConnectionCount");
            }
            return result;
        }else return -1;
    }

    public void setSocket(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);
        NioChannel channel = new NioChannel(socketChannel);
        getPoller0().register(channel);
    }

    protected class PollerEvent implements Runnable {

        private NioChannel socket;
        private int interestOps;
        private NioSocketWrapper socketWrapper;

        public PollerEvent(NioChannel socket, NioSocketWrapper w, int interestOps) {
            this.socket = socket;
            socketWrapper = w;
            this.interestOps = interestOps;
        }

        @Override
        public void run() {
            if (interestOps == OP_REGISTER) {
                try {
                    socket.getIOChannel().register(socket.getPoller().getSelector(), SelectionKey.OP_READ, socketWrapper);
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
            NioSocketWrapper ka = new NioSocketWrapper(socket, NioEndpoint.this);
            socket.setPoller(this);
            PollerEvent r = new PollerEvent(socket, ka, OP_REGISTER);
            events.offer(r);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    events();

                    int keyCount = selector.select(selectorTimeout); // 获取可用channel数量(阻塞等待)
                    if (keyCount == 0) continue;

                    Set<SelectionKey> selectionKeySet = selector.selectedKeys(); // 获取可用channel集合
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey sk = iterator.next();
                        NioSocketWrapper attachment = (NioSocketWrapper) sk.attachment();

                        iterator.remove(); // 移除Set中的当前selectionKey, 因为selectedKeys()每次返回原有集合

                        processKey(sk, attachment);
                    }

                    timeout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            }
        }

        private void timeout() {
            for (SelectionKey key : selector.keys()) {
                NioSocketWrapper ka = (NioSocketWrapper) key.attachment();
                //we don't support any keys without attachments
                if(ka == null){
                    cancelledKey(key);
                }
            }
        }

        public void cancelledKey(SelectionKey key) {
            NioSocketWrapper ka = null;
            if(key == null) return;
            ka = (NioSocketWrapper) key.attach(null);
            if(ka != null){
                try {
                    ka.getSocket().close(true);
                } catch (IOException e) {
                    System.out.println("endpoint.debug.socketCloseFail");
                }
            }
            if (ka != null) {
                countDownConnection();
            }
        }

        protected void processKey(SelectionKey sk, NioSocketWrapper attachment){
            if(sk.isReadable()){
                processSocket(attachment, SocketEvent.OPEN_READ);
            }
        }

    }



    private void processSocket(NioSocketWrapper socketWrapper, SocketEvent event) {
        SocketProcessor sc = createSocketProcessor(socketWrapper, event);
        sc.run();
//        new Thread(sc).start();
    }

    private SocketProcessor createSocketProcessor(NioSocketWrapper socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    public static class NioSocketWrapper {
        private NioChannel socket;
        private NioEndpoint endpoint;

        public NioSocketWrapper(NioChannel channel, NioEndpoint endpoint) {
            this.socket = channel;
            this.endpoint = endpoint;
        }

        public NioChannel getSocket() {
            return socket;
        }
    }

    protected class SocketProcessor implements Runnable {


        private NioSocketWrapper socketWrapper;
        private SocketEvent event;

        public SocketProcessor(NioSocketWrapper socketWrapper, SocketEvent event) {
            this.socketWrapper = socketWrapper;
            this.event = event;
        }


        @Override
        public void run() {
            NioChannel socket = socketWrapper.getSocket();
            SelectionKey key = socket.getIOChannel().keyFor(socket.getPoller().getSelector());

            SocketChannel socketChannel = (SocketChannel) key.channel();
            String request = readMessageFromChannel(socketChannel, key, socket.getPoller());
            System.out.println(request);

        }

        public String readMessageFromChannel(SocketChannel socketChannel, SelectionKey key, Poller poller) {
            String request = null;
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                request = "";
                while (readChannel(socketChannel, byteBuffer)) { // 将socketChannel的字节循环写入byteBuffer
                    byteBuffer.flip(); // 切换buffer为读模式
                    request += StandardCharsets.UTF_8.decode(byteBuffer);
                }

                try {
                    socketChannel.register(poller.getSelector(), SelectionKey.OP_READ, socketWrapper);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                poller.cancelledKey(key);
            }
            return request;
        }

        private boolean readChannel(SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException {
            int nRead = socketChannel.read(byteBuffer);
            if (nRead == -1) {
                throw new EOFException();
            }
            return nRead > 0;
        }


    }



    public static void main(String[] args) throws IOException {
        new NioEndpoint().start();
    }
}
