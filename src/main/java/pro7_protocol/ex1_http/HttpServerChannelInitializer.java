/**
 * Welcome to https://waylau.com
 */
package pro7_protocol.ex1_http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	public HttpServerChannelInitializer() {

	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("codec", new HttpServerCodec());  // 支持HTTP协议
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576)); //设置最大内容长度为1MB
		ch.pipeline().addLast(new ChunkedWriteHandler());   // 支持异步发送大的码流（大文件传输），但不占用过多的内存，防止发生OOM
		ch.pipeline().addLast("serverHandler", new HttpServerHandler());    // 自定义的Handler
	}

}
