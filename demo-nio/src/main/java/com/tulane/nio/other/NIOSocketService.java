package com.tulane.nio.other;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NIOSocketService {

    public static void main(String[] args) throws IOException {
        new NIOSocketService().start();
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        // 设置serverSocketChannel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        // 将serverSocketChannel注册到selector, 并设置对连接事件感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 非阻塞式查询selector是否有准备好的读、写、连接事件
            int select = selector.selectNow();
            // 如果返回大于0, 则有准备好的读、写、连接事件
            if (select <= 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isReadable()) { // 如果事件为准备好可读
                    System.out.println("readable");
                    // 处理读事件的handler
                    readHandler(selectionKey);
                } else if (selectionKey.isAcceptable()) { // 如果事件为准备好连接
                    ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
                    // 同意连接, 返回一个SocketChannel
                    SocketChannel accept = ssc.accept();
                    System.out.println("有连接进来! " + accept.getRemoteAddress());
                    // 将SocketChannel也设置为非阻塞模式
                    accept.configureBlocking(false);
                    // 注册到selector, 并设置对读感兴趣
                    accept.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }

    public void readHandler(SelectionKey selectionKey) {
        try (
                SocketChannel channel = (SocketChannel) selectionKey.channel()
        ) {
            ByteBuffer buffer = ByteBuffer.allocate(64);
            StringBuilder stringBuilder = new StringBuilder();
            while (channel.read(buffer) != -1) {
                buffer.flip();
                stringBuilder.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
            }
            System.out.println(stringBuilder.toString());
            // 组织服务发送给客户端的数据
            String re = new String("我是服务器, 我已经收到你的消息了!");
            buffer.put(StandardCharsets.UTF_8.encode(re));
            // 将buffer设置为写模式
            buffer.flip();
            // 写入SocketChannel通道
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            // 关闭服务器的写出通道, 此时服务端会发送FIN到客户端, 客户端收到后, 会返回ACK确认字符
            channel.socket().shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
