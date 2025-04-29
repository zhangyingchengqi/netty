package pro5_decoder_encoder.ex4_jsonSerialize;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * @program: netty
 * @description:
 * @author: zy
 * @create: 2025-03-23 10:41
 */
public class JacksonClientInitializer extends
        ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JacksonDecoder<JacksonBean>(JacksonBean.class));
        pipeline.addLast(new JacksonEncoder());
        pipeline.addLast(new JacksonClientHandler());
    }
}
