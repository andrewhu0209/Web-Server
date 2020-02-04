package com.webserver.servlets;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 處理註冊業務
 * @author andrew
 *
 */
public class RegServlet extends HttpServlet {
	public void service(HttpRequest request, HttpResponse response) {
		/*
		 * 註冊大致流程：
		 * 1:獲取用戶提交的註冊信息
		 * 2:將註冊信息寫入文件user.dat
		 * 3:響應客戶端註冊成功的頁面
		 */
		System.out.println("開始處理註冊業務！！！");
		/*
		 * 1
		 * 通過request.getParameter()方法獲取用戶
		 * 提交上來的數據時，傳遞的參數這個字符串的
		 * 值應當是頁面中form表單裡對應的輸入框的
		 * 名子(name 屬性的值)
		 */
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println("username: "+username);
		System.out.println("password: "+password);
		System.out.println("nickname: "+nickname);
		System.out.println("age: "+age);
		
		/*
		 * 2
		 * 每條紀錄佔100字節，其中用戶名，密碼
		 * 暱稱為字符串，各佔32字節。
		 * 年齡為int值佔4字節。寫入到user.dat
		 * 文件。
		 * 
		 */
		try {
			RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
			//先將指針移動到文件末尾
			raf.seek(raf.length());
			
			//寫用戶名
			//1先將用戶名轉成對應的一組字節
			byte[] data = username.getBytes("UTF-8");
			//2將該數組擴充為32字節
			data = Arrays.copyOf(data,32);
			//3將該字節數組一次性寫入文件
			raf.write(data);
			
			//寫密碼
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data,32);
			raf.write(data);
			
			//寫暱稱
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data,32);
			raf.write(data);
			
			//寫年齡
			raf.writeInt(age);
			
			System.out.println("註冊完畢!");
			raf.close();
			
			//3響應客戶端註冊成功頁面
			forward("/myweb/reg_success.html",request,response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
