package com.tilchina.timp.app.controller;

import com.tilchina.auth.Auth;
import com.tilchina.auth.manager.AppLoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.service.AppLoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/4/2
 */
@RestController
@RequestMapping(value = {"/s1/app"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppLoginController {

    @Autowired
    private AppLoginManager appLoginManager;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> login(@RequestBody AppApiParam<Map<String, String>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}", param);
        try {
            Map<String, String> map = param.getData();
            String userCode = map.get("userCode");
            String password = map.get("password");
            String identifier = map.get("identifier");
            if (StringUtils.isBlank(userCode) || StringUtils.isBlank(password) || StringUtils.isBlank(identifier)) {
                throw new BusinessException("9003");
            }

            UserInfoVO info = appLoginManager.login(request, response, userCode, password, identifier);

            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("app 登录失败，param={}", param, e);
            return ApiResult.failure(e);

        }
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> captcha(@RequestBody AppApiParam<Map<String, String>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}", param);
        try {
            Map<String, String> map = param.getData();
            String phoneNumber = map.get("phoneNumber");
            String captcha = map.get("captcha");
            String identifier = map.get("identifier");
            if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(captcha) || StringUtils.isBlank(identifier)) {
                throw new BusinessException("9003");
            }

            UserInfoVO info = appLoginManager.loginWithCaptcha(request, response, phoneNumber, captcha, identifier);

            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("app 登录失败，param={}", param, e);
            return ApiResult.failure(e);

        }
    }

    @RequestMapping(value = "/relogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> relogin(@RequestBody AppApiParam<Map<String, String>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}", param);
        try {
            Map<String, String> map = param.getData();
            String userId = map.get("userId");
            String k = map.get("k");
            String t = map.get("t");
            String i = map.get("i");
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(k) || StringUtils.isBlank(t) || StringUtils.isBlank(i)) {
                throw new BusinessException("9003");
            }

            UserInfoVO info = appLoginManager.relogin(request, response, Long.valueOf(userId), k, t, i);
            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("app 登录失败，param={}", param, e);
            return ApiResult.failure(e);

        }
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<UserInfoVO> getUserInfo(@RequestBody AppApiParam<Map<String, String>> param) {
        log.debug("LoginController login: {}", param);
        try {
            UserInfoVO info = appLoginManager.getUserInfo();
            return ApiResult.success(info);
        } catch (Exception e) {
            return ApiResult.failure(e);

        }
    }


}
