package pro7_protocol.ex1_47_httpServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @program: netty
 * @description:   处理器，  处理http请求，组装pipeline
 * @author: zy
 * @create: 2025-04-19 19:07
 */
public class HttpServerChannelHandler  extends ChannelInitializer<SocketChannel> {
    private EventLoopGroup businessGroup;

    public HttpServerChannelHandler(EventLoopGroup businessGroup) {
        this.businessGroup = businessGroup;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //日 志
        ch.pipeline().addLast(  this.businessGroup, "logging", new LoggingHandler(LogLevel.INFO)); // 增加一个日志处理的 handler
        //配置  http协议的编解码器
        ch.pipeline().addLast(  this.businessGroup, new HttpServerCodec());
        //加入一个 aggregator 聚合器, 聚合http请求和响应,并设置内容的最大长度，
        ch.pipeline().addLast(  this.businessGroup, new HttpObjectAggregator(1024 * 1024 * 10));
        //   支持异步发送大的码流（例如大的文件），但不占用过多的内存，防止发生java内存溢出错误。
        ch.pipeline().addLast(  new ChunkedWriteHandler()  );
        // 自定义处理器
        ch.pipeline().addLast(  this.businessGroup,"serverWebPageHandler", new HttpServerWebPageHandler()  );

    }
}
