package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 外协对账扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class OutsourcingDeduction extends SuperVO {

    private Long reconciliationDeductionId;   //
    private Long reconciliationId;   //主表ID
    private Long reconciliationDetailId;   //子表ID
    private Long deductionId;   //扣款项目ID
    private BigDecimal deductionAmount;   //扣款金额
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refDeductionName;
}

