package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.webserver.core.EmptyRequestException;

/**
 * 請求對象
 * 每個實例表示客戶端發送過來的一個具體請求
 * @author andrew
 *
 */
public class HttpRequest {
	/*
	 * 請求行相關信息定義
	 */
	//請求方式
	private String method;
	//資源路徑
	private String url;
	//協議版本
	private String protocol;
	
	//url中的請求部分
	private String requestURI;
	//url中的參數部分
	private String queryString;
	//每個參數
	private Map<String,String> parameters = new HashMap<String,String>();
	
	
	/*
	 * 消息頭相關信息定義
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	/*
	 * 消息正文相關信息定義
	 */
	
	//客戶端連接相關信息
	private Socket socket;
	private InputStream in;
	/**
	 * 初始化請求
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * 解析請求
			 * 
			 * 1:解析請求行
			 * 2:解析消息頭
			 * 3:解析消息正文
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
			
		}catch(EmptyRequestException e) {
			throw e;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 解析請求行
	 */
	private void parseRequestLine()throws EmptyRequestException{
		System.out.println("開始解析請求行...");
		try {
			String line = readLine();
			System.err.println("請求行："+line);
			/*
			 * 將請求行進行拆分，將每部份內容
			 * 對應的設置到屬性上。
			 */
			String[] data = line.split("\\s");
			if(data.length!=3) {
				//空請求
				throw new EmptyRequestException();
			}
			method = data[0];
			url = data[1];
			//進一步解析URL
			parseURL();
			protocol = data[2];
		
			System.out.println("method:"+method);
			System.out.println("url:"+ url);
			System.out.println("protocol:"+ protocol);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("解析請求行完畢！");
	}
	/**
	 * 進一步解析URL
	 * url有可能會有兩種格式：帶參數和不帶參數
	 * 1,不帶參數如：
	 * /myweb/index.htm
	 * 
	 * 2,帶參數如：
	 *  http://localhost:8088/myweb/reg?username=ilovemyfriend7462%40gmail.com&password=123456&nickname=hushih&age=23
	 */
	private void parseURL() {
		/*
		 * 首先判斷當前url是否含有參數，判斷的
		 * 依據試看url是否包含"?"，含有則認為
		 * 這個url是包含參數的，否則直接將url
		 * 賦值給requestURI即可。
		 * 
		 * 若有參數：
		 * 1:將url按照“？”拆分為兩部分，第一部分
		 *   為請求部分，賦值給requestURI
		 *   第二部分為參數部分，賦值給queryString
		 *   
		 * 2:再對queryString進一步拆分，先按照“&"
		 *   拆分出每個參數，再將每個參數按照“＝”
		 *   拆分為參數名與參數值，並存入parameters
		 *   這個Map中。
		 *   
		 * 解析過程中要注意url的幾個特別情況：
		 * 1:url可能含有"？"但是沒有參數部分
		 * 如：
		 * /myweb/reg?
		 * 
		 * 2:參數部分有可能只有參數名沒有參數值
		 * 如：http://localhost:8088/myweb/reg?username=&password=123456&nickname=hushih&age=23
		 * 
		 *   
		 */
		if(url.indexOf("?")!=-1){
			//按照？拆分
			String[] data = url.split("\\?");
			requestURI = data[0];
			//判斷?後面是否有參數
			if(data.length>1) {
				queryString = data[1];
				//近一步解析參數部分
				parseParameter(queryString);
				
			}
		

		}else {
			//不含也？
			requestURI = url;
		}
		
		
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);
		
		
		
		
		
	}
	
	/**
	 * 解析消息頭
	 */
	private void parseHeaders(){
		System.out.println("開始解析消息頭...");
		try {
			/*
			 * 解析消息頭的流程：
			 * 循環調用readLine方法，讀取每一個消息頭
			 * 當readLine方法返回值為空字符串時停只
			 * 循環(因為返回空字符串說明單獨讀取了CRLF
			 * 而這是作為消息頭結束的標誌)
			 * 在讀取到每個消息頭後，根據":"(冒號空格)
			 * 進行拆分，並將消息頭的名子作為key，消息
			 * 頭對應的值作為value保存到屬性headers這個
			 * Map中完成解析工作
			 */
			while(true){
				String line = readLine();
				if("".equals(line)){
					break;
				}
				String[] data = line.split(":\\s");
				headers.put(data[0], data[1]);
			}
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("析消息頭完畢！");
	}
	/**
	 * 解析消息正文
	 */
	private void parseContent(){
		System.out.println("開始解析消息正文...");
		/*
		 * 根據消息頭是否含有Content-Length決定
		 * 該請求是否含有消息正文
		 */
		

		try {
			
			
			if(headers.containsKey("Content-Length")) {
				
				//含有消息正文的
				int length = Integer.parseInt(headers.get("Content-Length"));
				//讀取消息正文內容
				byte[] data = new byte[length];
				in.read(data);
				
				/*
				 * 根據消息頭Content-Type判斷該
				 * 消息正文的數據類型
				 */
				String contentType = headers.get("Content-Type");
				//判斷是否為form表單提交數據
				
				if("application/x-www-form-urlencoded".equals(contentType)) {
					
					/*
					 * 該正文內容相當於原GET請求地址欄裡
					 * url中"?"右側內容
					 */
					String line = new String(data,"ISO8859-1");
					System.out.println("正文內容:"+line);
					parseParameter(line);
				}
				
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("消息正文完畢！");

	}
	
	/**
	 * 解析參數
	 * 格式：name=value&name=value&...
	 * @param line
	 */
	private void parseParameter(String line){ 
		/*
		 * 先將參數中的"%XX"的內容按照對應
		 * 字符集(瀏覽器通常用UTF-8)還原為
		 * 對應文字
		 */
		try {
			/*
			 * URLDecoder的decode方法可以將給定的
			 * 字符串中的"%XX"內容
			 */
			
			System.out.println("對參數轉碼前:"+line);
			line=URLDecoder.decode(line,"UTF-8");
			System.out.println("對參數轉碼後:"+line);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
		
		
		
		//按照&拆分出每一個參數
		String[] paraArr = line.split("&");
		//遍歷每個參數進行拆分
		for(String para:paraArr) {
			//再按照"=" 拆分每個參數
			String[] paras = para.split("=");
			if(paras.length>1) {
				//該參數有值
				parameters.put(paras[0], paras[1]);
			}else {
				//沒有值
				parameters.put(paras[0],null);
			} 
				
		}
		
		
		
	}
	
	/**
	 * 讀取一行字符串，當連續讀取CR，LF時停止
	 * 並將之前的內容以一行字符串形式返回。
	 * @return
	 * @throws IOException
	 */
	private String readLine() throws IOException{
		StringBuilder builder = new StringBuilder();
		//本次讀取的字節
		int d = -1;
		//c1表示上次讀取的字符，c2表示本次讀取的字符
		char c1='a', c2='a';
		while((d = in.read())!=-1){
			c2 = (char) d;
			if(c1==HttpContext.CR&&c2==HttpContext.LF){
				break;
			}
			builder.append(c2);
			c1 = c2;
		}
		return builder.toString().trim();  //13換行符是空白字符，故使用trim把他拿掉
	}
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getProtocol() {
		return protocol;
	}
	/**
	 * 根據給定的消息頭的名子獲取對應消息頭的值
	 * @param name
	 * @return
	 */
	public String getHeader(String name){
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
/**
 * 根據給定的參數名獲取對應的參數值
 * @param name
 * @return
 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	
	

}
