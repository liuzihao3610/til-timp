package com.tilchina.auth.manager;

import com.tilchina.timp.model.AppLogin;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */ 
public interface AppCheckLoginService {

    boolean check(HttpServletRequest request, String hashed, String salt, String identifier, Long u);

    boolean checkReLogin(HttpServletRequest request, String hashed, String salt, String identifier, Long userId);

    void cleanLoginInfo(AppLogin applogin);
}
