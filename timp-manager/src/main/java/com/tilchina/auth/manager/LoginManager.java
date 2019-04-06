package com.tilchina.auth.manager;

import com.tilchina.auth.vo.UserInfoVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */ 
public interface LoginManager {

    UserInfoVO login(HttpServletRequest request, HttpServletResponse response, String userCode, String password);

    UserInfoVO cutCorp(HttpServletRequest request, HttpServletResponse response,Long corpId);

}
