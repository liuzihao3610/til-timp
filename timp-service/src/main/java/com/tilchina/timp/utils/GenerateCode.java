package com.tilchina.timp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generate some code, bad code
 * 
 * @version 1.0.0 2018/6/13
 * @author WangShengguang   
 */ 
public class GenerateCode {

    public static String getCode(){
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sf.format(new Date());
    }
}
