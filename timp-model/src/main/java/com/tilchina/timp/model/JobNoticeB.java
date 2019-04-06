package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 定时到期任务提醒子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class JobNoticeB extends SuperVO {

    private Long jobNoticeBId;   //主键
    private Long jobNoticeId;   //主表ID
    private Long userId;   //用户ID
    private Integer userType;   //用户类型(0=系统用户 1=司机)
    private Long corpId;   //公司ID

    private String refJobNoticeName;//定时任务名称
    private String refJobNoticeCode;//定时任务编码
    private String refUserName;//用户名称
    private String refCorpName;//公司名称

}

