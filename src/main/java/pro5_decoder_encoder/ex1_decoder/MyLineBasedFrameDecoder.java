package pro5_decoder_encoder.ex1_decoder;

import io.netty.handler.codec.LineBasedFrameDecoder;


//LineBasedFrameDecoder是一个基于换行符的解码器,它会将接收到的数据按照换行符进行拆分,然后返回拆分后的帧.
public class MyLineBasedFrameDecoder extends LineBasedFrameDecoder {

    private final static int MAX_LENGTH = 1024; // 解码器帧的最大长度,超过这个长度的数据会抛出 TooLongFrameException.

    public MyLineBasedFrameDecoder() {
        super(MAX_LENGTH);
    }

}
