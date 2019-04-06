package com.tilchina.timp.app.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Push;
import com.tilchina.timp.service.PushService;
import lombok.extern.slf4j.Slf4j;

/**
* APP:极光推送
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/app/jgPush"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppPushController{
	
	@Autowired
	private PushService pushservice;
	
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<Push> getById(@RequestBody AppApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            Push t = pushservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<List<Push>> getList(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Push> pageInfo = pushservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 添加推送信息
     * @param param
     * @return
     */
    @RequestMapping(value = "/addPush", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
   /* @Auth(ClientType.APP)*/
    public ApiResult<String> push(@RequestBody AppApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Push push = (Push)JSON.parseObject(param.getData(), Push.class);
            pushservice.addPush(push);
            return ApiResult.success("添加推送信息成功");
        } catch (Exception e) {
            log.error("添加推送信息失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    /**
     * 添加推送信息
     * @param param
     * @return
     */
    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> driverush(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Push push = (Push)JSON.parseObject(param.getData(), Push.class);
            pushservice.addPush(push);
            return ApiResult.success("添加推送信息成功");
        } catch (Exception e) {
            log.error("添加推送信息失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}