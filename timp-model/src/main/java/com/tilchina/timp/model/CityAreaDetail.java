package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 虚拟城市区域明细
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class CityAreaDetail extends SuperVO {

    private Long cityAreaDetailId;   //
    private Long cityAreaId;   //
    private Long unitId;   //收发货单位ID
    private Long corpId;   //公司ID

    private String refUnitName; // 收货单位名称
    private String refProvince; // 收货单位省
    private String refCity; // 收货单位市
    private String refArea; // 收货单位区
    private String refUnitAddress;  // 收货单位地址

}

