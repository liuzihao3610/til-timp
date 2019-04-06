package com.tilchina.auth.manager;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */ 
public interface WebCheckLoginService {

    boolean check(HttpServletRequest request);
}
