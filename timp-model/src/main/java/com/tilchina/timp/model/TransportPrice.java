package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运价管理
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class TransportPrice extends SuperVO {

    private Long transportPriceId;   //运价ID
    private String transportPriceCode;   //运价编号
    private Long customerId;   //客户ID(公司ID)
    private Long brandId;   //品牌ID
    private Long carTypeId;   //车辆类别ID
    private Long carId;   //商品车ID
    private Long startCityId;   //发货城市(城市ID)
    private Long endCityId;   //结算城市(城市ID)
    private Integer priceType;   //报价方式:0=单台,1=按板按台,2=按板按板
    private BigDecimal price;   //价格(元)
    private Integer examination;   //车检标志
    private Long checkpointId;   //检查站ID
    private String remark;   //备注
    private Integer billStatus;   //状态:0=制单,1=审核
    private Long creator;   //制单人
    private Date createDate;   //制单日期
    private Long checker;   //审核人
    private Date checkDate;   //审核日期
    private Long corpId;   //公司ID
    
    private String refCustomerName;   //客户名称(公司名称)
    private String refBrandName;   //品牌名称
    private String refCarTypeName;   //车辆类别名称
    private String refCarName;   //商品车名称
    private String refStartCityName;   //发货城市(城市名称)
    private String refEndCityName;   //结算城市(城市名称)
    private String refCheckpointName;   //检查站名称
    private String refCorpName;   //公司ID

}

