package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 外协对账主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class OutsourcingReconciliation extends SuperVO {

    private Long reconciliationId;   //
    private String reconciliationNumber;   //对账单号
    private Long vendorCorpId;   //承运公司
    private Integer carVinCount;   //车架号数量
    private BigDecimal outsourcingAmount;   //外协对账金额
    private BigDecimal actualAmount;   //实际金额
    private BigDecimal redAmount;   //红字总额
    private BigDecimal blueAmount;   //蓝字总额
    private BigDecimal deductionAmount;   //累计扣款
    private String deductionReason;   //扣款原因
    private Integer billStatus;   //单据状态
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long confirmor;   //确认人
    private Date confirmDate;   //确认时间
    private Date settlementDate;   //确认结算时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCreatorName;
    private String refCheckerName;
    private String refConfirmorName;
    private String refVendorCorpName;
    private String refCorpName;
}

