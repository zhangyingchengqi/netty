package pro2_transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @program: netty
 * @description: channel接口写出数据案例
 * @author: zy
 * @create: 2025-03-12 11:15
 */
public class P5_NettyChannelOperation {

    private static Channel channel;

    public static void main(String[] args) throws InterruptedException {
        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建 Bootstrap 并初始化
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)  // 绑定线程组, 用于处理网络事件,包括连接建立、数据传输等
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            // 可以在这里添加更多的 Handler
                        }
                    });

            // 连接到服务器 (假设服务器在本地的 8999 端口)
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8999).sync();
            channel = future.channel(); // 获取已连接的 Channel

            // 调用写入方法
            //writingToChannel();   // 单线程使用同一个channel写入数据
            writingToChannelFromManyThreads();  //多线程使用同一个channel写入数据

            // 等待通道关闭
            channel.closeFuture().sync();
        } finally {
            // 关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     *  Writing to a Channel
     */
    public static void writingToChannel() {
        if (channel == null || !channel.isActive()) {
            System.err.println("Channel is not active, cannot write data.");
            return;
        }

        ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
        ChannelFuture cf = channel.writeAndFlush(buf);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    System.out.println("Write successful");
                } else {
                    System.err.println("Write error");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    /**
     *  Using a Channel from many threads
     */
    public static void writingToChannelFromManyThreads() {
        if (channel == null || !channel.isActive()) {
            System.err.println("Channel is not active, cannot write data.");
            return;
        }

        final ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);

        Runnable writer = () -> {
            ByteBuf duplicateBuf = buf.retainedDuplicate(); // 创建新的引用计数对象
            channel.writeAndFlush(duplicateBuf).addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("Write successful from thread: " + Thread.currentThread().getName());
                } else {
                    System.err.println("Write error from thread: " + Thread.currentThread().getName());
                    future.cause().printStackTrace();
                }
            });
        };

        // 使用 Netty 自己的 EventLoop 执行任务，避免额外的线程池
        channel.eventLoop().execute(writer);
        channel.eventLoop().execute(writer);
    }

}
