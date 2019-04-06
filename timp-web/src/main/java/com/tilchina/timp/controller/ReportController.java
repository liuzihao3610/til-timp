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
import com.tilchina.timp.model.ReportOrder;
import com.tilchina.timp.service.ReportOrderService;
import com.tilchina.timp.vo.WayReportVO;

/**
 * 在途查询
 * @author XiaHong
 * @version 1.1.0
 */
@RestController
@RequestMapping(value = {"/s1/report"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class ReportController {

    @Autowired
    private ReportOrderService reportrrderservice;

    @RequestMapping(value = "/routeQuery", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<WayReportVO>> routeQuery(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("routeQuery: {}", param);
        try {
            PageInfo<WayReportVO> pageInfo = reportrrderservice.routeQuery(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("在途查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/queryList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<ReportOrder>> queryList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("ReportOrder: {}", param);
        try {
            PageInfo<ReportOrder> pageInfo = reportrrderservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("在途查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
