package com.tulane.nio.base;


import com.tulane.nio.base.handler.AcceptHandler;
import com.tulane.nio.base.handler.ReadHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * NIO服务器端
 */
public class ServerNioSocket extends AbstractNioSocket {

    /**
     * 启动:
     * 1. 创建Selector
     * 2. 通过ServerSocketChannel创建channel通道
     * 3. 为channel通道绑定监听端口
     * 4. 设置channel为非阻塞模式
     * 5. 将channel注册到selector上, 监听连接事件
     * 6. 循环等待新接入的连接
     * 7. 根据就绪状态, 调用对应方法处理业务逻辑
     */
    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = getServerSocketChannel();

        registerSocketChannel(selector, serverSocketChannel);

        beginAccpetorAndHandlerAllChannel(serverSocketChannel, selector);
    }

    public ServerSocketChannel getServerSocketChannel() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8000));
        serverSocketChannel.configureBlocking(false);
        return serverSocketChannel;
    }

    public void registerSocketChannel(Selector selector, ServerSocketChannel serverSocketChannel){
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        System.out.println("服务器启动成功");
    }

    @Override
    public void handlerChannel(SelectionKey selectionKey, ServerSocketChannel serverSocketChannel, Selector selector){
        try {
            // 接入事件
            if (selectionKey.isAcceptable()) {
                AcceptHandler.acceptHandler(serverSocketChannel, selector);
            }

            // 可读事件
            if (selectionKey.isReadable()) {
                ReadHandler.readHandler(selectionKey, selector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
