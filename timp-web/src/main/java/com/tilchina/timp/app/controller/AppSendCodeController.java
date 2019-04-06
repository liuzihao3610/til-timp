package com.tilchina.timp.app.controller;

import com.tilchina.auth.Auth;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.service.DriverSettlementService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Captcha;
import com.tilchina.timp.service.CaptchaService;

/**
* 
*发送验证码
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/app/sendCode"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppSendCodeController extends BaseControllerImpl<Captcha>{

	@Autowired
	private CaptchaService captchaService;

    @Autowired
    private DriverSettlementService driverSettlementService;
	
	@Override
	protected BaseService<Captcha> getService() {
		return captchaService;
	}
	/**
	 * 发送单条验证码
	 * @param param
	 * @param request
	 * @return
	 */
    @RequestMapping(value = "/singleCode", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> sendCode(@RequestBody AppApiParam<Map<String, Object>> param,HttpServletRequest request) {
        log.debug("LoginController login: {}", param);
        try {
        	 String ip = request.getHeader("X-Real-IP");  
             if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {  
                  
             }  
             ip = request.getHeader("X-Forwarded-For");  
             if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {  
                 // 多次反向代理后会有多个IP值，第一个为真实IP。  
                 int index = ip.indexOf(',');  
                 if (index != -1) {  
                     ip=ip.substring(0, index);  
                 } else {  
                     ip=request.getHeader("X-Forwarded-For");;  
                 }  
             } else {  
                 ip=request.getRemoteAddr();  
             }
            String code= captchaService.sendCode(param.getData(),ip);
            return ApiResult.success(code);
        } catch (Exception e) {
            log.error("app 发送失败，param={}", param, e);
            return ApiResult.failure("9999",e.getMessage());

        }
    }
	/**
	 * 批量发送验证码
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/batchSend", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> batchSend(@RequestBody AppApiParam<Map<String, Object>> param,HttpServletRequest request) {
        log.debug("batchSend: {}", param);
        try {
        	String ip = request.getHeader("X-Real-IP");  
            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {  
                 
            }  
            ip = request.getHeader("X-Forwarded-For");  
            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {  
                // 多次反向代理后会有多个IP值，第一个为真实IP。  
                int index = ip.indexOf(',');  
                if (index != -1) {  
                    ip=ip.substring(0, index);  
                } else {  
                    ip=request.getHeader("X-Forwarded-For");;  
                }  
            } else {  
                ip=request.getRemoteAddr();  
            }
            String message=captchaService.batchSend(param.getData(),ip);
            return ApiResult.success(message);
        } catch (Exception e) {
            log.error("发送失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	/**
	 * 查询剩余条数
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/selectNumber", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<Integer> selectNumber(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("LoginController login: {}", param);
        try {
            int number= captchaService.selectNumber(param.getData());
            return ApiResult.success(number);
        } catch (Exception e) {
            log.error("app 查询失败，param={}", param, e);
            return ApiResult.failure("9999",e.getMessage());

        }
    }

    /**
     * 发送运单验证码
     * @param param
     * @param request
     * @return
     */
    @RequestMapping(value = "/business", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<String> Business(@RequestBody AppApiParam<Map<String, Object>> param,HttpServletRequest request) {
        log.debug("LoginController login: {}", param);
        try {
            String ip = request.getHeader("X-Real-IP");
            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {

            }
            ip = request.getHeader("X-Forwarded-For");
            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip=ip.substring(0, index);
                } else {
                    ip=request.getHeader("X-Forwarded-For");;
                }
            } else {
                ip=request.getRemoteAddr();
            }
            StringBuilder content = new StringBuilder();
            content.append("【锦浩共源】结算单号："+param.getData().get("settlementCode")+",结算月份："+param.getData().get("settlementMonth")+"，请您确认是本人操作，并且结算信息及金额准确无误。验证码为：");
            param.getData().put("content",content);
            String code= captchaService.business(param.getData(),ip);
            return ApiResult.success(code);
        } catch (Exception e) {
            log.error("app 发送失败，param={}", param, e);
            return ApiResult.failure("9999",e.getMessage());

        }
    }

    /**
     * 验证运单验证码
     * @param param
     * @return
     */
    @RequestMapping(value = "/validationCode", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<String> validationCode(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("validationCode  {}", param);
        try {
            Boolean validationCode = captchaService.validationCode(param.getData());
            driverSettlementService.voidorderReceiving(Long.valueOf(param.getData().get("settlementId").toString()));
            return ApiResult.success("确认成功！");
        } catch (Exception e) {
            log.error("app 发送失败，param={}", param, e);
            return ApiResult.failure("9999",e.getMessage());

        }
    }
	
}
