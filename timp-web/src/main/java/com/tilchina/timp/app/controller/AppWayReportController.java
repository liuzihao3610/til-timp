package com.tilchina.timp.app.controller;

import com.alibaba.fastjson.JSON;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.WayReport;
import com.tilchina.timp.service.CarStatusService;
import com.tilchina.timp.service.WayReportService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
* 在途提报
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/app/wayReport"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppWayReportController extends BaseControllerImpl<WayReport>{

	@Autowired
	private WayReportService wayReportService;
	
	@Override
	protected BaseService<WayReport> getService() {
		return wayReportService;
	}
	
	@RequestMapping(value = "/submission", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP) 
    public ApiResult<String> submission(
    									@RequestParam("appLogin") String appLogin,
    									@RequestParam("wayReport") String json,
										@RequestParam("files") MultipartFile[] files) {
        log.debug("submission: {}", json);
        try {
        	
        	wayReportService.submission(json,files);
        	
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("操作失败，param={}", json, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/getByDriverId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
    public ApiResult<List<WayReport>> getByDriverId(@RequestBody AppApiParam<WayReport> param) {
        log.debug("getByDriverId: {}", param);
        try {
        	
        	List<WayReport> wayReports=wayReportService.getByDriverId();
        	
            return ApiResult.success(wayReports);
        } catch (Exception e) {
            log.error("操作失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	

	
	
	
}
