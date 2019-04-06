package com.tilchina.timp.util;

import org.apache.http.client.fluent.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 
 * @author LiuShuqi 
 * @Description:HTTP 请求
 */
public class SmsUtil {
	
	public static String sendSmsByPost(String path) {
		URL url = null;
		try {
			url=new URL(path);
			
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");// 提交模式
			httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
			httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);  
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");

			httpURLConnection.connect();  
			StringBuilder s = new StringBuilder();
			int httpRspCode = httpURLConnection.getResponseCode();
			if (httpRspCode == HttpURLConnection.HTTP_OK) {
				// 开始获取数据
				BufferedReader br = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					s.append(line);
				}
				br.close();
				return s.toString();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sendMessage(String url) {
		String response = null;
		try {
			response = Request.Post(url).execute().returnContent().asString(Charset.forName("UTF8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}

}

