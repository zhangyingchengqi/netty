package pro2_transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: netty
 * @description: NIO版服务器 (非阻塞）
 * @author: zy
 * @create: 2025-03-12 10:00
 */
public class P2_PlainNioServer {

    public static void main(String[] args) throws IOException {
        new P2_PlainNioServer().serve(9999);
    }

    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);

        Selector selector = Selector.open();  // 打开Selector，用于监听通道的事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);   //这个channel只是监听accept事件 当服务器套接字通道（ServerSocketChannel）准备好接受新的连接时。

        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
        for (;;){
            try {
                selector.select();    // 阻塞，直到有事件就绪
            } catch (IOException ex) {
                ex.printStackTrace();
                //handle exception
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys(); // 获取就绪的事件

            Iterator<SelectionKey> iterator = readyKeys.iterator();         // 遍历就绪的事件
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {    // 接收客户端的连接请求, 并将连接的客户端注册到Selector中, 用于监听客户端的读写事件
                        ServerSocketChannel server =
                                (ServerSocketChannel) key.channel();   // 获取ServerSocketChannel对象, 用于接收客户端的连接请求
                        SocketChannel client = server.accept();  // 接收客户端的连接请求
                        client.configureBlocking(false);   // 设置客户端的SocketChannel为非阻塞模式
                        client.register(selector, SelectionKey.OP_WRITE |
                                SelectionKey.OP_READ, msg.duplicate());    // 注册客户端的SocketChannel到Selector中, 用于监听客户端的读写事件
                        System.out.println(
                                "接收来自客户端: " + client +"的联接");
                    }

                    if (key.isReadable()) {
                        SocketChannel client =
                                (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        client.read(buffer);
                    }
                    if (key.isWritable()) {
                        SocketChannel client =
                                (SocketChannel) key.channel();
                        ByteBuffer buffer =
                                (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        // ignore on close
                    }
                }
            }
        }
    }
}

/*
 以上采用NIO的分发模型 ，特点:
  1. 将一个完整处理过程分解为多个任务.
  2. 每个任务的动作不产生阻塞
  3. 在任务执行状态被 触发时，才会执行任务.通知 机制.

 */
