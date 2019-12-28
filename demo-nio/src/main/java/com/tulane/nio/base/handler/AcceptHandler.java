package com.tulane.nio.base.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class AcceptHandler {
    /**
     * 接入事件处理器
     * 1. 创建socketChannel, 与客户端建立连接
     * 2. 将socketChannel设置为非阻塞工作模式
     * 3. 将channel注册到selector上, 监听 可读事件
     * 4. 回复客户端提示信息
     */
    public static void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(StandardCharsets.UTF_8.encode("你与聊天室里其他人都不是朋友关系, 请注意隐私安全"));
    }
}
