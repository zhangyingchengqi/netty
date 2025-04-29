package pro7_protocol.ex1_http;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String WEB_ROOT = "webapps";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if ("/".equals(uri)) {
            uri = "/index.html";
        }
        File file = new File(WEB_ROOT, uri);
        if (!file.exists() || file.isDirectory()) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        try {
            byte[] content = Files.readAllBytes(Paths.get(file.getPath()));
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, getContentType(uri));
            response.content().writeBytes(content);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.content().writeBytes(("Error: " + status.toString()).getBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String getContentType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".png")) return "image/png";
        if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) return "image/jpeg";
        if (uri.endsWith(".swf")) return "application/x-shockwave-flash";
        return "application/octet-stream";
    }


}
