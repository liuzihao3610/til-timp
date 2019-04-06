package com.tilchina.timp.util;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.Push;

import cn.jiguang.common.utils.Base64;

public class JiGuangApi {
	// 设置好账号的app_key和masterSecret 
	private static String appKey = PropertiesUtils.getString("jgappkey");
	private static String masterSecret = PropertiesUtils.getString("jgmastersecret");
	private static String url = PropertiesUtils.getString("jgurl");
	public static void main(String[] args) {
		String client = "e01bbf30eca70ed7a44d6cbb:3b6dd42b36c9c3e68099ac96";
		StringBuilder Authorization;
		Authorization = new StringBuilder();
		System.out.println(Base64.encode(client.getBytes()));
		Authorization.append("Basic ").append(Base64.encode(client.getBytes()));
		System.out.println(Authorization.toString());
	}
	/**
	 * 推送消息给指定用户
	 * @param push
	 * @return 0推送失败，1推送成功
	 * @throws Exception 
	 */
	public static int sendToAll(Push push) {
		String client = appKey+":"+masterSecret,responseStr;
		StringBuilder Authorization;
	
		int status = 0;
		String str = "{\"platform\":[\"android\"],\"audience\":{\"tag\":[\"上海\"]},\"notification\":{\"android\":{\"alert\":\"Test-title\",\"title\":\"Test-title\"}},\"message\":{\"title\":\"Test-title\",\"msg_content\":\"Test-content-test\"},\"options\":{\"sendno\":1,\"time_to_live\":86400,\"apns_production\":false}}";
		try {
			Authorization = new StringBuilder();
			Authorization.append("Basic ").append(Base64.encode(client.getBytes()));
			Response re = Request.Post(url)
				.addHeader("Authorization",Authorization.toString())
				.bodyString("", ContentType.APPLICATION_JSON).execute();
			responseStr = re.returnResponse().toString();
			System.out.println(responseStr);
			if(responseStr.contains("200")) {
				status = 1;
			}else {
				throw new RuntimeException("极光推送失败！");
			}
		} catch (Exception e) {
			 status = 0;
			 throw new RuntimeException("操作失败！", e);
           
		}
		return status;
	 }
}
