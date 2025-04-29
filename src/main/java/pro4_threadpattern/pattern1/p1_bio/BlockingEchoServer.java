/**
 * Welcome to https://waylau.com
 */
package pro4_threadpattern.pattern1.p1_bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Blocking Echo Server.
 */
public class BlockingEchoServer {




	public static void main(String[] args) {

		int port=9999;

		ServerSocket serverSocket = null;
		try {
			// 服务器监听
			serverSocket = new ServerSocket(port);
			System.out.println(
					"BlockingEchoServer已启动，端口：" + port);

		} catch (IOException e) {
			System.out.println(
					"BlockingEchoServer启动异常，端口：" + port);
			System.out.println(e.getMessage());
		}

		// Java 7 try-with-resource语句
		try (
				// 接受客户端建立链接，生成Socket实例
				Socket clientSocket = serverSocket.accept();
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);

				// 接收客户端的信息
				BufferedReader in =
						new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				// 发送信息给客户端
				out.println(inputLine);
				System.out.println(
						"BlockingEchoServer -> " + clientSocket.getRemoteSocketAddress() + ":" + inputLine);
			}
		} catch (IOException e) {
			System.out.println(
					"BlockingEchoServer异常!" + e.getMessage());
		}
	}

}
