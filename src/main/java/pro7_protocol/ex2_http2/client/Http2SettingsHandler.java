package pro7_protocol.ex2_http2.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.Http2Settings;

import java.util.concurrent.TimeUnit;

/**
 * 读取第一个Http2Settings对象并通知ChannelPromise
 */
public class Http2SettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {
    private final ChannelPromise promise; // 用于通知第一个Http2Settings对象的ChannelPromise对象,


    public Http2SettingsHandler(ChannelPromise promise) {
        this.promise = promise;
    }

    /**

     * 等待此处理程序在升级到HTTP/2后添加，并等待初始序言握手完成。
     *
     */
    public void awaitSettings(long timeout, TimeUnit unit) throws Exception {
        // 等待ChannelPromise对象的通知，直到超时或其他失败发生
        if (!promise.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting for settings");
        }

        // 如果ChannelPromise对象的通知失败，则抛出异常
        if (!promise.isSuccess()) {
            throw new RuntimeException(promise.cause());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) throws Exception {
        // 读取到信息后，通知ChannelPromise对象
        promise.setSuccess();
        ctx.pipeline().remove(this);
    }
}
