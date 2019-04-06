package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportRecord;
import com.tilchina.timp.service.TransportRecordService;

/**
* 运输记录表
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/transportrecord"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportRecordController {

	@Autowired
	private TransportRecordService transportrecordservice;
	
	protected BaseService<TransportRecord> getService() {
		return transportrecordservice;
	}
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<TransportRecord> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportRecord t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<TransportRecord>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransportRecord> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
