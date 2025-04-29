package pro5_decoder_encoder.ex3_objectSerialize;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-23 10:26
 */
public class SerializationServer {
    static final int PORT = 8082;

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100) // 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                    .handler(new LoggingHandler(LogLevel.INFO)) // 记录日志
                    .childHandler(new SerializationServerInitializer());

            ChannelFuture f = b.bind(PORT).sync();

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
