package pro7_protocol.ex1_47_httpServer;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Logger;

/**
 * @program: netty
 * @description: 基于netty的http服务器
 * @author: zy
 * @create: 2025-04-19 18:59
 */
public class HttpServer {
    public static int DEFAULT_PORT=8080;

    private static final int MASTER_REACTOR_SIZE = 1;

    private static final int SLAVE_REACTOR_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int BUSINESS_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    //日志
    private static Logger logger= Logger.getLogger(HttpServer.class.getName());


    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        }catch(Exception ex){
            port = DEFAULT_PORT;
        }

        //主reactor线程组(处理连接请求)
        EventLoopGroup masterReactorGroup = new NioEventLoopGroup(MASTER_REACTOR_SIZE);
        //从reactor线程组(处理IO请求)
        EventLoopGroup slaveReactorGroup = new NioEventLoopGroup(SLAVE_REACTOR_SIZE);
        // 业务线程组(处理业务逻辑)
        EventLoopGroup businessGroup = new NioEventLoopGroup(BUSINESS_THREAD_SIZE);

        try {
            //启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(masterReactorGroup, slaveReactorGroup)  //****关键点:  主从reactor线程组
                    .channel(NioServerSocketChannel.class)  //channel通道 类型    NioServerSocketChannel  基于nio的通道
                    //主reactor配置项
                    .option(ChannelOption.SO_BACKLOG, 1024)    //联接队列的大小
                    .option(ChannelOption.SO_REUSEADDR, true)  //端口复用
                    //从reactor配置项
                    .childOption(ChannelOption.SO_KEEPALIVE, true)   //保持连接
                    .childOption(ChannelOption.TCP_NODELAY, true)    //关闭延迟发送


                    .childHandler(        new HttpServerChannelHandler(  businessGroup    )  );

            ChannelFuture future = serverBootstrap.bind(port).sync();  // 绑定端口

            logger.info("http server 启动，监听端口:" + port);

            future.channel().closeFuture().sync();  // 关闭通道
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            masterReactorGroup.shutdownGracefully();
            slaveReactorGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
        }



    }
}
