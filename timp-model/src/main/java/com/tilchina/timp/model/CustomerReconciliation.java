package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 客户对账表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CustomerReconciliation extends SuperVO {

    private Long reconciliationId;   //主表ID
    private String reconciliationNumber;   //对账单号
    private Long customerId;   //客户ID
    private Date reconciliationStartDate;   //起始日期
    private Date reconciliationEndDate;   //截止日期
    private Integer carVinCount;   //车架号数量
    private BigDecimal totalAmount;   //总金额
    private BigDecimal redAmount;   //红字总额
    private BigDecimal blueAmount;   //蓝字总额
    private BigDecimal deductionTotalAmount;   //累计扣款
    private String deductionReason;   //扣款原因
    private Date customerFeedbackDate;   //客户反馈时间
    private Date settlementDate;   //结算时间
    private Long confirmor;   //确认人
    private Date confirmDate;   //确认日期
    private Long creator;   //创建人ID
    private Date createDate;   //创建时间
    private Long checker;   //审核人ID
    private Date checkDate;   //审核时间
    private Integer billStatus;   //单据状态(0:制单 1:审核)
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCustomerName;
    private String refConfirmorName;
    private String refCheckerName;
    private String refCreatorName;
    private String refCorpName;
}

