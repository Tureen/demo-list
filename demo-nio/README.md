[toc]

# Java NIO
* Channel: 通道
* Buffer: 缓冲区
* Selector: 选择器 或 多路复用器

## Channel
* 双向性: 合并write与read功能
* 非阻塞性
* 操作唯一性: 基于数据块操作, 只能通过buffer操作

### Channel实现
* 文件类: FileChannel
* UDP类: DatagramChannel
* TCP类: ServerSocketChannel / SocketChannel

### Channel与BIO中Socket对比

#### BIO Socket

##### 服务端
```java
// 监听端口
ServerSocket serverSocket = new ServerSocket(8000);

while(true){
    // 接收请求, 建立连接
    Socket socket = serverSocket.accept();

    // 数据交换
    new Thread(new BIOServerHandler(socket)).start();
}

// 关闭资源
serverSocket.close();
```

##### 客户端
```java
// 建立连接
Socket socket = new Socket("127.0.0.1", 8000);

// 获取输入输出流
InputStream inputStream = socket.getInputStream();
OutputStream outputStream = socket.getOutputStream();
```

#### NIO Channel使用

##### 服务端
```java
// 通过服务端socket创建channel
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

// 绑定端口
serverSocketChannel.bind(new InetSocketAddress(8000))_;

// 监听客户端连接, 建立socketChannel连接 (与BIO中的socket是一样的, 是服务端与客户端之间建立的通道)
SocketChannel socketChannel = serverSocketChannel.accept();
```

##### 客户端
```java
// 连接远程主机及端口
SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
```

## Buffer
* 作用: 读写Channel中数据 (只有Buffer可以操作Channel)
* 本质: 一块内存区域

### Buffer属性
* Capacity: 容量 (一次写入最大字节, 超过需先清空)
* Position: 位置 (当前写入或读取的位置, 最大为:容量-1, 读写转换时都会重置为0)
* Limit: 上限 (写模式下等于Capacity, 读模式下, 等于Position写模式下的值)
* Mark: 标记 (存储Position位置, 用于恢复位置, 继续处理数据)

### Buffer使用
```java
/**
 * 初始化长度为10的byte类型的buffer
 * position:0    limit(写):10    capacity:10
 */
ByteBuffer.allocate(10);

/**
 * 向byteBuffer写入三个字节
 * position:3    limit(写):10    capacity:10
 */
byteBuffer.put("abc".getBytes(Charset.forName("UTF-8")));

/**
 * 将byteBuffer从写模式切换成读模式
 * position:0    limit(读):3    capacity:10
 */
byteBuffer.flip();

/**
 * 从byteBuffer中读取一个字节
 * position:1    limit(读):3    capacity:10
 */
byteBuffer.get();

/**
 * 调用mark方法记录下当前position的位置
 * position:1    limit(读):3    capacity:10    mark:1
 */
byteBuffer.mark();

/**
 * 先调用get方法读取下一个字节
 * position:2    limit(读):3    capacity:10    mark:1
 */
byteBuffer.get();

/**
 * 再调用reset方法将position重置到mark位置
 * position:1    limit(读):3    capacity:10    mark:1
 */
byteBuffer.reset();

/**
 * 调用clear方法, 将所有属性重置
 * position:0    limit(写):10    capacity:10
 */
byteBuffer.clear();
```

## Selector
* 作用: I/O就绪选择
* 地位: NIO网络编程的基础 (通过Selector, 可以管理多个Channel, 从而管理多个网络连接)

### Selector使用
```java
// 创建Selector
Selector selector = Selector.open();

// 将channel注册到selector上, 监听读就绪事件
SelectionKey selectionKey = channel.register(selector, SelectionKey.OPEN_READ);

// 阻塞等待channel有就绪事件发生
int selectNum = selector.select();

// 获取发生就绪事件的channel集合
Set<SelectionKey> selectedKeys = selectors.selectedKeys();
```

### SelectionKey简介
* 四种就绪状态常量
* 有价值的属性

## NIO编程实现步骤
1. 创建Selector
2. 创建ServerSocketChannel, 并绑定监听端口
3. **将Channel设置为非阻塞模式**
4. 将Channel注册到Selector上, 监听连接事件
5. 循环调用Selector的select方法, 检测就绪情况
6. 调用selectedKeys方法获取就绪channel集合
7. 判断就绪事件种类, 调用业务处理方法
8. 根据业务需要决定是否再次注册监听事件, 重复执行第三步操作