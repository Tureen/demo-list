package com.tulane.nio.base;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractNioSocket {

    public void beginAccpetorAndHandlerAllChannel(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        for (; ; ) {
            int readyChannels = selector.select(); // 获取可用channel数量(阻塞等待)
            if (readyChannels == 0) continue;

            Set<SelectionKey> selectionKeySet = selector.selectedKeys(); // 获取可用channel集合
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove(); // 移除Set中的当前selectionKey, 因为selectedKeys()每次返回原有集合

                handlerChannel(selectionKey, serverSocketChannel, selector); // 针对通道事件进行处理
            }
        }
    }

    public abstract void handlerChannel(SelectionKey selectionKey, ServerSocketChannel serverSocketChannel, Selector selector);
}
