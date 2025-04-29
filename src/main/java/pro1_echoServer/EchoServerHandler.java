package pro1_echoServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable // 标识一个 ChannelHandler 可以被多个 Channel 安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // 读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //字节缓冲区(    1个英文=1个字节,    gbk/gb2312=2个字节,    utf-8=3个字节 )
        ByteBuf in=(ByteBuf)msg;
        System.out.println("服务器收到消息: "+    in.toString(CharsetUtil.UTF_8));
        // Echo就是回声筒
        ctx.write(   in  ); // 把接收到的消息写给发送者，而不冲刷出站消息
    }

    // 通知 ChannelInboundHandler 最后一次对 channelRead() 的调用是当前批量读取中的最后一条消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(   Unpooled.EMPTY_BUFFER  ).addListener(ChannelFutureListener.CLOSE); // 冲刷所有待审消息到远程节点。关闭通道后，操作完成
    }

    // 处理异常，释放资源
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
