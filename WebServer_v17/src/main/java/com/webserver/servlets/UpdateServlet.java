package com.webserver.servlets;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 修改密碼業務
 * @author andrew
 *
 */
public class UpdateServlet extends HttpServlet {
	public void service(HttpRequest request, HttpResponse response) {
		/*
		 * 1.獲取用戶信息
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String newPassword = request.getParameter("newpassword");
		
		/*
		 * 2:修改
		 */
		
		try {
			RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
			//匹配用戶
			boolean check = false;
			for(int i=0;i<raf.length()/100;i++) {
	
				raf.seek(i*100);
				//讀取用戶名
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				
				if(name.contentEquals(username)) {					
					check = true;
					//找到此用戶，匹配密碼
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						
						//匹配上後修改密碼
						//1先將指針移動到密碼位置
						raf.seek(i*100+32);
						//2將新密碼重新寫入
						data = newPassword.getBytes("UTF-8");
						data = Arrays.copyOf(data,32);
						raf.write(data);
						//3響應頁面					
						forward("/myweb/update_success.html", request, response);
						break;
						
					}else {
						//原密碼輸入有誤
						forward("/myweb/update_fail.html", request, response);						
					}
					
				}
			}
			
			if(!check) {
				//沒有此人
				response.setEntity(new File("webapps/myweb/no_user.html"));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
