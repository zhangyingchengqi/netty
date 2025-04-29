package pro5_decoder_encoder.ex4_jsonSerialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JacksonEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
            throws Exception {

        ObjectMapper mapper = JacksonMapper.getInstance();
        byte[] body = mapper.writeValueAsBytes(msg); // 将对象转换为byte
        out.writeBytes(body); // 消息体中包含我们要发送的数据
    }

}
