package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @version 1.0.0 2018/5/16
 * @author WangShengguang   
 */
@Data
public class CityAreaGroupVO implements Serializable {

    private Long cityAreaId;
    private Long sendCityId;
    private String sendCityName;
    private Long receiveCityId;
    private Long receiveCityName;
    private Long settlementCityId;
    private String settlementCityName;

    private Double distance;

    private List<PendingOrderVO> orders;
    private int lastQuantity;

}
