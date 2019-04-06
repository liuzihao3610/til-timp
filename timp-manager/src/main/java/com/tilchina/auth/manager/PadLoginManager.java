package com.tilchina.auth.manager;

import com.tilchina.auth.vo.UserInfoVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 
 * @version 1.0.0 2018/4/1
 * @author WangShengguang   
 */ 
public interface PadLoginManager {

    public UserInfoVO login(HttpServletRequest request, HttpServletResponse response, String userCode, String password, String identifier);

    UserInfoVO loginWithCaptcha(HttpServletRequest request, HttpServletResponse response, String phoneNumber, String captcha, String identifier);

    UserInfoVO relogin(HttpServletRequest request, HttpServletResponse response, Long userId, String hashed, String salt, String identifier);
}
