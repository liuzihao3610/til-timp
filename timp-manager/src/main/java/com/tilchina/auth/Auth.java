package com.tilchina.auth;

import com.tilchina.timp.enums.ClientType;

import java.lang.annotation.*;

/**
 * 
 * 
 * @version 1.0.0 2018/3/23
 * @author WangShengguang   
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Auth {

    ClientType value() default ClientType.WEB;
}
