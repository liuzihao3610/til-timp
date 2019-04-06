package com.tilchina.timp.app.controller;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransferOrder;
import com.tilchina.timp.service.TransferOrderService;

/**
* 交接单状态
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/app/transfer"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppTransferStatusController{

	@Autowired
	private TransferOrderService transferorderservice;
	
	protected BaseService<TransferOrder> getService() {
		return transferorderservice;
	}
	
	//通过驾驶员ID获取未完成的运单
		@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		@Auth(ClientType.APP)
	    public ApiResult<List<Map<String, Object>>> getOrders(@RequestBody AppApiParam<Map<String, Object>> param ) {
	        log.debug("get: {}");
	        try {
	        	List<Map<String, Object>> pageInfo=transferorderservice.getOrders(param.getData());
	            return ApiResult.success(pageInfo);
	        } catch (Exception e) {
	            log.error("查询失败","param={}", e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }
	
	@RequestMapping(value = "/updateStatus", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
    public ApiResult<String> putPartList(@RequestBody AppApiParam<String> param) {
        log.debug("updateStatus: {}", param);
        try {
        	//TransferOrder t = (TransferOrder)JSON.parseObject(param.getData(), TransferOrder.class);
        	List<TransferOrder> t=JSONArray.parseArray(param.getData(), TransferOrder.class);
        	transferorderservice.updateByVin(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	

}
