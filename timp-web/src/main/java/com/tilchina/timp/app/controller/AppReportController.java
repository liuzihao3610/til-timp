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
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.DriverReport;
import com.tilchina.timp.model.TransporterReport;
import com.tilchina.timp.service.DriverReportService;
import com.tilchina.timp.service.TransporterReportService;

import lombok.extern.slf4j.Slf4j;

/**
 * 状态提报
 * @author Xiahong
 *
 */
@RestController
@RequestMapping(value = {"/s1/app/report"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppReportController {
	
	@Autowired
	private DriverReportService driverreportservice;
	
	@Autowired
	private TransporterReportService transporterreportservice;
	
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<DriverReport>> queryHistory(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<DriverReport> pageInfo = driverreportservice.queryByDriverIdList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/driver", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<String> putDriver(@RequestBody AppApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            DriverReport t = JSON.parseObject(param.getData(), DriverReport.class);
            driverreportservice.report(t);
            return ApiResult.success("司机提报成功");
        } catch (Exception e) {
            log.error("司机提报失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/trans", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<String> putTransporter(@RequestBody AppApiParam<String> param) {
        log.debug("post: {}", param);
        try {
        	TransporterReport t = JSON.parseObject(param.getData(), TransporterReport.class);
        	transporterreportservice.report(t);
            return ApiResult.success("轿运车提报成功");
        } catch (Exception e) {
            log.error("轿运车提报失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/dt", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<String> putPart(@RequestBody AppApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            DriverReport t = JSON.parseObject(param.getData(), DriverReport.class);
            driverreportservice.dt(t);
            return ApiResult.success("空闲提报成功");
        } catch (Exception e) {
            log.error("空闲提报失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
