package pro4_threadpattern.netty.v1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-04-11 09:29
 */
public class SingleThreadNettyServer {
    public static void main(String[] args) {
        // 创建单个EventLoop的EventLoopGroup
        EventLoopGroup singleGroup = new NioEventLoopGroup(1); // 关键点1：单线程  所有I/O和业务由同一线程处理  阻塞会直接影响整个服务
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(singleGroup, singleGroup) // 关键点2：boss和worker使用同一个单线程组
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new SimpleServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.println("Server started on port 8080 (Single Thread Mode)");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            singleGroup.shutdownGracefully();
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
