package com.tilchina.auth.manager;

import com.tilchina.auth.vo.UserInfoVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Xiahong
 */
public interface UnitLoginManager {

    UserInfoVO login(HttpServletRequest request, HttpServletResponse response, Map<String,Object> unitCode, String password);

}
