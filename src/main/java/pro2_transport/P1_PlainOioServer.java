package pro2_transport;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Blocking networking without Netty
 */
public class P1_PlainOioServer {

    public static void main(String[] args) throws IOException {
        P1_PlainOioServer s = new P1_PlainOioServer();
        s.serve(9999);
    }

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        try {
            for (; ; ) {
                final Socket clientSocket = socket.accept();  // 阻塞
                System.out.println(
                        "接收客户端来自 " + clientSocket + " 的联接");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            //如果有有输入，就一直阻塞

                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(
                                    Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // ignore on close
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 以上模型由于I/O在阻塞时会一直占用CPU资源，因此在高并发场景下性能较差。
 */
