package pro2_transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @program: netty
 * @description: 基于netty的Nio通信
 * @author: zy
 * @create: 2025-03-12 10:09
 */
public class P4_NettyNioServer {

    public static void main(String[] args) throws Exception {
        new P4_NettyNioServer().server(9999);
    }

    public void server(int port) throws Exception {
        final ByteBuf buf =
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",
                        Charset.forName("UTF-8")));


        NioEventLoopGroup group = new NioEventLoopGroup();    // ***********关键的是NioEventLoopGroup，它是处理I/O操作的线程池
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(group).channel(NioServerSocketChannel.class)      // ******关键的是NioEventLoopGroup，它是处理I/O操作的线程池
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch)
                                              throws Exception {
                                          ch.pipeline().addLast(
                                                  new ChannelInboundHandlerAdapter() {
                                                      @Override
                                                      public void channelActive(
                                                              ChannelHandlerContext ctx) throws Exception {
                                                          ctx.writeAndFlush(buf.duplicate())
                                                                  .addListener(
                                                                          ChannelFutureListener.CLOSE);
                                                      }
                                                  });
                                      }
                                  }
                    );
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
