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
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransporterReport;
import com.tilchina.timp.service.TransporterReportService;

/**
* 骄运车状态变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transporterreport"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransporterReportController{

	@Autowired
	private TransporterReportService transporterreportservice;
	
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<TransporterReport>> queryHistory(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransporterReport> pageInfo = transporterreportservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransporterReport>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransporterReport> pageInfo = transporterreportservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
}
