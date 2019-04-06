package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 奔驰导入模板
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Mercedes extends SuperVO {

    private Integer serialNumber;   //序号
    private String vendorCode;   //供应商编码
    private String sloc;   //海上交通线
    private String commNo;   //生产号
    private String model;   //型号
    private String vin;   //底盘号
    private String shipToParty;   //送达方编码
    private String shipToPartyName ;   //经销商名称
    private Date requestedDispatchDate;   //要求发运日期
    private Date financeReleaseDate ;   //财务放行日期
    private Date deliveryOrderDate;   //订单发送日期
    private Date VPCGateOutDate;   //VPC 出库日期
    private Date actualDeliveryDate;   //运输商实际发送日期
    private Date dealerReceiveDate;   //经销商收货日期
    private Date invoiceReceivedDate;   //开票日期
    private Integer specialDelivery;   //是否特殊运输(0=否 1=是)	
    private String repair;   //维修
    private String remarks;   //备注
    
    private List<DefaultTransportCorp> defaultTransportCorps; // 默认运输公司
    private Long receiveUnitId; // 收货单位ID


}

