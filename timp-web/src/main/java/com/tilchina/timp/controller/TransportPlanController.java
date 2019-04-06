package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
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
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.TransportPlan;
import com.tilchina.timp.model.TransportRecordOuter;
import com.tilchina.timp.service.TransportPlanService;

/**
* 运输计划表
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transportplan"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportPlanController /*extends BaseControllerImpl<TransportPlan>*/{

	@Autowired
	private TransportPlanService transportplanservice;
	
	protected BaseService<TransportPlan> getService() {
		return transportplanservice;
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<TransportPlan> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportPlan t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<TransportPlan>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransportPlan> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    

    @RequestMapping(value = "/getPlanList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<TransportPlan>> getPlanList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
        	if(param.getData().get("transportOrderId") == null) {
        		throw new BusinessException("请传入运单ID");
        	}
            PageInfo<TransportPlan> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/reviseApprove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> reviseApprove(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            TransportPlan t = (TransportPlan)JSON.parseObject(param.getData(), TransportPlan.class);
            TransportPlan transportPlan = null;
            if(t.getApprove() != null && t.getTransportPlanId() != null) {
        	   transportPlan = new TransportPlan();
        	   transportPlan.setTransportPlanId(t.getTransportPlanId());
        	   transportPlan.setApprove(t.getApprove());
            }else {
            	throw new BusinessException("传入参数错误！请将运输计划得核准公里数、运输计划id传入！");
            }
            getService().updateSelective(transportPlan);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	transportplanservice.logicDeleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteList(@RequestBody ApiParam<Integer[]> param) {
        log.debug("remove: {}", param);
        try {
        	transportplanservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
}
