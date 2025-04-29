package pro2_transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @program: netty
 * @description: 使用netty实现阻塞式tcp通信
 * @author: zy
 * @create: 2025-03-12 10:06
 */
public class P3_NettyOioServer {

    public static void main(String[] args) throws Exception {
        new P3_NettyOioServer().server(9999);
    }

    public void server(int port) throws Exception {
        // 准备数据,这个数据类型为ByteBuf, 它是netty的缓冲区, 它是一个引用计数的对象, 可以被重复读取,它的引用计数为0时, 它的内存会被释放
        // 在这个例子中, 我们使用了Unpooled类的unreleasableBuffer方法, 它会返回一个不可释放的缓冲区, 它的引用计数为1,
        // 当这个缓冲区被写入到通道时, 它的引用计数会减1, 当引用计数为0时, 它的内存会被释放
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi, 我是 基于 netty的NIO通信!\r\n", Charset.forName("UTF-8")));
        EventLoopGroup group = new OioEventLoopGroup();    // ************** 这是 关键 **************
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(OioServerSocketChannel.class)       // ************** 这是 关键 **************
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            // ch.pipeline()表示当前通道的处理器链, 它是一个双向链表, 它的头节点是HeadContext, 尾节点是TailContext
                            ch.pipeline().addLast(
                                    // ChannelInboundHandlerAdapter
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(
                                                ChannelHandlerContext ctx)
                                                throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                                    .addListener(
                                                            ChannelFutureListener.CLOSE); // ChannelFutureListener.CLOSE表示当通道关闭时, 释放缓冲区的内存
                                        }
                                    });
                        }
                    });
            // 绑定端口, 并等待绑定成功
            ChannelFuture f = b.bind().sync();

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
