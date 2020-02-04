package com.webserver.http;

public class Demo {
	public static void main(String[] args) {
		//獲取文件名的後綴部分
		String fileName = "logo.png";
		int index = fileName.lastIndexOf(".")+1;
		String ext = fileName.substring(index);
		System.out.println(ext);
		
	}

}
