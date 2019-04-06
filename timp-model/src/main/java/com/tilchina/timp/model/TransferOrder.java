package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 客户交接单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class TransferOrder extends SuperVO {

    private Long transferOrderId;   //主键
    private String transferOrderCode;   //交接单编号
    private Long customerId;   //客户ID(公司ID)
    private String carVin;   //车架号
    private Long orderId;   //原始订单ID
    private Long orderDetailId;   //原始订单明细ID
    private Long transportOrderId;   //运单ID
    private Long transportOrderDetailId;   //运单明细ID
    private Long dealerCorpId;   //经销公司ID
    private Long dealerUnitId;   //经销店ID(收发货单位ID)
    private String scanPath;   //扫描件路径
    private String remark;   //备注
    private Integer billStatus;   //状态(0=未签收 1=压单 2=甩单 3=在途,4=回单)
    //private Long userId;   //签收人ID(联系人ID)
    private Date receptionDate;   //签收时间
    private Long corpId;   //公司ID
    
    private String refCustomerName;//客户名称
    private String refOrderCode;//原始订单号
    private String refTransportOrderCode;//运单号
    private String refDealerCorpName;//经销公司名称
    private String refDealerUnitName;//经销店名称
    private String refUserName;//签收人名称
    private String refCorpName;//公司名称
    

}

