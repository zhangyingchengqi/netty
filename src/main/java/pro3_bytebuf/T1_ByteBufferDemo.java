package pro3_bytebuf;

import java.nio.ByteBuffer;

/**
 * @program: netty
 * @description:  jdk的ByteBuffer的使用
 * @author: zy
 * @create: 2025-03-17 09:48
 */
public class T1_ByteBufferDemo {

    public static void main(String[] args) {
        // 创建一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10);
        System.out.println("------------初始时缓冲区------------");
        printBuffer(buffer);

        // 添加一些数据到缓冲区中
        System.out.println("------------添加数据到缓冲区------------");

        String s = "love";
        buffer.put(s.getBytes());  //缓冲区模式是写
        printBuffer(buffer);

        // 切换成读模式
        System.out.println("------------执行flip切换到读取模式------------");
        buffer.flip();
        printBuffer(buffer);

        // 读取数据
        System.out.println("------------读取数据------------");

        // 创建一个limit()大小的字节数组(因为就只有limit这么多个数据可读)
        byte[] bytes = new byte[buffer.limit()];

        // 将读取的数据装进我们的字节数组中
        buffer.get(bytes);   //将缓冲区的数据读到  bytes字节数组中
        printBuffer(buffer);

        // 执行compact
        System.out.println("------------执行compact------------");
        buffer.compact(); // 清空已经读取的数据，将未读取的数据向前移动，将position和limit都设置为可写入的位置
        printBuffer(buffer);

        // 执行clear
        System.out.println("------------执行clear清空缓冲区------------");
        buffer.clear(); // 清空缓冲区，将position和limit都设置为0
        printBuffer(buffer);

    }

    /**
     * 打印出ByteBuffer的信息
     *
     * @param buffer
     */
    private static void printBuffer(ByteBuffer buffer) {
        System.out.println("mark(记录当前所标记的索引下标)：" + buffer.mark());
        System.out.println("position(对于写模式，表示当前可写入的数据的下标,对于读,表示接下来可以读取的数据的下标)：" + buffer.position());
        System.out.println("limit(可于写模式，表示当前可以写入的数组大小，默认为数组最大长度,对于读模式，表示当前最多可以读取的数据位置下标)：" + buffer.limit());
        System.out.println("capacity(当前数组的容量大小）：" + buffer.capacity());
    }
}
