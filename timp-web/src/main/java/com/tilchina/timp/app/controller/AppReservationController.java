package com.tilchina.timp.app.controller;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.LoadingReservation;
import com.tilchina.timp.service.AppReservationService;

/**
* 预约装车
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/app/reservation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppReservationController{
	@Autowired
	private AppReservationService appReservationService;
	
	/**
	 * 历史预约记录查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<LoadingReservation>> query(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	List<LoadingReservation> loadingReservations = appReservationService.queryList(param.getData());
            log.debug("get result: {}", loadingReservations);
            return ApiResult.success(loadingReservations);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 预约
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/req", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<String> putStatus(@RequestBody AppApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            LoadingReservation t = JSON.parseObject(param.getData(), LoadingReservation.class);
            appReservationService.appointment(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<LoadingReservation>> getList(@RequestBody AppApiParam<String> param) {
        try {
        	List<LoadingReservation> reservations = appReservationService.queryList();
            log.debug("get result: {}", reservations);
            return ApiResult.success(reservations);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
}
