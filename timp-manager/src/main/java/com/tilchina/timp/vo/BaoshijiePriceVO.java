package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by demon on 2018/6/12.
 */
@Data
public class BaoshijiePriceVO implements Serializable {

    private String customerName;
    private String sendCityName;
    private String receiveCityName;
    private String carTypeName;
    private String price;
    private Date startDate;
    private Date endDate;
}
