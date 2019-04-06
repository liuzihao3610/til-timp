package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Route;
import com.tilchina.timp.model.RouteDetail;
import com.tilchina.timp.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 虚拟路线档案
*
* @version 1.0.0
* @author WangShengguang
*/
@RestController
@RequestMapping(value = {"/s1/route"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class RouteController {

	@Autowired
	private RouteService routeService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> add(@RequestBody ApiParam<Route> param) {
        log.debug("add: {}", param);
        try {
            routeService.add(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败！，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/update",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> update(@RequestBody ApiParam<Route> param) {
        log.debug("update: {}", param);
        try {
            routeService.update(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("更新失败！，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/remove",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            routeService.delete(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@RequestMapping(value = "/query",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Route>> queryList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("query: {}",param);
        try {
            
        	PageInfo<Route> pageInfo =routeService.queryList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/ref",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Route>> queryRef(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("queryRef: {}",param);
        try {
            PageInfo<Route> pageInfo =routeService.queryList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/queryDetail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<RouteDetail>> queryDetail(@RequestBody ApiParam<Long> param) {
        log.debug("queryDetail: {}",param);
        try {
            
        	List<RouteDetail> details =routeService.queryDetails(param.getData());
            return ApiResult.success(details);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
