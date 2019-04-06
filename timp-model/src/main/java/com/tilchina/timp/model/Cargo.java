package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 货物装载信息表
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class Cargo extends SuperVO {

    private Long cargoId;   //载货ID
    private Long trailerId;   //挂车ID
    private Integer cargoType;   //货物类型
    private Integer floor;   //所在层
    private Integer place;   //位置
    private Integer carryOver;   //是否可以结转
    private Integer cargoLong;   //长（毫米）
    private Integer cargoWidth;   //宽（毫米）
    private Integer cargoHigh;   //高（毫米）
    private Integer cargoWeight;   //限重（kg）
    private Long corpId;   //公司
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Integer flag;   //封存标志
    
    private String refTrailerCode;//挂车型号
    private String refTrailerName;//挂车名称
    private String refCorpName;//公司名称
    private String refCreateName;//创建人名称

}

