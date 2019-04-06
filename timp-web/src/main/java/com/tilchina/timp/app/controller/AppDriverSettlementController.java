package com.tilchina.timp.app.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.DriverSettlement;
import com.tilchina.timp.service.DriverSettlementService;
import com.tilchina.timp.vo.DriverSettlementVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import com.tilchina.catalyst.param.AppApiParam;
/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/app/driversettlement"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppDriverSettlementController {

	@Autowired
	private DriverSettlementService driversettlementservice;


    @RequestMapping(value = "/query", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<List<DriverSettlement>> getList(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<DriverSettlement> pageInfo = driversettlementservice.appQueryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetails", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult< List<DriverSettlementVO>> getDetails(@RequestBody AppApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            List<DriverSettlementVO> t = driversettlementservice.queryDetailsById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
