package com.tilchina.timp.app.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.TransportRecord;
import com.tilchina.timp.service.CarStatusService;
import com.tilchina.timp.service.TransportRecordService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
* 位置自动上传
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/app/upLocation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppUpLocationController extends BaseControllerImpl<TransportRecord>{

	@Autowired
	private TransportRecordService transportRecordService;
	
	@Override
	protected BaseService<TransportRecord> getService() {
		return transportRecordService;
	}
	
	@RequestMapping(value = "/upLoad", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
    public ApiResult<String> upLoad(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("getCarList: {}", param);
        try {
        	
        	transportRecordService.upLoad(param.getData());
        	
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("上传失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	

	
	
}
