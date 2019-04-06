package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 商品车出库记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class StockCarOut extends SuperVO {

    private Long stockCarOutId;   //商品车出库记录表id
    private Long stockCarId;   //商品车库存表id
    private Long orderId;   //出库单ID
    private String orderCode;   //出库单号
    private Long orderDetailId;   //出库单子表ID
    private Integer orderType;   //订单类型 0=客户订单 1=转包订单 2=分段订单 
    private Long customerId;   //客户ID
    private Long carrierId;   //承运商ID
    private String carVin;   //车架号
    private Long carId;   //商品车型号ID
    private Integer carStatus;   //车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
    private Long currentUnitId;   //所在单位(指当前车辆在哪个单位)
    private Integer orderBillStatus;   //单据状态:0=有效单 1=作废单
    private Date issuedDate;   //下达时间
    private Date rightBillType;   //对账时间
    private Integer rightBillStatus;   //对账状态:0=未结算,1=已结算
    private Long rightBillId;   //对账单id
    private Date settleDate;   //结算时间
    private Integer settleStatus;   //结算状态:0=未结算,1=已结算
    private Long settleId;   //结算id
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refStockCarCode;   //商品车库存表编码
    private String refCustomerName;	//客户名称
    private String refCarrierName;   //承运商名称
    private String refCarName;   //商品车名称
    private String refBrandName; //品牌名称
    private String refCategoryName; //类别名称
    private String refCarCode; //商品车型号
    private String refCurrentUnitName;   //所在单位(指当前车辆在哪个单位) unitId
    private String refRightBillCode;   //对账单id
    private String refSettleCode;   //结算编码
    private String refCorpName;//公司名称
    
}

