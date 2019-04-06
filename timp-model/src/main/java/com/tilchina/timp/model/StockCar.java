package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 商品车库存表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class StockCar extends SuperVO {

    private Long stockCarId;   //商品车库存表id
    private String stockCarCode;   //商品车库存表单号
    private Long orderId;   //客户订单ID
    private String orderCode;   //客户订单号
    private Long orderDetailId;   //子订单ID
    private Integer orderType;   //订单类型 0=客户订单 1=转包订单 2=分段订单 
    private Long customerId;   //客户ID
    private Long carrierId;   //承运商ID
    private String carVin;   //车架号
    private Long carId;   //商品车型号ID
    private Integer carStatus;   //车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店 11=取消下达/取消订单/取消计划)
    private Long currentUnitId;   //所在单位(指当前车辆在哪个单位)id
    private Date receivingDate;   //接单时间
    private Long transferOrderId;   //交接单id
    private Integer transferOrderStatus;   //交接单状态(0=未签收 1=压单 2=甩单 2=回单)
    private Date shutBillDate;   //关账时间
    private Integer shutBillStatus;   //关帐状态:0=未结算,1=已结算
    private Long shutBillId;   //关帐单id
    private Date rightBillType;   //对账时间
    private Integer rightBillStatus;   //对账状态:0=未结算,1=已结算
    private Long rightBillId;   //对账单id
    private Date settleDate;   //结算时间
    private Integer settleStatus;   //结算状态:0=未结算,1=已结算
    private Long settleId;   //结算id
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCustomerName;	//客户名称
    private String refCarrierName;   //承运商名称
    private String refCarName;   //商品车名称
    private String refBrandName; //品牌名称
    private String brandId; //品牌名称
    private String refCategoryName; //类别名称
    private String refCarCode; //商品车型号
    private String refCurrentUnitName;   //所在单位(指当前车辆在哪个单位) unitId
    private String refTransferOrderCode;	//交接单编码
    private String refRightBillCode;   //对账单id
    private String refShutBillCode;   //关帐单编码
    private String refSettleCode;   //结算编码
    private String refCorpName;//公司名称
    private String refSendUnitName;   //发货单位名称
    private String refSendUnitCity;	//发货单位城市
    private String refSendUnitAddr;	//发货单位名称
    private String refReceiveUnitName;   //收货单位名称
    private String refReceiveUnitCity;	//收货单位城市
    private String refReceiveUnitAddr;	//收货单位名称
    private Integer refReceiveUnitSpecial;	//是否经销店
    
    private Date refClaimLoadingDate;	//计划装车日期
    private Date refClaimDeliveryDate;	//计划到店日期
    private Long sendUnitId;	//发货单位id
    private Long receiveUnitId;	//发货单位id

    @JSONField(serialize=false)
    private OrderDetail orderDetail;
    
    private String otStatus;//app交接单显示字段

}

