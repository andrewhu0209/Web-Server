package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlets.HttpServlet;

/**
 * 處理客戶端請求
 * 
 * @author andrew
 *
 *
 */
public class ClientHandler implements Runnable {
	private Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			/*
			 * 主流程： 1:解析請求 2:處理請求 3:發送響應
			 */

			// 1:準備工作
			// 1.1:解析請求，創建請求對象
			HttpRequest request = new HttpRequest(socket);
			// 1.2創建響應對象
			HttpResponse response = new HttpResponse(socket);

			// 2處理請求
			// 2.1:獲取請求的資源路徑
			String url = request.getRequestURI();
			System.out.println("處理後的url" + url);

			// 判斷該請求是否為請求業務
			String servletName = ServerContext.getServletName(url);
			if (servletName != null) {
				System.out.println("ClientHandler:正在加載" + servletName);
				Class<?> cls = Class.forName(servletName); // 加載其對應的class object
				HttpServlet servlet = (HttpServlet) cls.newInstance(); // 透過java反射實例化這個class
				servlet.service(request, response); // 調用這個servle的service方法

			} else {

				// 2.2:根據資源路徑去webapps目錄中尋找該資源
				File file = new File("webapps" + url);

				if (file.exists()) {
					System.out.println("Resource Found！");
					// 向響應對象中設置要響應的資源內容
					response.setEntity(file);

				} else {
					// 設置狀態代碼404
					response.setStatusCode(404);
					// 設置404頁面
					response.setEntity(new File("webapps/root/404.html"));
					System.out.println("Resource Not Found！");

				}
			}
			// 3響應客戶端
			response.flush();
		} catch (EmptyRequestException e) {
			/*
			 * 實例化HttpRequest時若發現是空請求時 該構造方法會將該異常拋出，這裏不做任 何處理，直接在finally中與客戶端斷開 即可
			 */
			System.out.println("空請求！");

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// 與客戶端斷開連接
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
