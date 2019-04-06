package com.tilchina.timp.vo;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单结算明细：配送明细
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class DistributionDetailVO extends SuperVO {

    private String brandName;	  //	车辆信息（车辆品牌）
    private String sendUnitAddr;	//	发货单位名称
    private String receiveUnitName;   //	收货单位名称
    private String carVin;  //  车架号
    private BigDecimal finalPrice;   //	单价(运价)

}

