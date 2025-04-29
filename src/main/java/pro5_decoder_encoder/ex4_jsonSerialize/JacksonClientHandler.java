package pro5_decoder_encoder.ex4_jsonSerialize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-23 10:40
 */
public class JacksonClientHandler  extends
        SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        String jsonString = "";
        if (obj instanceof JacksonBean) {
            JacksonBean user = (JacksonBean) obj;
            jsonString = JacksonMapper.getInstance().writeValueAsString(user); // 对象转为json字符串
            System.out.println("Server -> Client: " + jsonString);
        }
    }

}
