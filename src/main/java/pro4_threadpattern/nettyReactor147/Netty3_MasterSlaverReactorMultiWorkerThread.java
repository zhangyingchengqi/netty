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
 * @description: 主从reactor多工作线程机制
 * @author: zy
 * @create: 2025-04-13 15:45
 */
public class Netty3_MasterSlaverReactorMultiWorkerThread {

    private static final int MASTER_REACTOR_SIZE = 1;

    private static final int SLAVE_REACTOR_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int BUSINESS_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;


    public static void main(String[] args) {
        //主reactor线程组(处理连接请求)
        EventLoopGroup masterReactorGroup = new NioEventLoopGroup(MASTER_REACTOR_SIZE);
        //从reactor线程组(处理IO请求)
        EventLoopGroup slaveReactorGroup = new NioEventLoopGroup(SLAVE_REACTOR_SIZE);
        // 业务线程组(处理业务逻辑)
        EventLoopGroup businessGroup = new NioEventLoopGroup(BUSINESS_THREAD_SIZE);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(masterReactorGroup, slaveReactorGroup)  //****关键点:  主从reactor线程组
                    .channel(NioServerSocketChannel.class)
                    //主reactor配置项
                    .option(ChannelOption.SO_BACKLOG, 1024)    //联接队列的大小
                    .option(ChannelOption.SO_REUSEADDR, true)  //端口复用
                    //从reactor配置项
                    .childOption(ChannelOption.SO_KEEPALIVE, true)   //保持连接
                    .childOption(ChannelOption.TCP_NODELAY, true)    //关闭延迟发送


                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ByteToMessageDecoder() {
                                // in  入站  out 出站
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                    Thread.sleep(5000);
                                    //将从通道中能读取到的字节全部读取出来，放入到out中
                                    out.add(in.readBytes(in.readableBytes()));   //这个处理是放在  reactor线程中执行的
                                }
                            }).addLast(businessGroup, new SimpleServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(8888).sync();  // 绑定端口
            future.channel().closeFuture().sync();  // 关闭通道
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            masterReactorGroup.shutdownGracefully();
            slaveReactorGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
        }


    }

    //处理类
    private static class SimpleServerHandler extends ChannelInboundHandlerAdapter {
        //事件处理的回调方法
        //读事件
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread.sleep(5000);
            ByteBuf in = (ByteBuf) msg;  //读到的数据
            System.out.println("服务端收到数据: " + in.toString(CharsetUtil.UTF_8));
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello client\n", CharsetUtil.UTF_8));  // \n是用于给客户端确认处理完毕.
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
