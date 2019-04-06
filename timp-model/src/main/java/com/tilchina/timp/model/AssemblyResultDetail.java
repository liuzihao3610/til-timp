package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 配板结果子表
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class AssemblyResultDetail extends SuperVO {

    private Long assemblyResultDetailId;   //
    private Long assemblyResultId;   //配板结果主表ID
    private Long orderId;   //订单ID
    private Long orderDetailId;   //订单明细ID
    private String carVin;   //车架号

    private String orderCode;
    private Long brandId;
    private String brandName;
    private Long carTypeId;
    private String carTypeName;
    private Long carId;
    private String carName;
    private Long sendUnitId;
    private String sendUnitCityName;
    private String sendUnitName;
    private Long receiveUnitId;
    private String receiveUnitCityName;
    private String receiveUnitName;

}

