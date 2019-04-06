package com.tilchina.timp.util;

/**
 * Created by demon on 2018/6/19.
 */
public class IntegerUtil {

    public static boolean getBoolean(Integer i){
        if(i == null){
            return false;
        }
        if(i.intValue() == 1){
            return true;
        }
        return false;
    }
}
