package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * 
 * @version 1.0.0 2018/5/27
 * @author WangShengguang   
 */
@Data
public class BestAssemblyResultVO implements Serializable {

    private BigDecimal rate;
    private int moreCityPrice;
    private List<AssemblyResultVO> results;
}
