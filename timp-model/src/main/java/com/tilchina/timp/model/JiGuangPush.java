package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 极光推送
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class JiGuangPush extends SuperVO {
/*	{"platform":["android"],
		"audience":{"tag":["上海"]},
		"notification":{"android":{"alert":"Test-title","title":"Test-title"}},
		"message":{"title":"Test-title",
			"msg_content":"Test-content-test"},
		"options":{"sendno":1,
				"time_to_live":86400,
				"apns_production":false}}*/
    private String[] platform;   //推送平台设置
    private Map audience;   //推送设备
    private JSONObject notification;   //通知类型:0=通知内容体,1=消息内容体,2=通知内容体/消息内容体
    private JSONObject message;   //通知内容
    private JSONObject options;   //通知标题
    private String cid;   //用于防止 api 调用端重试造成服务端的重复推送而定义的一个标识符
    
/*    {
    	"data": {
    		"platformType": 1,
    		"pushType": 0,
    		"audienceType": 1,
    		"audience": {
    		"tag": ["上海"]
    		},
    		"notificationType": 0,
    		"content": "Test-content-test",
    		"title": "上海",
    		"cid": "Test-cid",
    		"pushCount": 1,
    		"flag": 0
    	}*/
}

