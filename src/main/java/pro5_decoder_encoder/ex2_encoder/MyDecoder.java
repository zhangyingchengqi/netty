package pro5_decoder_encoder.ex2_encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * MyDecoder集成自LengthFieldBasedFrameDecoder。 基于长度的解码器. 在数据包中加入了一个长度字段，保存上层包的长度.解码时，按这个长度进行上层ByteBuf应用包的提取.
 */
public class MyDecoder extends LengthFieldBasedFrameDecoder {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  // 最大帧长度
    private static final int LENGTH_FIELD_LENGTH = 4;         // 长度字段的字节数长度
    private static final int LENGTH_FIELD_OFFSET = 1;         //长度域偏移量，指的是长度域位于整个数据包字节数组中的下标
    private static final int LENGTH_ADJUSTMENT = 0;          //长度域的偏移量矫正。如果长度域的值，除了包含有效数据域的长度外，还包含了其他域长度，就需要进行娇正。矫正的值为包长-长度域的值-长度域偏移-长度域长.
    private static final int INITIAL_BYTES_TO_STRIP = 0;    //丢弃的起始字节数。丢弃处于有效数据前面的字节数量.

    private static final int HEADER_SIZE = 5;
    private byte msgType; // 消息类型
    private int len; // 长度

    public MyDecoder() {
        super(MAX_FRAME_LENGTH,
                LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH,
                LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
    }

    @Override
    protected Msg decode(ChannelHandlerContext ctx, ByteBuf in2) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in2);
        if (in == null) {
            return null;
        }

        // 校验头长度
        if (in.readableBytes() < HEADER_SIZE) {
            return null;
        }

        msgType = in.readByte();
        len = in.readInt();

        // 校验消息体长度
        if (in.readableBytes() < len) {
            return null;
        }

        ByteBuf buf = in.readBytes(len);
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");

        // ByteBuf转为Msg类型
        Msg msg = new Msg();
        MsgHeader msgHeader = new MsgHeader(msgType, len);
        msg.setBody(body);
        msg.setMsgHeader(msgHeader);
        return msg;
    }
}
