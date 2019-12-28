package com.tulane.nio.base;

import com.tulane.nio.base.handler.ReadHandler;

import java.io.IOException;
import java.nio.channels.*;

public class ClientNioSocket extends AbstractNioSocket {

    private SocketChannel socketChannel;

    public ClientNioSocket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        try {
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("开始接收服务端信息");
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        beginAccpetorAndHandlerAllChannel(null, selector);
    }

    @Override
    public void handlerChannel(SelectionKey selectionKey, ServerSocketChannel serverSocketChannel, Selector selector) {
        try {
            if(selectionKey.isReadable()){
                ReadHandler.readHandler(selectionKey, selector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
