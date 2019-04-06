package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by demon on 2018/6/12.
 */
@Data
public class ImportCarVO implements Serializable {

    private String brandName;
    private String carTypeName;
    private String carName;

    public String getKey(){
        return brandName+"@@"+carTypeName;
    }

    public String getCarKey(){
        return brandName+"@@"+carTypeName+"@@"+carName;
    }
}
