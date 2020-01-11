package club.tulane.tomcat.simple;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioEndpoint {

    private ServerSocketChannel serverSocket = null;
    private Acceptor acceptor = null;
    private Poller poller = null;

    public void start() throws IOException {

        // 创建服务器channel, 设置阻塞模式
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(true);

        // 开启Poller, 使用IO多路复用器监听读请求
        poller = new Poller();
        new Thread(poller).start();

        // 开启Accetor监听连接请求
        acceptor = new Acceptor();
        new Thread(acceptor).start();

        // 主线程不结束
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Acceptor implements Runnable {

        @Override
        public void run() {
            while(true){
                try {
                    SocketChannel socketChannel = serverSocket.accept();
                    // 获取客户机channel, 注册只读事件并设置非阻塞
                    socketChannel.configureBlocking(false);
                    socketChannel.register(poller.getSelector(), SelectionKey.OP_READ);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Poller implements Runnable{

        private Selector selector;
        private long selectorTimeout = 1000;

        public Selector getSelector() {
            return selector;
        }

        public Poller() throws IOException {
            this.selector = Selector.open();
        }

        @Override
        public void run() {
            while(true){
                try {
                    int keyCount = selector.select(selectorTimeout);
                    if(keyCount == 0) continue;

                    // 获取触发了已注册事件的channel通道
                    // 将channel传递给SocketProcessor处理
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while(iterator.hasNext()){
                        SelectionKey sk = iterator.next();
                        iterator.remove();
                        processKey(sk);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processKey(SelectionKey sk) {
            if(sk.isReadable()){
                SocketProcessor sc = new SocketProcessor(sk);
//                sc.run();
                new Thread(sc).start();
            }
        }

        public void cancelledKey(SelectionKey key) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                socketChannel.socket().close();
                socketChannel.close();
            } catch (IOException e) {
                System.out.println("endpoint.debug.socketCloseFail");
            }
        }
    }

    public class SocketProcessor implements Runnable{

        private SelectionKey key;

        public SocketProcessor(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            String message = readMessageFromChannel(socketChannel);
            System.out.println("客户端:" + message);
        }

        private String readMessageFromChannel(SocketChannel socketChannel) {
            StringBuilder request = new StringBuilder();
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                request = new StringBuilder();
                while (readChannel(socketChannel, byteBuffer)) { // 将socketChannel的字节循环写入byteBuffer
                    byteBuffer.flip(); // 切换buffer为读模式
                    request.append(StandardCharsets.UTF_8.decode(byteBuffer));
                }
                try {
                    socketChannel.register(poller.getSelector(), SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
            catch (IOException e) {
                poller.cancelledKey(key);
            }
            return request.toString();
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
