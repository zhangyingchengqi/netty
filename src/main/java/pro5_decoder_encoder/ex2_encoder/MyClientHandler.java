package pro5_decoder_encoder.ex2_encoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pro5_decoder_encoder.ex2_encoder.Msg;

public class MyClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        Channel incoming = ctx.channel();
        if (obj instanceof Msg) {
            Msg msg = (Msg) obj;
            System.out.println("服务器->客户端:" + incoming.remoteAddress() + msg.getBody());
        } else {
            System.out.println("服务器->客户端:" + incoming.remoteAddress() + obj.toString());
        }
    }

}
