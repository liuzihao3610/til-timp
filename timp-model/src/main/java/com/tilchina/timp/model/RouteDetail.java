package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 虚拟路线明细
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class RouteDetail extends SuperVO {

    private Long routeDetailId;   //
    private Long routeId;   //
    private Long cityAreaId;   //虚拟城市区域ID
    private Long corpId;   //公司ID

    private String refCityAreaCode;
    private String refCityAreaName;
    private String refSetCityName;


}

