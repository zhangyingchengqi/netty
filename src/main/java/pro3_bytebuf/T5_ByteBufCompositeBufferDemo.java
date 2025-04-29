package pro3_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: netty
 * @description: 复合缓冲区: 将多个缓冲区合并成一个缓冲区，实现零拷贝.   零拷贝: 零拷贝是一种I/O操作的优化技术，它通过减少数据的复制次数来提高I/O操作的性能。
 *                   为什么复合缓冲区能实现零拷贝? 因为复合缓冲区是由多个缓冲区组成的，每个缓冲区都有自己的内存地址,在进行I/O操作时，
 *                   只需要将多个缓冲区的内存地址传递给操作系统，操作系统会将数据从一个缓冲区复制到另一个缓冲区，而不需要将数据复制到用户空间。
 * @author: zy
 * @create: 2025-03-17 10:02
 */
public class T5_ByteBufCompositeBufferDemo {
    public static void main(String[] args) {
        // 创建一个堆缓冲区
        ByteBuf heapBuf = Unpooled.buffer(5);
        String way = "hello";
        heapBuf.writeBytes(way.getBytes());
        // 创建一个直接缓冲区
        ByteBuf directBuf = Unpooled.directBuffer(5);
        String lau = "world";
        directBuf.writeBytes(lau.getBytes());

        // 创建一个复合缓冲区
        CompositeByteBuf compositeBuffer = Unpooled.compositeBuffer(10);
        compositeBuffer.addComponents(heapBuf, directBuf); // 将缓冲区添加到符合缓冲区
        // 检查是否是支撑数组.
        // 不是支撑数组，则为复合缓冲区
        if (!compositeBuffer.hasArray()) {
            for (ByteBuf buffer : compositeBuffer) {
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
    }

    /**
     * 打印出Buffer的信息
     */
    private static void printBuffer(byte[] array, int offset, int len) {
        System.out.println("array：" + array);
        System.out.println("array->String：" + new String(array));
        System.out.println("offset：" + offset);
        System.out.println("len：" + len);
    }
}
