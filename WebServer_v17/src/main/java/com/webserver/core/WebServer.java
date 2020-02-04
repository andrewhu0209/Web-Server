package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主類
 * 
 * @author andrew
 *
 *
 */
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool;

	/**
	 * 構造方法，用來初始化服務端
	 */
	public WebServer() {
		try {
			System.out.println("正在啟動服務端...");
			server = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(5);
			System.out.println("服務端啟動完畢");
		} catch (Exception e) {

		}
	}

	/**
	 * 服務端開始工作的方法
	 */
	public void start() {
		try {
			/*
			 * 暫時只處理客戶端的一次請求
			 */
			while (true) {
				System.out.println("等待客戶端連接...");
				Socket socket = server.accept();
				System.out.println("一個客戶端連接了");
				// 啟動一個線程處理該客戶端請求
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();

	}

}
