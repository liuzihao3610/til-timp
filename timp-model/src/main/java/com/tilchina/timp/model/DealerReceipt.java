package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 电子签收单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class DealerReceipt extends SuperVO {

    private Long dealerReceiptId;   //主键
    private String dealerReceiptCode;   //签收单号
    private Long carId;   //商品车ID
    private String carVin;   //车架号
    private Long orderId;   //原始订单ID
    private Long orderDetailId;   //原始订单明细ID
    private Long transportOrderId;   //运单ID
    private Long transportOrderDetailId;   //运单明细ID
    private Integer damageSign;   //质损标志
    private Long unitId;   //经销店ID
    private String remark;   //备注
    private Long userId;   //签收人ID
    private Date receptionDate;   //签收时间
    private Long corpId;   //公司ID
    
    private String refCarName;//商品车名称
    private String refOrderCode;//原始订单号
    private String refTransportOrderCode;//运单号
    private String refUnitName;//经销店名称
    private String refUserName;//签收人名称
    private String refCorpName;//公司名称
    

}

