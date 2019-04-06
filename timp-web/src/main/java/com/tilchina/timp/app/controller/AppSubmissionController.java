package com.tilchina.timp.app.controller;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.TransportRecord;
import com.tilchina.timp.service.AppTransportRecordService;
import com.tilchina.timp.service.CarStatusService;


/**
* 在途提报
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/app/submission"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppSubmissionController extends BaseControllerImpl<CarStatus>{

	@Autowired
	private CarStatusService carstatusservice;
	
	@Autowired
	private AppTransportRecordService apptransportrecordservice;
	@Override
	protected BaseService<CarStatus> getService() {
		return carstatusservice;
	}
	
	@RequestMapping(value = "/updateBillStatus", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
    public ApiResult<String> updateBillStatus(@RequestBody AppApiParam<String> param,HttpServletRequest request) {
        log.debug("getCarList: {}", param);
        try {
        	String path=request.getRequestURI();
        	carstatusservice.updateBillStatus(param.getData(),path);
        	
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * app:定时（30分钟）上传运输记录
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/realDateUpdate", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
    public ApiResult<String> realDateUpdate(@RequestBody AppApiParam<TransportRecord> param) {
        log.debug("realDateUpdate: {}", param);
        try {
        	apptransportrecordservice.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加运输计划失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	
}
