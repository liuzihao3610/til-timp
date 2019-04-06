package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class TransportLogin extends SuperVO {

    private Long transportLoginId;   //认证信息ID
    private Long userId;   //用户ID
    private String token;   //TOKEN
    private String special;   //SPECIAL
    private Integer checkPhone;   //手机验证
    private String captcha;   //验证码
    private String identifier;   //识别码
    private String userAgent;   //UserAgent
    private Integer clientType;   //客户端类型 0=PC 1=ANDROID 2=IOS 3=PAD 4=WX
    private String ip;   //IP
    private Date recentlyLogintime;   //最近一次登陆时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

}

