package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 服務端相關配置信息
 * @author andrew
 *
 */
public class ServerContext {
	/*
	 * Servlet映射關係
	 * key:請求路徑
	 * value:Servlet的名子
	 */
	private static Map<String,String> servletMapping = new HashMap<String,String>();
	
	static {
		initServletMapping();
	}
	/**
	 * 初始化Servlet映射
	 */
	private static void initServletMapping() {
//		servletMapping.put("/myweb/reg","com.webserver.servlets.RegServlet");
//		servletMapping.put("/myweb/login","com.webserver.servlets.LoginServlet");
//		servletMapping.put("/myweb/update","com.webserver.servlets.UpdateServlet");
		/*
		 * 加載conf/servlets.xml文件初始化
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for(Element servletEle : list) {
				String url = servletEle.attributeValue("url");
				String className = servletEle.attributeValue("className");
				servletMapping.put(url,className);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	/**
	 * 根據請求路徑獲取對應的Servlet名子
	 * @param s
	 * @return
	 */
	public static String getServletName(String url) {
		return servletMapping.get(url);
		
	}
	
	public static void main(String[] args) {
		System.out.println(getServletName("/myweb/reg"));
	}
	
	

}
