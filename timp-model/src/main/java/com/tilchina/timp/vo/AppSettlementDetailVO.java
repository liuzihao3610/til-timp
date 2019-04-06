package com.tilchina.timp.vo;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.model.TransportSubsidy;

/**
* 运单结算明细
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class AppSettlementDetailVO extends SuperVO {

    private String transportOrderCode;	//	运单号
    private Date receivingDate;	//	单据日期
    private BigDecimal allOrderPrice;   //	总运价(元)
    private BigDecimal basicsPrice;   //	基础运价(元)
    private BigDecimal subsidyPrice;   //	总的补贴金额(元)
    
    List<TransportSubsidy> Subsidys;	//	补贴明细

    List<DistributionDetailVO>	distributions;	//	配送明细

}

