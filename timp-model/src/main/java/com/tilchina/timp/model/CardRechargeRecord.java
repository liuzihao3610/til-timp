package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 卡充值记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class CardRechargeRecord extends SuperVO {

    private Long cardRechargeRecordId;   //充值记录ID
    private Long cardResourceId;   //卡ID
    private BigDecimal balanceBeforeRecharge;   //充值前余额
    private BigDecimal rechargeMoney;   //充值金额
    private Integer deduction;   //是否可抵扣(0=否 1=是)
    private Long userId;   //充值人
    private Date rechargeDate;   //充值时间
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId; //公司ID
    
    private String refCardResourceCode;//卡号
    private String refUserName;//充值人名称
    private String refCreateName;//创建人名称
    private String refCorpName; //公司名称

}

