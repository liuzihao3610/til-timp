package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.AppCheckLoginService;
import com.tilchina.auth.manager.AppLoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.utils.BCryptUtil;
import com.tilchina.catalyst.utils.IpUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.constant.CaptchaConstant;
import com.tilchina.timp.expection.LoginErrorException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/4/1
 */
@Service
public class AppLoginManagerImpl implements AppLoginManager {

    @Autowired
    private LoginService loginService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CorpService corpService;

    @Autowired
    private AppLoginService appLoginService;

    @Autowired
    private TransporterService transporterService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private AppCheckLoginService appCheckLoginService;

    @Override
    public UserInfoVO login(HttpServletRequest request, HttpServletResponse response, String userCode, String password, String identifier) {
        Login login = loginService.queryByUserCode(userCode, 1);
        if (login == null) {
            throw new LoginErrorException("901");
        }
        if (!BCryptUtil.check(password, login.getPassword())) {
            throw new LoginErrorException("902");
        }

        return success(request, response, login.getUserId(), identifier, true);
    }

    @Override
    public UserInfoVO loginWithCaptcha(HttpServletRequest request, HttpServletResponse response, String phoneNumber, String captcha, String identifier) {
        User user = driverService.queryByPhone(phoneNumber);
        if (user == null) {
            throw new LoginErrorException("901");
        }

        Captcha code = captchaService.queryCaptcha(phoneNumber, CaptchaConstant.LOGIN);
        if (code == null) {
            throw new LoginErrorException("9901");
        }
        if (code.getSendTime().getTime() - new Date().getTime() > 15 * 60 * 1000) {
            throw new LoginErrorException("9901");
        }

        code.setCaptchaStatus(1);
        captchaService.updateSelective(code);

        if (!code.getCaptchaCode().equals(captcha)) {
            throw new LoginErrorException("9902");
        }

        return success(request, response, user.getUserId(), identifier, false);
    }

    @Override
    public UserInfoVO relogin(HttpServletRequest request, HttpServletResponse response, Long userId, String hashed, String salt, String identifier) {
        if (appCheckLoginService.checkReLogin(request, hashed, salt, identifier, userId)) {
            return success(request, response, userId, null, false);
        } else {
            throw new LoginErrorException("911");
        }
    }

    @Override
    public UserInfoVO getUserInfo() {
        Environment env=Environment.getEnv();
        User user = driverService.queryById(env.getUser().getUserId());
        Corp corp = corpService.queryById(user.getCorpId());
        Transporter trans = transporterService.queryByDriverId(user.getUserId());
        UserInfoVO info = new UserInfoVO();
        info.setUser(user);
        info.setCorp(corp);
        info.setTransporter(trans);
        return info;
    }

    private UserInfoVO success(HttpServletRequest request, HttpServletResponse response, Long userId, String identifier, boolean check) {

        User user = driverService.queryById(userId);
        Corp corp = corpService.queryById(user.getCorpId());
        Transporter trans = transporterService.queryByDriverId(user.getUserId());
        Environment env = new Environment();
        env.setUser(user);
        env.setCorp(corp);
        env.setTransporter(trans);
        Environment.setEnv(env);

        String ip = IpUtils.getIp(request);
        Date loginTime = new Date();
        String userAgent = request.getHeader("User-Agent");
        String token = appLoginService.login(identifier, ip, userAgent, loginTime, check);

        UserInfoVO info = new UserInfoVO();
        info.setUser(user);
        info.setCorp(corp);
        info.setTransporter(trans);
        info.setToken(token);

        HttpSession session = request.getSession();
        session.setAttribute("env", env);
        //CookieUtils.addCookie(response, LoginConstant.PRARM_LOGIN_TIME,String.valueOf(loginTime.getTime()),LoginConstant.PARAM_DOMAIN,-1);
        if (user.getBindingPhone() == 0) {
            user.setBindingPhone(1);
            driverService.updateSelective(user);
        }
        return info;
    }

}
