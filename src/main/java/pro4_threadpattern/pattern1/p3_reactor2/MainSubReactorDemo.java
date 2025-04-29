package pro4_threadpattern.pattern1.p3_reactor2;

import java.io.IOException;

/**
 * Main Sub Reactor Demo

 */
public class MainSubReactorDemo {

    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(2333)).start();
    }

}
