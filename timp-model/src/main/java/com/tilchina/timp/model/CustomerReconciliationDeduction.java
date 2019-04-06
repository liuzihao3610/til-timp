package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 客户对账明细扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CustomerReconciliationDeduction extends SuperVO {

    private Long reconciliationDeductionId;   //客户对账明细扣款ID
    private Long reconciliationId;   //
    private Long reconciliationDetailId;   //
    private Long deductionId;   //扣款项目ID
    private BigDecimal deductionAmount;   //扣款金额
    private Long creator;   //创建人ID
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refDeductionName;
    private String refCreatorName;
    private String refCorpName;
}

