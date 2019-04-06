package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by demon on 2018/6/11.
 */
@Data
public class ImportBaseVO implements Serializable{

    private String customName;
    private String unitName;
    private String address;
    private String unitCode;
    private String contant;
    private String phone;
    private String tel;
    private String province;
    private String city;
    private String area;
    private String settlementCity;
    private String lng;
    private String lat;
}
