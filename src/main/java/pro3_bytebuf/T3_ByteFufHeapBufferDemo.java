package pro3_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: netty中的ByteBuf的使用模式
 * @description: 堆缓冲区模式: 堆缓冲区是Netty中最常用的缓冲区类型，它使用JVM的堆内存来存储数据
 *    优点: 堆缓冲区是JVM的堆内存，因此它的分配和释放都非常快，并且可以被JVM的垃圾回收机制自动回收,并提供了数组直接快速访问机制
 *     缺点: 每次数据与I/O进行传输时，都需要将数据复制到直接缓冲区中，这会导致数据的复制和传输的开销
 * @author: zy
 * @create: 2025-03-17 09:58
 */
public class T3_ByteFufHeapBufferDemo {

    public static void main(String[] args) {
        // 创建一个堆缓冲区
        ByteBuf buffer = Unpooled.buffer(10);
        String s = "helloworld";
        buffer.writeBytes(s.getBytes());
        // 检查是否是支撑数组
        if (buffer.hasArray()) {
            // 支撑数组的引用
            byte[] array = buffer.array();
            // 计算第一个字节的偏移量
            int offset = buffer.readerIndex() + buffer.arrayOffset();
            // 可读字节数
            int length = buffer.readableBytes();
            printBuffer(array, offset, length);
        }
    }

    /**
     * 打印出Buffer的信息
     *
     */
    private static void printBuffer(byte[] array, int offset, int len) {
        System.out.println("array：" + array);
        System.out.println("array->String：" + new String(array));
        System.out.println("offset：" + offset);
        System.out.println("len：" + len);
    }
}
