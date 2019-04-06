package com.tilchina.timp.app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransporterAndDriver;
import com.tilchina.timp.service.AppDriverService;
import lombok.extern.slf4j.Slf4j;

/**
* APP:获取车辆信息
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/app/driver"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppDriverController {
	
	@Autowired
	private AppDriverService appdriverservice;
	
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<TransporterAndDriver>> getById(@RequestBody AppApiParam<String> param) {
        try {
        	List<TransporterAndDriver> t = appdriverservice.queryList();
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
}
