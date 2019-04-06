package com.tilchina.timp.test.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.manager.AutoAssemblyManager;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.service.FreightService;
import com.tilchina.timp.service.TransportOrderService;
import com.tilchina.timp.vo.AssemblyParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Created by demon on 2018/6/5.
 */
@RestController
@RequestMapping(value = {"/s1/test"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TestPriceController {

    @Autowired
    private FreightService freightService;

    @Autowired
    private TransportOrderService transportOrderService;

    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<BigDecimal> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            TransportOrder order = transportOrderService.queryById(param.getData());
            freightService.getPrice(order);
            BigDecimal d = freightService.getAllPrice(order);

            return ApiResult.success(d);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @Autowired
    private AutoAssemblyManager autoAssemblyManager;

    @RequestMapping(value = "/auto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> auto(@RequestBody ApiParam<AssemblyParam> param) {
        log.debug("get: {}", param);
        try {
//            autoAssemblyManager.assembly(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
