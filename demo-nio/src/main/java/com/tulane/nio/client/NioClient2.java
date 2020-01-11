package com.tulane.nio.client;

import com.tulane.nio.base.ClientNioSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * NIO客户端
 */
public class NioClient2 {

    /**
     * 启动
     * 1. 连接服务器端
     * 2. 接收服务器端响应
     * 3. 向服务器端发送数据
     */
    public void start() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));

        readMessageFromServer(socketChannel);
        sendMessageToServer(socketChannel);
    }

    private void readMessageFromServer(SocketChannel socketChannel) {
        new Thread(() -> {
            try {
                new ClientNioSocket(socketChannel).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessageToServer(SocketChannel socketChannel) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String request = scanner.nextLine();
            if(request != null && request.length() > 0){
                socketChannel.write(StandardCharsets.UTF_8.encode(request));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioClient2().start();
    }
}
