package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 司机登录信息
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class DriverLogin extends SuperVO {

    private Long driverLoginId;   //认证信息ID
    private Long driverId;   //用户ID
    private String password;   //密码
    private String token;   //TOKEN
    private String ldentifier;   //识别码
    private String userAgent;   //UserAgent
    private Integer clientType;   //客户端类型 0=PC 1=ANDROID 2=IOS 3=PAD 4=WX
    private Integer errorTimes;   //密码输入错误次数
    private Integer block;   //锁定状态:0=锁定,1=未锁定
    private String ip;   //IP
    private Date recentlyLogintime;   //最近一次登陆时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

}

