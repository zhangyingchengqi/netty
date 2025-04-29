package pro4_threadpattern.netty.v3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-04-11 10:03
 */
public class MasterSlaveReactorServer {
    // 核心线程数配置
    private static final int BOSS_GROUP_SIZE = 1; // 通常1个线程足够处理连接接入
    private static final int WORKER_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BUSINESS_THREADS = 16; // 业务线程池大小

    public static void main(String[] args) throws InterruptedException {
        // 主Reactor线程组 (处理连接请求)
        EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE);
        // 从Reactor线程组 (处理I/O读写)
        EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER_GROUP_SIZE);
        // 业务线程池 (处理耗时操作)
        EventExecutorGroup businessGroup = new DefaultEventLoopGroup(BUSINESS_THREADS);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 主Reactor配置
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接队列大小
                    .option(ChannelOption.SO_REUSEADDR, true) // 端口重用
                    // 从Reactor配置
                    .childOption(ChannelOption.TCP_NODELAY, true) // 禁用Nagle算法
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 在worker线程执行的处理器
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            //pipeline.addLast(new FrameDecoder()); // 自定义拆包器
                            //pipeline.addLast(new MessageCodec()); // 编解码器

                            // 切换到业务线程池的处理器
                            pipeline.addLast(businessGroup, "businessHandler", new BusinessHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.printf("Server started with master(%d)-slave(%d)-business(%d) threads%n",
                    BOSS_GROUP_SIZE, WORKER_GROUP_SIZE, BUSINESS_THREADS);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
        }
    }

    // 业务处理器
    private static class BusinessHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

            // 模拟耗时业务处理
            TimeUnit.MILLISECONDS.sleep(50);

            ByteBuf in = (ByteBuf) msg;
            System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client\n", CharsetUtil.UTF_8));

        }


    }
}
