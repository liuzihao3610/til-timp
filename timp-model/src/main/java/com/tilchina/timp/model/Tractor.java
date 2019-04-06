package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 轿运车车头型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Tractor extends SuperVO {

    private Long tractorId;   //牵引车ID
    private String tractorCode;   //牵引车型号
    private String tractorName;   //牵引车名称
    private Long brandId;   //品牌ID
    private Integer tractorType;   //牵引车类型(0=一般货车 1=半挂牵引车 2=全挂牵引车）
    private Integer truckType;//板车类型(0=大板 1=小板)
    private Integer baseType;   //牵引座类型(0=固定型 1=轴式牵引座）
    private Integer tractorLong;   //长(毫米）
    private Integer tractorWidth;   //宽(毫米)
    private Integer tractorHigh;   //高(毫米)
    private Double tractorWeight;   //自重(kg)
    private Double tractionWeight;   //牵引总质量(kg)
    private Double fuel;   //满载油耗(百公里油耗)
    private BigDecimal price;   //价格(元)
    private String origin;   //产地
    private Long supplierId;   //经销商ID
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refBrandName; //品牌名称
    private String refSupplierName; //经销商名称
    private String refCreateName; //创建人名称
    private String refCheckerName; //审核人名称
    private String refCorpName; //公司名称
    

}

