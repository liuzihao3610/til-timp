package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 短信验证码
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class Captcha extends SuperVO {

    private Long captchaId;   //主键
    private Long userId;   //用户ID
    private String phoneNum;   //手机号
    private String captchaCode;   //验证码
    private String identifier;   //识别码
    private String ip;   //IP
    private String captchaType;   //业务类型
    private Date sendTime;   //发送时间
    private Integer captchaStatus;   //使用状态 0=未使用 1=已使用
    private Long corpId;   //公司ID

}

