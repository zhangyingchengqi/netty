package pro4_threadpattern.nettyReactor147;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @program: netty
 * @description: 单reactor单线程版本
 * @author: zy
 * @create: 2025-04-13 14:34
 */
public class Netty1_SingleReactorSingleThreadServer {
    public static void main(String[] args) {
        //netty线程组:  1个
        EventLoopGroup bossGroup = new NioEventLoopGroup(   1   );   // ***********单线程. 所以有的IO和业务都在同一个线程处理，阻塞会导致其他业务无法处理

        try {
            //服务器端的启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, bossGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(8888).sync();  // 绑定端口
            future.channel().closeFuture().sync();  // 关闭通道
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            bossGroup.shutdownGracefully();  //关闭线程组
        }
    }

    //处理类
    private static class SimpleServerHandler extends ChannelInboundHandlerAdapter {
        //事件处理的回调方法
        //读事件
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread.sleep(  5000 );
            ByteBuf in=(ByteBuf)msg;  //读到的数据
            System.out.println("服务端收到数据: "+in.toString(   CharsetUtil.UTF_8     ));
            ctx.writeAndFlush(  Unpooled.copiedBuffer("hello client\n", CharsetUtil.UTF_8)  );  // \n是用于给客户端确认处理完毕.
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
              cause.printStackTrace();
              ctx.close();
        }
    }
}
