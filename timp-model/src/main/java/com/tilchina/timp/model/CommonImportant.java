package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 通用导入模板
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class CommonImportant extends SuperVO {

    private String startPlace ;   //起运地
    private String vin;   //车架号
    private String destination;   //目的地
    private String destinationCity;   //目的地城市
    private String model;   //车型
    private String brandName;   //品牌
    private Date loadDate;   //装车日期
    private Date arrivalDate;   //指定到达日期
    private String scooterCode;   //计划板车号
    private String driverName;   //司机
    private String remarks;   //备注
    private String carryCorp;   //承运公司
    private Date confirmationDate;   //财务确认日期
    private Date outBoundDate;   //港口出库日期
    private String productionCode;//生产编码
    private String scooter;   //实际板车号
    private String code;   //编号
    private String instructionNumber;   //指示书号
    private BigDecimal freightPrice;//运费
    
    private List<DefaultTransportCorp> defaultTransportCorps; // 默认运输公司
    private Long receiveUnitId; // 收货单位ID

}

