package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.auth.manager.LoginManager;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 *
 * @version 1.0.0 2018/4/2
 * @author WangShengguang
 */
@RestController
@RequestMapping(value = {"/s1/web"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class WebLoginController {

    @Autowired
    private LoginManager loginManager;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserInfoVO> login(@RequestBody ApiParam<Map<String, String>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("LoginController login: {}", param);
        try {
            Map<String,String> map = param.getData();
            String userCode = map.get("userCode");
            String password = map.get("password");
            if(StringUtils.isBlank(userCode) || StringUtils.isBlank(password)){
                throw new BusinessException("9003");
            }

            UserInfoVO info = loginManager.login(request, response, userCode, password);
            info.getUser().setRefCorpName("");
            info.getCorp().setCorpName("");
            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("app 登录失败，param={}", param, e);
            return ApiResult.failure(e);
        }
    }

    @RequestMapping(value = "/cutCorp", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<UserInfoVO> cutCorp(@RequestBody ApiParam<Map<String, Long>> param, HttpServletRequest request, HttpServletResponse response) {
        log.debug("cutCorp login: {}", param);
        try {
            Map<String,Long> map = param.getData();
            Long  corpId = map.get("corpId");
            UserInfoVO userInfoVO = loginManager.cutCorp(request,response,corpId);
            return ApiResult.success(userInfoVO);
        } catch (Exception e) {
            log.error("web 切换公司信息失败，param={}", param, e);
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
            
            UserInfoVO info = loginManager.login(request, response, userCode, password);
            info.getUnit().setRefCorpName("");
            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("app 登录失败，param={}", e);
            return ApiResult.failure(e);
        }
    }

}
