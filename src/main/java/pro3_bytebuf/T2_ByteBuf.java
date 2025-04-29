package pro3_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: netty
 * @description: netty提供的ByteBuf操作更简洁
 * @author: zy
 * @create: 2025-03-17 09:54
 */
public class T2_ByteBuf {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // 创建一个缓冲区
        ByteBuf buffer = Unpooled.buffer(10);
        System.out.println("------------初始时缓冲区------------");
        printBuffer(buffer);

        // 添加一些数据到缓冲区中
        System.out.println("------------添加数据到缓冲区------------");

        String s = "love";
        buffer.writeBytes(s.getBytes());
        printBuffer(buffer);

        // 读取数据
        System.out.println("------------读取数据------------");

        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }

        printBuffer(buffer);

        // 执行compact
        System.out.println("------------执行discardReadBytes------------");
        buffer.discardReadBytes(); // 清空已经读取的数据，将未读取的数据向前移动，将readerIndex和writerIndex都设置为可写入的位置
        printBuffer(buffer);

        // 执行clear
        System.out.println("------------执行clear清空缓冲区------------");
        buffer.clear(); // 清空缓冲区，将readerIndex和writerIndex都设置为0
        printBuffer(buffer);

    }

    /**
     * 打印出ByteBuf的信息
     *
     * @param buffer
     */
    private static void printBuffer(ByteBuf buffer) {
        System.out.println("readerIndex：" + buffer.readerIndex());
        System.out.println("writerIndex：" + buffer.writerIndex());
        System.out.println("capacity：" + buffer.capacity());
    }
}
