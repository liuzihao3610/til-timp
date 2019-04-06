package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* 订单子表
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class OrderDetail extends SuperVO {

    private Long orderDetailId;   //子订单ID
    private Long orderId;   //订单ID
    private String orderCode;   //订单号
	private Long carId;//商品车型号ID
	private Long carTypeId;//商品车类型ID
    private String carVin;   //车架号
    private Long brandId;   //品牌ID
    private Long sendUnitId;   //发货单位
    private Long receiveUnitId;   //收货单位
    private String instructionNumber;   //指示书号
    private Date claimLoadDate;   //指定装车日期
    private Date claimDeliveryDate;   //指定交车日期
    private Date eta;   //船期
    private Integer carStatus;//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店 11=取消下达/取消订单/取消计划)
    private Integer transferOrderStatus;//交接单状态(0=未签收 1=压单 2=甩单 3=在途,4=回单)
    private Long quotedPriceId;//报价编号
    private Long freightPriceId;//运价编号
    private String cancelReason;   //计划取消原因
    private String remark;   //备注
    private String productNumber;   //生产号
    private Integer closed;   //已关账(0=否 1=是)
    private Integer reconciliation;   //已对账(0=未对账 1=已对账)
    private Integer settlement;   //已结算(0=否 1=是)
    private Integer urgent;   //加急标志
    private Integer scheduleType;   //排程类型 0=未处理 1=预排 2=下达
    private Date confirmationDate;   //财务确认日期
    private BigDecimal customerQuotedPrice;   //客户报价
    private Long originalOrderId;   // 原始订单ID
    private Long originalOrderDetailId; // 原始订单明细ID
    private Long corpId;   //公司ID
    
    private String refSendUnitName;//发货单位名称
    private String refSendCityName;
    private String refReceiveUnitName;//收货单位名称
    private String refReceiveCityName;
    private String refCarName;//商品车名称
    private String refCarTypeName;//商品车名称
    private String refBrandName;//品牌名称
    private String refQuotedPriceCode;//客户报价编码
    private String refFreightPriceCode;//运价编码
    private String refCorpName;//公司名称

    private Long sendCityId;
    private Long receiveCityId;

    @JSONField(serialize = false)
    private Long oriReceiveCityId;// 原始收货城市

    @JSONField(serialize = false)
    private Long oriReceiveUnitId;// 原始收货单位
    
//    private List<DefaultTransportCorp> defaultTransportCorps; // 默认运输公司

}

