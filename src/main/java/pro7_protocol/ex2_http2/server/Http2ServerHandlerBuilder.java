package pro7_protocol.ex2_http2.server;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2Settings;

import static io.netty.handler.logging.LogLevel.INFO;

/**
 * Http2ServerHandler Builder.
 */
public final class Http2ServerHandlerBuilder
        extends AbstractHttp2ConnectionHandlerBuilder<Http2ServerHandler, Http2ServerHandlerBuilder> {

    private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2ServerHandler.class);

    public Http2ServerHandlerBuilder() {
        frameLogger(logger);
    }

    @Override
    public Http2ServerHandler build() {
        return super.build();
    }

    @Override
    protected Http2ServerHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
                                           Http2Settings initialSettings) {
        Http2ServerHandler handler = new Http2ServerHandler(decoder, encoder, initialSettings);
        frameListener(handler);
        return handler;
    }
}
