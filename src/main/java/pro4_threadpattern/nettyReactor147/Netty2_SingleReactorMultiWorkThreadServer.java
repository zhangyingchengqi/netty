package pro4_threadpattern.nettyReactor147;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @program: netty
 * @description: 单reactor    工作线程多个
 * @author: zy
 * @create: 2025-04-13 15:17
 */
public class Netty2_SingleReactorMultiWorkThreadServer {
    //获取cpu核数，作为线程池的大小
    private static final int NTHREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) {
        EventLoopGroup reactorGroup = new NioEventLoopGroup(1);   //单reactor线程

        EventLoopGroup workerGroup = new NioEventLoopGroup(NTHREADS);   //多worker线程

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(reactorGroup, reactorGroup)    // 和accept  and IO   使用同一个线程组
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ByteToMessageDecoder() {
                                // in  入站  out 出站
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                    //Thread.sleep(  5000 );
                                    //将从通道中能读取到的字节全部读取出来，放入到out中
                                    out.add(in.readBytes(in.readableBytes()));   //这个处理是放在  reactor线程中执行的
                                }
                            }).addLast(workerGroup, new SimpleServerHandler());
                            //将业务处理类(耗时的操作)放到了worker线程组中执行.
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8888).sync();  // 绑定端口
            future.channel().closeFuture().sync();  // 关闭通道
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            reactorGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
