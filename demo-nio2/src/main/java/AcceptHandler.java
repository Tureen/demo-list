import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

// AcceptHandler类实现了CompletionHandler接口的completed方法. 它还有两个模板参数, 第一个是异步通道, 第二个是Nio2Server本身
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Nio2Server> {

    // 具体处理连接请求的就是completed方法, 它有两个参数: 第一个是异步通道, 第二个就是上面传入的Nio2Server对象
    @Override
    public void completed(AsynchronousSocketChannel asc, Nio2Server attachment) {
        // 调用accept方法继续接收其他客户端的请求
        attachment.assc.accept(attachment, this);

        //1. 先分配好Buffer, 告诉内核, 数据拷贝到哪里去
        ByteBuffer buf = ByteBuffer.allocate(1024);

//        channe
    }

    @Override
    public void failed(Throwable exc, Nio2Server attachment) {

    }
}
