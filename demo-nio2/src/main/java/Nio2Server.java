import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 步骤:
 * 1. 创建一个线程池, 这个线程池用来执行来自内核的回调请求
 * 2. 创建一个AsynchronousChannelGroup, 并绑定一个线程池
 * 3. 创建AsynchronousServerSocketChannel, 并绑定到AsynchronousChannelGroup
 * 4. 绑定一个监听端口
 * 5. 调用accept方法开始监听连接请求, 同时传入一个回调类处理连接请求. 其第一个参数是this, 就是Nio2Server对象本身
 *
 * 问题:
 * 1. 为什么需要创建一个线程池?
 * 为了提高处理速度, 提供线程池给内核使用, 内核只需要把工作交给线程池就立即返回
 */
public class Nio2Server {

    public AsynchronousServerSocketChannel assc;

    void listen() throws IOException {
        //1. 创建一个线程池
        ExecutorService es = Executors.newCachedThreadPool();

        //2. 创建异步通道群组
        AsynchronousChannelGroup tg = AsynchronousChannelGroup.withCachedThreadPool(es, 1);

        //3. 创建服务端异步通道
        assc = AsynchronousServerSocketChannel.open(tg);

        //4. 绑定监听端口
        assc.bind(new InetSocketAddress(8080));

        //5. 监听连接, 传入回调类处理连接请求
        assc.accept(this, new AcceptHandler());
    }
}
