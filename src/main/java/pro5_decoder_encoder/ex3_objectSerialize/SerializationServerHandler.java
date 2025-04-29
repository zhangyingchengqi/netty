package pro5_decoder_encoder.ex3_objectSerialize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-23 10:23
 */
public class SerializationServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof SerializationBean) {
            SerializationBean user = (SerializationBean) obj;
            ctx.writeAndFlush(user);
            System.out.println("客户端 -> 服务器: " + user);
        }
    }

}
