package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by demon on 2018/6/12.
 */
@Data
public class ImplTransVO implements Serializable{

    private String corpName;
    private String brandName;
    private String sendCityName;
    private String receiveCityName;
    private String price;
}
