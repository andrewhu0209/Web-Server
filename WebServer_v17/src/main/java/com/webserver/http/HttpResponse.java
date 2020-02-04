package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 響應對象
 * 該類的每一個實例用於表示一個具體要給客戶端
 * 響應的內容
 * 一個響應包含：
 * 狀態行，響應頭，響應正文
 * @author andrew
 *
 */
public class HttpResponse {
	/*
	 * 狀態行相關信息定義
	 */
	//狀態代碼
	private int statusCode = 200;
	//狀態描述
	private String statusReason  = "OK";
	/*
	 * 響應頭相關信息定義
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	//響應的實體文件
	private File entity;

	/*
	 * 響應正文相關信息定義
	 */
	
	//與連接相關信息定義
	private Socket socket;
	private OutputStream out;
	
	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 將當前響應內容發送給客戶端
	 */
	public void flush(){
		/*
		 * 響應客戶端：
		 * 1:發送狀態行
		 * 2:發送響應頭
		 * 3:發送響應正文
		 */
		try {
			
			sendStatusLine();
			sendHeaders();
			sendContent();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}
	/**
	 * 發送狀態行
	 */
	private void sendStatusLine() {
		try {
			//發送狀態行
			String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
			System.out.println("發送狀態行："+ line);
			println(line);
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	/**
	 * 發送響應頭
	 */
	private void sendHeaders() {
		try {
			Set<Entry<String,String>> set = headers.entrySet();
			
			for(Entry<String,String> header : set) {
				
				String key = header.getKey();
				String value = header.getValue();
				String line = key+": "+value;
				println(line);
			}
			//單獨發送CRLF，表示響應頭部分結束
			println("");
		

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 發送響應正文
	 */
	private void sendContent() {
		try {
			//發送響應正文
			FileInputStream fis = new FileInputStream(entity);
			byte[] data = new byte[1024*10];
			int len = -1;
			while((len = fis.read(data))!=-1) {
				out.write(data,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public File getEntity() {
		return entity;
	}
	/**
	 * 設置響應實體文件，在設置的同時會根據文件
	 * 類型自動添加對應的Content-Type與Content-Length
	 * 這兩個響應頭
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		//根據給定的文件自動設置對應的Content-Type與Content-Length
		
		this.headers.put("Content-Length", entity.length()+"");
		//獲取資源後綴名，去HttpContext中獲取對應的介質類型
		//獲取資源文件名
		String fileName = entity.getName();
		int index = fileName.lastIndexOf(".")+1;
		String ext = fileName.substring(index);
		String contentType = HttpContext.getMimeType(ext);
		this.headers.put("Content-Type",contentType);
		
		System.out.println(headers);
		
	}
	
	
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * 設置狀態代碼，設置後會自動將對應的描述
	 * 設置好
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		this.statusReason = HttpContext.getStatusReason(statusCode);
	}
	public String getStatusReason() {
		return statusReason;
	}
	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * 添加指定的響應頭信息
	 * @param name 響應頭的名子
	 * @param value 響應頭對應的值
	 */
	public void putHeader(String name, String value) {
		this.headers.put(name,value);
	}
	/**
	 * 向客戶端發送一行字符串
	 * 發送後會自動發送CR, LF
	 * @param line
	 */
	private void println(String line) {
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(HttpContext.CR);//written CR
			out.write(HttpContext.LF);//written CF
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
	
	
}
