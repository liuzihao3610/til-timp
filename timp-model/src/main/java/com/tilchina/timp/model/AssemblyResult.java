package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;
import org.apache.commons.collections4.CollectionUtils;

/**
* 配板结果主表
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class AssemblyResult extends SuperVO {

    private Long assemblyResultId;   //
    private String resultCode;   //结果编号
    private BigDecimal allTransPrice;   //总运价
    private BigDecimal rate;   //毛利率
    private BigDecimal baseTransPrice;   //基础运价
    private BigDecimal allSubsidy;   //总补贴
    private Long trailerId;   //挂车ID
    private Integer maxQuantity;   //板位数
    private Integer carCount;   //商品车数量
    private Integer cityCount;   //城市数量
    private Integer unitCount;   //经销店数量
    private String transPlan;   //运输计划
    private Integer billStatus;   //单据状态 0=未生成 1=已生成
    private Long creator;   //创建人
    private Date createDate;   //创建时间

    private String refCreateName;

    @JSONField(serialize = false)
    private List<AssemblyResultDetail> details;

    public void addItem(AssemblyResultDetail detail){
        if(CollectionUtils.isEmpty(details)){
            details = new ArrayList<>();
        }
        details.add(detail);
    }

}

