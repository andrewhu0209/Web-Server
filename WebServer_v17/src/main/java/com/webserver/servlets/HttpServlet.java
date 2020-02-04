package com.webserver.servlets;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有Servlet的超類
 * @author andrew
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request, HttpResponse response);
	
	/**
	 * 跳轉到指定路徑
	 * 注:TOMCAT中實際該方法屬於轉發器，可以
	 * 通過request獲取。
	 * @param path
	 * @param request
	 * @param response
	 */
	public void forward(String path, HttpRequest request, HttpResponse response) {
		response.setEntity(new File("webapps"+path));
		
	}

}
