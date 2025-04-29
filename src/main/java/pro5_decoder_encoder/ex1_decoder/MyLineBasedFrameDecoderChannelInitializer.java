package pro5_decoder_encoder.ex1_decoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 自定义ChannelInitializer，用于初始化ChannelPipeline,并添加自定义的ChannelHandler.
public class MyLineBasedFrameDecoderChannelInitializer extends ChannelInitializer<SocketChannel> {

    private EventLoopGroup businessGroup;

    public MyLineBasedFrameDecoderChannelInitializer(EventLoopGroup businessGroup) {
        // 工作线程组
         this.businessGroup = businessGroup;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast( "logging", new LoggingHandler(LogLevel.DEBUG)); // 增加一个日志处理的 handler

        // 基于换行符号的解码器
        channel.pipeline().addLast(  this.businessGroup,new MyLineBasedFrameDecoder());

        // 解码  字节流转String
        channel.pipeline().addLast( this.businessGroup, new StringDecoder());

        // 自定义ChannelHandler
        channel.pipeline().addLast(  this.businessGroup,  new MyLineBasedFrameDecoderServerHandler());

        //也可以指定线程组
       // channel.pipeline().addLast(  workThreadEventLoop,  new MyLineBasedFrameDecoderServerHandler());
    }
}
