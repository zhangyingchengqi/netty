package pro7_protocol.ex1_47_httpServer;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * @program: netty
 * @description: 读取 FullHttpRequest中的请求的内容, 客户端要访问的资源路径,然后以流的形式返回给客户端
 * @author: zy
 * @create: 2025-04-19 19:14
 */
public class HttpServerWebPageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String WEB_ROOT = "webapps";

    //日志
    private static Logger logger = Logger.getLogger(HttpServerWebPageHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if ("/".equals(uri)) {
            uri = "/index.html";
        }



        File file = new File(WEB_ROOT, uri);
        logger.info("访问的文件路径为:" + file.getAbsolutePath());
        if (file.exists() == false || file.isDirectory()) {
            logger.info("路径不存在");
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        //           java.nio中的Files类
        byte[] content = Files.readAllBytes(Paths.get(file.getPath()));
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,   getContentType(  uri )  );

        response.content().writeBytes(content);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);


    }

    private String getContentType(String uri) {
        if(  uri.endsWith(".html")){
            return "text/html; charset=UTF-8";
        }else if(  uri.endsWith(".css")){
            return "text/css; charset=UTF-8";
        }else if(  uri.endsWith(".js")){
            return "text/javascript; charset=UTF-8";
        }else if(  uri.endsWith(".png")){
            return "image/png";
        }else if(  uri.endsWith(".jpg")){
            return "image/jpeg";
        }else{
            return "text/octet-stream";
        }
    }

    //回送错误信息
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus notFound) {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, notFound);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        //

        response.content().writeBytes(("Error:" + notFound.toString()).getBytes());

        response.headers().set(    HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
//如何证明 基于netty的http服务器 性能强于  socket版本的http服务器
//    cpu密集型，         IO密集型       =>   如何优化.
// 请将当前服务器升级成 应用服务器 ,  servlet
