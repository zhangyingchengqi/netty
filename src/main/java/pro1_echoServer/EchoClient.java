package pro1_echoServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-12 09:14
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port=8999;
        String host="localhost";
        if (args.length== 2) {
             host = args[0];
             port = Integer.parseInt(args[1]);
        }
        new EchoClient(host, port).start();
    }

    public void start() throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();  // 创建Bootstrap对象, 用于启动客户端     Socket
            b.group(group)   // 绑定EventLoopGroup对象, 用于处理I/O操作的多线程事件循环
                   .channel(NioSocketChannel.class)  // 选择NIO传输channel类型, 用于异步客户端套接字通道的实现, 其它类型还有如: OioSocketChannel, 用于OIO传输channel类型, 用于异步客户端套接字通道的实现
                   .remoteAddress(new InetSocketAddress(host, port))

                   .handler(new ChannelInitializer<SocketChannel>() {  // 指定一个EchoClientHandler到子channel的ChannelPipeline中, 用于处理I/O操作
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // Future就是  Promise对象的一种实现
            ChannelFuture f = b.connect().sync();  // 连接服务器, 调用sync()方法阻塞, 直到连接完成
            f.channel().closeFuture().sync();    // 阻塞, 直到连接关闭
        } finally {
            group.shutdownGracefully().sync();  // 关闭EventLoopGroup, 释放所有资源
        }
    }
}
