package pro5_decoder_encoder.ex3_codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import pro5_decoder_encoder.ex2_encoder.*;

import java.nio.charset.Charset;


public class MyClient {
    private String host;
    private int port;

    public MyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                   // ch.pipeline().addLast("decoder", new MyDecoder());
                   // ch.pipeline().addLast("encoder", new MyEncoder());
                    ch.pipeline().addLast(new MyCodec());   //使用组合的编解码器
                    ch.pipeline().addLast(new MyClientHandler());

                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();

            while (true) {

                // 发送消息给服务器
                Msg msg = new Msg();
                MsgHeader msgHeader = new MsgHeader();
                msgHeader.setMsgType(MsgType.EMGW_LOGIN_REQ.getValue());
//                String body = "床前明月光，疑是地上霜。举头望明月，低头思故乡。";

                String body = "将进酒\r\n李白\r\n" +
                        "君不见黄河之水天上来，奔流到海不复回。\r\n" +
                        "君不见高堂明镜悲白发，朝如青丝暮成雪。\r\n" +
                        "人生得意须尽欢，莫使金樽空对月。\r\n" +
                        "天生我材必有用，千金散尽还复来。\r\n" +
                        "烹羊宰牛且为乐，会须一饮三百杯。\r\n" +
                        "岑夫子，丹丘生，将进酒，杯莫停。\r\n" +
                        "与君歌一曲，请君为我倾耳听。\r\n" +
                        "钟鼓馔玉不足贵，但愿长醉不复醒。\r\n" +
                        "古来圣贤皆寂寞，惟有饮者留其名。\r\n" +
                        "陈王昔时宴平乐，斗酒十千恣欢谑。\r\n" +
                        "主人何为言少钱，径须沽取对君酌。\r\n" +
                        "五花马，千金裘，呼儿将出换美酒，与尔同销万古愁。\r\n" +
                        "水调歌头\r\n苏轼\r\n" +
                        "明月几时有？把酒问青天。\r\n" +
                        "不知天上宫阙，今夕是何年。\r\n" +
                        "我欲乘风归去，又恐琼楼玉宇，高处不胜寒。\r\n" +
                        "起舞弄清影，何似在人间。\r\n" +
                        "转朱阁，低绮户，照无眠。\r\n" +
                        "不应有恨，何事长向别时圆？\r\n" +
                        "人有悲欢离合，月有阴晴圆缺，此事古难全。\n" +
                        "但愿人长久，千里共婵娟。\r\n" +
                        "辛弃疾《青玉案·元夕》\r\n" +
                        "东风夜放花千树，更吹落、星如雨。\n" +
                        "宝马雕车香满路。\n" +
                        "凤箫声动，玉壶光转，一夜鱼龙舞。\n" +
                        "蛾儿雪柳黄金缕，笑语盈盈暗香去。\n" +
                        "众里寻他千百度，蓦然回首，那人却在，灯火阑珊处。";

                byte[] bodyBytes = body.getBytes(Charset.forName("utf-8"));
                int bodySize = bodyBytes.length;
                msgHeader.setLen(bodySize);
                msg.setMsgHeader(msgHeader);
                msg.setBody(body);

                f.channel().writeAndFlush(msg);
                Thread.sleep(2000);
            }
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyClient("localhost", 8082).run();
    }
}
