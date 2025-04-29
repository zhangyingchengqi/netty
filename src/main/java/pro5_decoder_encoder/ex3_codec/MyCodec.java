package pro5_decoder_encoder.ex3_codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import pro5_decoder_encoder.ex2_encoder.MyDecoder;
import pro5_decoder_encoder.ex2_encoder.MyEncoder;

/**
 * @program: netty
 * @description: 对自定义的编码器与解码器进行组合
 * @author: zy
 * @create: 2025-03-23 09:21
 */
public class MyCodec extends CombinedChannelDuplexHandler<MyDecoder, MyEncoder> {
    public MyCodec() {
        super(new MyDecoder(), new MyEncoder());
    }

}
