package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 推送档案
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class Push extends SuperVO {

    private Long pushId;   //推送ID
    private Integer platformType;   //推送平台设置类型:0=all,1=android,2=ios
    private Integer pushType;   //推送类型:0=自动推送,1=人工推送
    private Integer audienceType;   //推送设备指定类型:0=all,1=多个标签(只要在任何一个标签范围内都满足),2=多个标签(需要同时在多个标签范围内),3=多个别名,4=多个注册ID,5=多类推送目标
    private String audience;   //推送设备指定内容
    private Integer notificationType;   //通知类型:0=通知内容体,1=消息内容体,2=通知内容体/消息内容体
    private String content;   //通知内容
    private String title;   //通知标题
    private String cid;   //用于防止 api 调用端重试造成服务端的重复推送而定义的一个标识符
    private Integer pushCount;   //推送信息条数
    private Long recipient;   //接收人
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCorpName;	//公司名称
    private String refCreateName;	//创建人姓名
    
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

