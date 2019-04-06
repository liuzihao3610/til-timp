package com.tilchina.timp.controller;

import com.tilchina.auth.manager.UnitLoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.expection.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @version 1.0.0 2018/7/16
 * @author Xiahong
 */
@RestController
@RequestMapping(value = {"/s1/unit/web"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class UnitLoginController {

    @Autowired
    private UnitLoginManager unitLoginManager;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> login(@RequestBody ApiParam<Map<String, String>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}", param);
        try {
            Map<String,String> map = param.getData();
           /* String userCode = map.get("unitCode");*/
            String userCode = map.get("userCode");
            String password = map.get("password");
            if(StringUtils.isBlank(userCode) || StringUtils.isBlank(password)){
                throw new BusinessException("9003");
            }

            Map<String,Object> unitUserCodeMap = new HashMap<>();
            unitUserCodeMap.put("dealerCode",userCode);
            unitUserCodeMap.put("unitCode",userCode);
            UserInfoVO info = unitLoginManager.login(request, response, unitUserCodeMap, password);
         /*   UserInfoVO info = loginManager.login(request, response, userCode, password);*/

            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("收发货单位 登录失败，param={}", param, e);
            return ApiResult.failure(e);

        }
    }

    @RequestMapping(value = "/l", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> loginGet(@RequestParam("u") String userCode,@RequestParam("p") String password, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}");
        try {

            if(StringUtils.isBlank(userCode) || StringUtils.isBlank(password)){
                throw new BusinessException("9003");
            }
            Map<String,Object> unitUserCodeMap = new HashMap<>();
            unitUserCodeMap.put("dealerCode",userCode);
            unitUserCodeMap.put("userCode",userCode);
            UserInfoVO info = unitLoginManager.login(request, response, unitUserCodeMap, password);

            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("收发货单位 登录失败，param={}", e);
            return ApiResult.failure(e);
        }
    }

}
