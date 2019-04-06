package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.service.RegistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
* 品牌档案
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/test"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TestController {

    @Autowired
    private RegistService registerService;

	@RequestMapping(value = "/t", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> delete(HttpServletRequest request) {
        log.debug("remove: {}");
        try {
            String value = LanguageUtil.getMessage("0000");
            Environment env = Environment.getEnv();
            env.getUser().getUserId();
            env.getCorp().getCorpId();
            log.error("bundle test : {}",value);
            return ApiResult.success(value);
        } catch (Exception e) {
            log.error("删除失败，param={}", "","",e);
            return ApiResult.failure(e);
        }
    }

    @RequestMapping(value = "/t4", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> t4(HttpServletRequest request) {
        log.debug("remove: {}");
        try {
            String value = LanguageUtil.getMessage("9003","第2个","第1个","第3个","第4个");
            log.error("bundle test : {}",value);
            return ApiResult.success(value);
        } catch (Exception e) {
            log.error("删除失败，param={}", "","",e);
            return ApiResult.failure(e);
        }
    }

    @RequestMapping(value = "/t1", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> t1(HttpServletRequest request) {
        log.debug("remove: {}");
        try {
            if(true){
                throw new Exception("异常测试");
            }
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", "","",e);
            return ApiResult.failure(e);
        }
    }

    @RequestMapping(value = "/t2", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> t2(HttpServletRequest request) {
        log.debug("remove: {}");
        try {
            if(true){
                throw new BusinessException("9001","品牌名称");
            }
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", "","",e);
            return ApiResult.failure(e);
        }
    }

    @RequestMapping(value = "/t3", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> t3(HttpServletRequest request) {
        log.debug("remove: {}");
        try {
            try {
                if (true) {
                    throw new BusinessException("9001","品牌名称");
                }
            }catch(Exception ex){
                throw new BusinessException("9002",ex,"品牌档案",ex.getMessage());
            }
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", "","",e);
            return ApiResult.failure(e);
        }
    }

    @PostMapping("/addUrls")
    @Auth(ClientType.WEB)
    public ApiResult<String> addUrls() {

	    registerService.addUrls();
        return ApiResult.success();
    }
}
