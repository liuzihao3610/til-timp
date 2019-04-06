package com.tilchina.timp.vo;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单：收发货联系人信息
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class AppSettlementVO extends SuperVO {

    private Long transportOrderId;	//	运单id
    private String transportOrderCode;	//	运单号
    private Date receivingDate;		//	单据日期
    private BigDecimal allOrderPrice;   //	总运价(元)
}

