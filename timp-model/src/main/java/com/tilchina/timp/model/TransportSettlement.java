package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class TransportSettlement extends SuperVO {

    private Long transportSettlementId;   //运单结算ID
    private Long transportOrderId;   //运单ID
    private String transportOrderCode;   //运单号
    private BigDecimal basicsPrice;   //基础运价(元)
    private BigDecimal subsidyPrice;   //补贴金额(元)
    private BigDecimal allOrderPrice;   //总运价(元)
    private BigDecimal grossMarginPrice;   //毛利率(元)
    private Long creator;   //制单人
    private Date createDate;   //制单日期
    private Long checker;   //审核人
    private Date checkDate;   //审核日期
    private Long corpId;   //公司ID

    private String grossMargin;   //毛利率(%)
    private TransportOrder transport;
    private String refCorpName;   //公司名称
    private String refCreateName;	//制单人姓名
    private String refCheckerName;	//审核人姓名
    private List<TransportSubsidy> subsidys;	//运单结算子表信息

}

