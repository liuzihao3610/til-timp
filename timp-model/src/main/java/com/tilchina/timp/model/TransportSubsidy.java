package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单结算子表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class TransportSubsidy extends SuperVO {

    private Long transportSubsidtId;   //运单结算子表ID
    private Long transportSettlementId;   //运单结算主表ID
    private Long deductionId;   //扣款项目ID
    private BigDecimal subsidyPrice;   //金额(元)
    private String remark;   //备注
    private Long creator;   //创建人
    private Date createDate;   //创建日期
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String deductionName;   //扣款项目名称
    private String refCorpName;   //公司名称
    private String refCreateName;	//创建人姓名
    
}

