package com.tilchina.auth;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.tilchina.auth.manager.AppCheckLoginService;
import com.tilchina.auth.manager.WebCheckLoginService;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.LoginErrorException;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.util.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/3/23
 */
@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Autowired
    private WebCheckLoginService webCheckLoginService;

    @Autowired
    private AppCheckLoginService appCheckLoginService;

    @Pointcut(value = "@annotation(com.tilchina.auth.Auth)")
    public void authAspect() {
    }

    @Around(value = "authAspect() && @annotation(auth)")
    public Object arround(ProceedingJoinPoint point, Auth auth) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        Object[] args = point.getArgs();
        AppApiParam param = null;
        if(auth.value() == ClientType.APP) {
            if (args[0] instanceof AppApiParam) {
                param = (AppApiParam) args[0];
            } else {
                param = JSON.parseObject(args[0].toString(), AppApiParam.class);
            }
        }
        try {
            if (!check(request, auth.value(), param)) {
                throw new LoginErrorException("911");
            }
            if (auth.value() == ClientType.WEB){
                HttpSession session = request.getSession();
                // 获取请求的相对url
                String url = request.getServletPath().replaceAll("/s1","");
                String path = CheckUtil.checkUrl(url);
                Map<String,Object> map = new HashMap<String,Object>(){
                    {
                        put("/web/login","/web/login");
                        put("/regist/getRegist","/regist/getRegist");
                        put("/regist/getButtonRegist","/regist/getButtonRegist");
                        put("getReferenceList","getReferenceList");
                        put("getDetailReference","getDetailReference");
                    }
                };
                if (map.get(url) == null && session.getAttribute("env") != null && map.get(path) == null){
                    Environment environment =  (Environment)session.getAttribute("env");
                    // 用户登陆之后
                    if(environment.getUser() != null){
                        // 获取当前登陆用户所有访问权限
                        List<Regist> regists = Optional.ofNullable( environment.getRegists()).orElse(new ArrayList<Regist>());
                        List<String> registUrls = new ArrayList<String>();
                        regists.forEach(regist -> registUrls.add(regist.getUrlPath()));
                        List<String> collect = registUrls.stream().filter(urlPath -> !StringUtil.isEmpty(urlPath) && url.equals(urlPath) ).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(collect)){
//                            System.out.println("您没有此节点的访问权限。");
//                            throw new LoginErrorException("900");
                        }
                    }
                }
            }
            Object result = point.proceed();
            return result;
        } catch (Throwable e) {
            log.error("check login error!",e);
            if (e instanceof BaseException) {
                return ApiResult.failure((BaseException) e);
            } else {
                return ApiResult.failure("9999", e.getMessage());
            }
        }

    }

    private boolean check(HttpServletRequest request, ClientType clientType, AppApiParam param) {
        switch (clientType){
            case WEB: return webCheckLoginService.check(request);
            case APP: return appCheckLoginService.check(request,param.getK(),param.getT(),param.getI(),param.getU());
            case WX: return false;
            case PAD: return false;
            case TRANSPORT: return false;
            default: return false;
        }
    }

}
