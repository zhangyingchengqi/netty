package pro4_threadpattern.netty.v2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.List;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-04-11 09:52
 */
public class SingleReactorMultiWorkerServer {
    // 业务线程池大小 (CPU核心数×2)
    private static final int BUSINESS_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws InterruptedException {
        // Reactor线程 (单线程处理I/O)
        EventLoopGroup reactorGroup = new NioEventLoopGroup(1);

        // 业务线程池
        EventExecutorGroup workerGroup = new DefaultEventLoopGroup(BUSINESS_THREADS);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(reactorGroup, reactorGroup) // 注意：    I/O和accept使用同一个reactor
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // I/O处理仍在reactor线程
                                    .addLast(new ByteToMessageDecoder() {
                                        @Override
                                        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
                                            out.add(in.readBytes(in.readableBytes()));
                                        }
                                    })
                                    // 业务处理切换到worker线程
                                    .addLast(workerGroup, new SimpleServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.println("Server started with " + BUSINESS_THREADS + " worker threads");
            future.channel().closeFuture().sync();
        } finally {
            reactorGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 业务处理器
    private static class SimpleServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // 模拟业务处理耗时
            try {
                Thread.sleep(100); // 关键点3：阻塞会直接影响整个服务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ByteBuf in = (ByteBuf) msg;
            System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client", CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
