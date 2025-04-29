package pro1_echoServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;


public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port=8999;
        if (args.length==1) {
             port=Integer.parseInt(args[0]);
        }
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        //服务器处理程序
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建EventLoopGroup对象, 用于处理I/O操作的多线程事件循环
        EventLoopGroup group = new NioEventLoopGroup();   //

        try {
            // 创建ServerBootstrap对象, 用于启动服务器
            ServerBootstrap b = new ServerBootstrap();  // ServerSocket
            b.group(group)
                   .channel(   NioServerSocketChannel.class)  // 选择NIO传输channel类型, 用于异步服务器套接字通道的实现, 其它类型还有如: OioServerSocketChannel, 用于OIO传输channel类型, 用于异步服务器套接字通道的实现
                   .localAddress(     new InetSocketAddress( port) )
                   .childHandler(   new ChannelInitializer<SocketChannel>(){  //指定一个EchoServerHandler到子channel的ChannelPipeline中, 用于处理I/O操作
                     @Override
                     public void initChannel(SocketChannel ch) throws Exception {
                         ch.pipeline().addLast( "logging", new LoggingHandler(LogLevel.DEBUG)); // 增加一个日志处理的 handler
                         ch.pipeline().addLast(serverHandler);//因为 serverHandler被标注为@Sharable, 所以我们总是使用同样的实例

                     }
                   });
            ChannelFuture f=b.bind().sync();   // 绑定服务器, 调用sync()方法阻塞, 直到服务器绑定完成


            f.channel().closeFuture().sync();  // 阻塞, 直到服务器关闭
        } finally {
            group.shutdownGracefully().sync(); // 关闭EventLoopGroup, 释放所有资源
        }
    }
}
