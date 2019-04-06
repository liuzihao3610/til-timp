package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
* 价格描述表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class QuotationPrice extends SuperVO {

    private Long quotationPriceId;   //价格明细ID
    private Long quotationId;   //报价ID
    private Long quotationDetailId;   //报价明细ID
    private Integer quotationAmount;   //数量
    private BigDecimal quotationPrice;   //价格
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)

    public String toString() {
        return String.format("%s台%s元", quotationAmount, quotationPrice.toString());
    }
}

