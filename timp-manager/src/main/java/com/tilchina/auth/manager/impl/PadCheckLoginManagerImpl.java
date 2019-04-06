package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.AppCheckLoginService;
import com.tilchina.auth.manager.PadCheckLoginManager;
import com.tilchina.catalyst.utils.MD5;
import com.tilchina.catalyst.utils.PBKDF2;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.model.AppLogin;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.AppLoginService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.TransporterService;
import com.tilchina.timp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */
@Service
@Slf4j
public class PadCheckLoginManagerImpl implements PadCheckLoginManager {

    @Autowired
    private AppLoginService appLoginService;

    @Autowired
    private UserService userService;

    @Autowired
    private CorpService corpService;

    @Autowired
    private TransporterService transporterService;

    @Override
    public boolean check(HttpServletRequest request, String hashed, String salt, String identifier, Long userId) {
//        HttpSession session = request.getSession();
//        Environment env = (Environment) session.getAttribute("env");
//        if(env == null ){
//            return false;
//        }


        AppLogin applogin = appLoginService.queryByUserId(userId);
        if(applogin == null){
            return false;
        }
        try {
            if(!salt.equals(applogin.getSpecial())) {
                if (PBKDF2.validatePassword(applogin.getToken(), hashed, salt, 2)) {
                    if (PBKDF2.validatePassword(applogin.getIdentifier(), identifier, salt, 2)) {
                        String userAgent = request.getHeader("User-Agent");
                        if (MD5.MD5(userAgent).equals(applogin.getUserAgent())) {

                            User user = userService.queryById(userId);
                            Corp corp = corpService.queryById(user.getCorpId());
                            Transporter trans = transporterService.queryByDriverId(user.getUserId());
                            Environment env = new Environment();
                            env.setUser(user);
                            env.setCorp(corp);
                            env.setTransporter(trans);
                            Environment.setEnv(env);


                            AppLogin newLogin = new AppLogin();
                            newLogin.setAppLoginId(applogin.getAppLoginId());
                            newLogin.setSpecial(salt);
                            appLoginService.updateSelective(newLogin);
                            return true;
                        }
                    }
                }
            }
        }catch(Exception e){
            log.error("App 登录检查失败！userId:{},token:{},hashed:{},salt:{}",applogin.getUserId(),applogin.getToken(),hashed,salt);
        }
        cleanLoginInfo(applogin);
        return false;
    }

    @Override
    public boolean checkReLogin(HttpServletRequest request, String hashed, String salt, String identifier, Long userId) {
        AppLogin applogin = appLoginService.queryByUserId(userId);
        try {
            if(!salt.equals(applogin.getSpecial())) {
                if (PBKDF2.validatePassword(applogin.getToken(), hashed, salt, 2)) {
                    if (PBKDF2.validatePassword(applogin.getIdentifier(), identifier, salt, 2)) {
                        String userAgent = request.getHeader("User-Agent");
                        if (MD5.MD5(userAgent).equals(applogin.getUserAgent())) {
                            AppLogin newLogin = new AppLogin();
                            newLogin.setAppLoginId(applogin.getAppLoginId());
                            newLogin.setSpecial(salt);
                            appLoginService.updateSelective(newLogin);
                            return true;
                        }
                    }
                }
            }
        }catch(Exception e){
            log.error("App 重新登录检查失败！userId:{},token:{},hashed:{},salt:{}",applogin.getUserId(),applogin.getToken(),hashed,salt);
        }
        cleanLoginInfo(applogin);
        return false;
    }

    @Override
    public void cleanLoginInfo(AppLogin applogin){
        appLoginService.cleanById(applogin.getAppLoginId());
    }
}
