package com.tilchina.timp.utils;

import java.util.Random;

/**
 * 创建系统用户时生成默认密码
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */ 
public class GenPassword {

    public static String get() {
        String pwd = "til@";
        Random r = new Random();
        return pwd + (r.nextInt(9999-1000+1)+1000);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(GenPassword.get());
        }
    }
}
