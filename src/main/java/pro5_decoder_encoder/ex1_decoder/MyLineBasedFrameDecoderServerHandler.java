package pro5_decoder_encoder.ex1_decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//ChannelInboundHandlerAdapter是一个适配器类
//MyLineBasedFrameDecoderServerHandler将读到的数据打印到控制台.
public class MyLineBasedFrameDecoderServerHandler extends ChannelInboundHandlerAdapter {

    //关注读的事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 接收msg消息，因为在这个Handler前面有一个  StringDecoder,所以msg已经是String, 无须解码.
        System.out.println("客户端->服务器: " + msg);
    }
}
