package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 轿运车档案
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class Transporter extends SuperVO {

    private Long transporterId;                           // 轿运车ID
    private String transporterCode;                       // 轿运车编号
    private Long tractorId;                               // 车头型号ID
    private String tractirPlateCode;                      // 挂车车牌号
    private String tractirVin;                            // 车头车架号
    private String engineCode;                            // 发动机号
    private Long trailerId;                               // 挂车型号ID
    private String trailerPlateCode;                      // 车头车牌号
    private String trailerVin;                            // 挂车车架号
    private Long contractorId;                            // 承包人ID
    private Date contractorEndDate;                       // 承包到期时间CONTRACTOR_NAME
    private Integer billStatus;                           // 状态:0=制单,1=审核
    private Long creator;                                 // 创建人
    private Date createDate;                              // 创建时间
    private Long checker;                                 // 审核人
    private Date checkDate;                               // 审核时间
    private Long corpId;                                  // 公司ID
    private Integer flag;                                 // 封存标志
    private Integer trucksType;                           // 货车类型:0=重型货车,1=中型货车,2=轻型货车,3=微型货车。默认重型货车
    private Double weightCount;                           // 总重量:0~100吨,单位kg
    private Double checkLoadweight;                       // 核定载重:0~100吨,单位kg
    private Double trucksLong;                            // 车长:0~25米
    private Double trucksWidth;                           // 车宽:0~5米
    private Double trucksHigh;                            // 车高:0~6米
    private Integer axleCount;                            // 0~6 轴,默认5轴
    private Integer truckType;                            // 板车类型(0:大板 1:小板)
    private Double longEmptyFuelConsumption;              // 长途空载油耗
    private Double longHeavyFuelConsumption;              // 长途重载油耗
    private Double longEmptyFuelConsumptionWithContainer; // 长途空载带箱油耗
    private Double longHeavyFuelConsumptionWithContainer; // 长途重载带箱油耗
    private Double cityFuelConsumption;                   // 市内油耗
    private Double cityFuelConsumptionWithContainer;      // 市内带箱油耗

    private String refTractorName;          // 车头型号名称
    private String refTrailerName;          // 挂车型号名称
    private String refContractorName;       // 承包人名称
    private String refCorpName;             // 公司名称
    private String refCreateName;           // 创建人姓名
    private String refCheckerName;          // 审核人姓名
    private String appTractirPlateProvince; // 车头车牌号(沪)
    private String appTractirPlateCode;     // 车头车牌号(axxxxx0)
    private Integer refTractorType;         // 牵引车类型
    private Integer refMaxQuantity;         // 装车数量

    private String refContractorCode;       // 承包人编号
    private String refTractorCode;          // 牵引车型号
    
}

