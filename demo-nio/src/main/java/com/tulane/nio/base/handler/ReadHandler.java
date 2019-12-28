package com.tulane.nio.base.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ReadHandler {
    /**
     * 可读事件处理器
     * 1. 从selectKey中获取到已经就绪的channel
     * 2. 创建buffer
     * 3. 循环读取客户端请求信息
     * 4. 将channel再次注册到selector上, 监听他的可读事件
     * 5. 将客户端发送的请求信息, 广播给其他客户端
     */
    public static void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String request = readMessageFromChannel(socketChannel);
        socketChannel.register(selector, SelectionKey.OP_READ);

        broadToClients(request);
    }

    public static String readMessageFromChannel(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) { // 将socketChannel的字节循环写入byteBuffer
            byteBuffer.flip(); // 切换buffer为读模式
            request += StandardCharsets.UTF_8.decode(byteBuffer);
        }
        return request;
    }

    public static void broadToClients(String request) {
        if (request.length() > 0) {
            System.out.println(":: " + request);
        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector,
                           SocketChannel sourceChannel, String request) {
        /**
         * 获取到所有已接入的客户端channel
         */
        Set<SelectionKey> selectionKeySet = selector.keys();

        /**
         * 循环向所有channel广播信息
         */
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();

            // 剔除发消息的客户端
            if (targetChannel instanceof SocketChannel
                    && targetChannel != sourceChannel) {
                try {
                    // 将信息发送到targetChannel客户端
                    ((SocketChannel) targetChannel).write(
                            StandardCharsets.UTF_8.encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
