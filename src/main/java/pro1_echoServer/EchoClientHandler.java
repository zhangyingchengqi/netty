package pro1_echoServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


@ChannelHandler.Sharable // 标识该类的实例可以被多个Channel共享
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    // SimpleChannelInboundHandler 与服务端用的 ChannelInboundHandler的区别:
    // 1. 它的实现简化了channelRead()方法, 因为它的实现中, 你不需要调用ChannelHandlerContext的read()方法来读取数据, 因为它已经被自动读取了.
    // 2. 它的实现简化了channelReadComplete()方法, 因为它的实现中, 你不需要调用ChannelHandlerContext的flush()方法来刷新出站数据, 因为它已经被自动刷新了.
    // 3. 它的实现简化了exceptionCaught()方法, 因为它的实现中, 你不需要调用ChannelHandlerContext的close()方法来关闭连接, 因为它已经被自动关闭了.

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));  // 当被通知channel是活跃的时候, 发送一条消息
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //每当接收到数据时, 都会调用channelRead0()方法，
        // 注意:由服务器发送的消息可能会被分块接收. 为了确保消息的完整性, 你应该将接收到的数据保存到一个缓冲区中, 直到缓冲区中包含了完整的消息.
        System.out.println("客户端收到消息: "+  msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
