package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.WebCheckLoginService;
import com.tilchina.timp.common.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 *
 * @version 1.0.0 2018/3/25
 * @author WangShengguang
 */
@Service
public class WebCheckLoginServiceImpl implements WebCheckLoginService {

    @Override
    public boolean check(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("env") == null){
            return false;
        }
        Environment env = (Environment) session.getAttribute("env");
        Environment.setEnv(env);
        return true;
    }
}
