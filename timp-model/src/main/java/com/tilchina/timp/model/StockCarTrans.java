package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 商品车运输记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class StockCarTrans extends SuperVO {

    private Long stockCarTransId;   //商品车运输记录表id
    private Long stockCarId;   //商品车库存表id
    private Long transportOrderDetailId;   //运单子表ID
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private Integer jobType;   //作业类型:0=长途,1=市内,2=短驳,3=回程
    private String carVin;   //车架号
    private Date shutBillDate;   //关账时间
    private Integer shutBillStatus;   //关帐状态:0=未结算,1=已结算
    private Long shutBillId;   //关帐单id
    private Date settleDate;   //结算时间
    private Integer settleStatus;   //结算状态:0=未结算,1=已结算
    private Long settleId;   //结算id
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refStockCarCode;   //商品车库存表编码
    private String refShutBillCode;   //关帐单编码
    private String refSettleCode;   //结算编码
    private String refCorpName;//公司名称

}

