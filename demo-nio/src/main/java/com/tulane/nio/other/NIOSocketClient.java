package com.tulane.nio.other;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NIOSocketClient {

    public static void main(String[] args) throws IOException {
        new NIOSocketClient().start();
    }

    public void start() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 连接到服务端, 此时客户端会发送SYN到服务端, 服务端收到SYN后, 会把SYN和ACK一并发送给客户端
        // 客户端收到后, 返回ACK给服务端, 然后完成3次握手
        // (如果SocketChannel为阻塞模式, 执行完这一句代码, 客户端和服务端的3次握手已经完成)
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));
        // 因为SocketChannel设置为非阻塞模式, 所有执行下面语句的时候可能还没有连接成功, 所以循环检查是否已经连接成功
        while (!socketChannel.finishConnect()) {
            System.out.println("等待连接完成...");
        }
        // 代码执行到这里, 证明客户端和服务端已连接成功! 完成3次握手!
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(StandardCharsets.UTF_8.encode("A"));
        buffer.flip();
        // 向服务器发送1个字节的数据
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
        // 关闭客户端的写出通道, 此时执行完这句客户端会发送FIN到服务端, 并且服务端会返回ACK确认字符
        socketChannel.socket().shutdownOutput();
        buffer.clear();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 循环读取服务端发送回来的数据
        while(socketChannel.read(buffer)!= -1){
            buffer.flip();
            outputStream.write(buffer.array(), 0, buffer.limit());
            buffer.clear();
        }
        System.out.println(new String(outputStream.toByteArray(), 0, outputStream.size(), StandardCharsets.UTF_8));
        socketChannel.shutdownInput();
        socketChannel.close();
        socketChannel.socket().close();
    }
}
