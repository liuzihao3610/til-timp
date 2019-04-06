package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 入库记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class OilDepotSupplyRecord extends SuperVO {

    private Long oilDepotSupplyRecordId;   //入库记录ID
    private Long oilDepotId;   //油库ID
    private Double allowanceBeforeSupply;   //入库前余量(m³)
    private Double oilSupply;   //入库量(m³)
    private BigDecimal unitPrice;   //单价(元/m³)
    private BigDecimal totalPrice;   //总金额(元)
    private Long managePerson;   //负责人
    private Date supplyDate;   //操作时间
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId; //公司ID
    
    private String refOilDepotCode;//油库编码
    private Integer refOilDepotType;//油库类型
    private String refOilLabel;//标号
    private String refManagePersonName;//负责人名称
    private String refCreateName;//创建人名称
    private String refCorpName; //公司名称

}

