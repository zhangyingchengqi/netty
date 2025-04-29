package pro3_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: netty中的ByteBuf的使用模式
 * @description:  2. 直接缓冲区模式: 属于堆外分配的直接内存，它可以被JVM的垃圾回收机制自动回收，并且可以被操作系统直接访问，因此它的读写性能比堆缓冲区要高
 *                            当使用Socket传递数据时性能很好，避免了数据从JVM堆内存复制到直接缓冲区的过程.
 *                                         缺点: 直接缓冲区的分配和释放都比较耗时，并且需要手动释放，否则会导致内存泄漏
 *
 *        涉及大量IO操作时用直接缓冲区。
 * @author: zy
 * @create: 2025-03-17 10:01
 */
public class T4_ByteBufDirectBufferDemo {

    public static void main(String[] args) {
        // 创建一个直接缓冲区  directBuffer
        ByteBuf buffer = Unpooled.directBuffer(10);
        String s = "helloworld";
        buffer.writeBytes(s.getBytes());
        // 检查是否是支撑数组.
        // 不是支撑数组，则为直接缓冲区
        if (  !buffer.hasArray()) {
            // 计算第一个字节的偏移量
            int offset = buffer.readerIndex();
            // 可读字节数
            int length = buffer.readableBytes();
            // 获取字节内容
            byte[] array = new byte[length];
            buffer.getBytes(offset, array);
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
