package pro4_threadpattern.pattern1.p3_client;

/**
 * Client Demo

 */
public class ClientDemo {

    public static void main(String[] args) {
        new Thread(new NIOClient("127.0.0.1", 2333)).start();
        new Thread(new NIOClient("127.0.0.1", 2333)).start();
    }

}
