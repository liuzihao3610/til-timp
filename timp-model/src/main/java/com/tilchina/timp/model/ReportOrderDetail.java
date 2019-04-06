package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 在途提报运单子表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class ReportOrderDetail extends SuperVO {

    private Long reportOrderDetailId;   //在途提报子表
    private Long reportId;   //在途提报主表ID
    private Long reportOrderId;   //在途提报运单主表ID
    private Long ordertDetailId;   //订单子表id
    private Long transportDetailId;   //运单子表ID
    private String carVin;   //车架号
    private Long carId;   //商品车型号ID
    private Long corpId;   //公司ID

}

