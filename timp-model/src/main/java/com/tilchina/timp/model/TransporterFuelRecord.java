package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 车辆加油记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class TransporterFuelRecord extends SuperVO {

    private Long transporterFuelRecordId;   //车辆加油记录ID
    private Long oilDepotId;   //油库ID
    private Long driverId;   //司机ID
    private Long transporterId;   //轿运车ID
    private Double fuelCharge;   //加油量(L)
    private Date fuelDate;   //加油时间
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId; //公司ID
    
    private String refOilDepotCode;//油库编码
    private Integer refOilDepotType;//油库类型
    private String refOilLabel;//标号
    private String refDriverName;//司机名称
    private String refTractirPlateCode;//轿运车车头车牌号
    private String refCreateName;//创建人名称
    private String refCorpName; //公司名称

}

