package com.tulane.nio.server;

import com.tulane.nio.base.ServerNioSocket;

import java.io.IOException;

public class NioServer {

    public static void main(String[] args) throws IOException {
        new ServerNioSocket().start();
    }
}
