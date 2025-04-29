package pro5_decoder_encoder.ex1_decoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @program: netty
 * @description: MyLineBasedFrameDecoderServer
 * @author: zy
 * @create: 2025-03-22 16:47
 */
public class MyLineBasedFrameDecoderServer {
    public static int DEFAULT_PORT = 8023;

    private static final int MASTER_REACTOR_SIZE = 1;

    private static final int SLAVE_REACTOR_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int BUSINESS_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;

        // 多线程事件循环器，一个用于处理客户端连接，另一个用于处理网络读写操作
        //主reactor线程组(处理连接请求)
        EventLoopGroup masterReactorGroup = new NioEventLoopGroup(MASTER_REACTOR_SIZE);
        //从reactor线程组(处理IO请求)
        EventLoopGroup slaveReactorGroup = new NioEventLoopGroup(SLAVE_REACTOR_SIZE);

        // 业务线程组(处理业务逻辑)
        EventLoopGroup businessGroup = new NioEventLoopGroup(BUSINESS_THREAD_SIZE);

        try {
            // 启动NIO服务的引导程序类
            ServerBootstrap b = new ServerBootstrap();

            b.group(masterReactorGroup, slaveReactorGroup) // 设置EventLoopGroup
                    .channel(NioServerSocketChannel.class) // 指定ServerChannel的实现类，这里使用NIO的实现类
                    //主reactor配置项
                    .option(ChannelOption.SO_BACKLOG, 1024)    //联接队列的大小
                    .option(ChannelOption.SO_REUSEADDR, true)  //端口复用
                    //从reactor配置项
                    .childOption(ChannelOption.SO_KEEPALIVE, true)   //保持连接
                    .childOption(ChannelOption.TCP_NODELAY, true)    //关闭延迟发送

                    .childHandler(new MyLineBasedFrameDecoderChannelInitializer(  businessGroup    )); // 指定ChannelHandler链


            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync();

            System.out.println("MyLineBasedFrameDecoderServer已启动，端口：" + port);

            // 等待服务器 socket 关闭 。
            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
            f.channel().closeFuture().sync();
        } finally {

            // 优雅的关闭
            masterReactorGroup.shutdownGracefully();
            slaveReactorGroup.shutdownGracefully();
        }

    }
}
