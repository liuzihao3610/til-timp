package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by demon on 2018/6/11.
 */
@Data
public class ImportDriverVO implements Serializable {

    private String corpName;
    private String driverName;
    private String tractorPlate;
    private String trailerPlate;
    private String max;
    private String cdCard;
    private String qrCode;
    private String gps;
    private String settlement;

}
