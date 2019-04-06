package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 商品车历史明细表
*
* @version 1.0.0
* @author LiShuqi
*/
@Getter
@Setter
@ToString
public class CarStatusHis extends SuperVO {

    private Long carStatusHisId;   //主键
    private Long carStatusId;   //明细表ID
    private Long carId;   //商品车ID
    private String carVin;   //车架号VIN
    private Integer urgentStatus;   //加急状态(0=一般 1=很急 2=特别急)
    private Long carTypeId;   //车型ID
    private String carTypeName;   //车型名称
    private Long brandId;   //品牌ID
    private String brandName;   //品牌名称
    private Long categoryId;   //类别ID
    private String categoryName;   //类别名称
    private Long orderId;   //当前订单ID
    private Long orderDetailId;   //当前订单明细ID
    private Integer orderStatus;   //当前订单状态
    private Long deliveryUnitId;   //当前发货单位ID
    private String deliveryUnitName;   //当前发货单位名称
    private Long receptionUnitId;   //当前收货单位ID
    private String receptionUnitName;   //当前收货单位名称
    private Long originalOrderId;   //原始订单ID
    private Long originalOrderDetailId;   //原始订单明细ID
    private Long customerCorpId;   //客户ID
    private String customerCorpName;   //客户名称
    private Date orderBillDate;   //客户订单日期
    private Long originalDeliveryUnitId;   //发货单位ID
    private String originalDeliveryUnitName;   //发货单位名称
    private Long originalReceptionUnitId;   //收货单位ID
    private String originalReceptionUnitName;   //收货单位名称
    private Date destinedLoadDate;   //指定装车日期
    private Date destinedReceptionDate;   //指定交车日期
    private Long transportOrderId;   //运单ID
    private Long driverId;   //司机ID
    private String driverName;   //司机姓名
    private BigDecimal customerQuotedPrice;   //客户报价
    private BigDecimal freightPrice;   //运价
    private Integer carStatusStatus;   //状态(0=未入库 1=在库 2=在途 3=到店)
    private Long sequence;   //序号
    private Long corpId;   //公司ID
    
    private String refCarName;//商品车名称
    private String refOrderCode;//当前订单号
    private String refOriginalOrderCode;//原始订单号
    private String refTransportOrderCode;//运单号
    private String refCorpName;//公司名称

}

