package pro5_decoder_encoder.ex4_jsonSerialize;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-23 10:37
 */
public class JacksonServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        String jsonString = "";
        if (obj instanceof JacksonBean) {
            JacksonBean user = (JacksonBean) obj;
            ctx.writeAndFlush(user);
            jsonString = JacksonMapper.getInstance().writeValueAsString(user); // 对象转为json字符串
            System.out.println("Client -> Server: " + jsonString);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
