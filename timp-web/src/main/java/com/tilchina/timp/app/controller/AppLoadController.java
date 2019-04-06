package com.tilchina.timp.app.controller;

import com.alibaba.fastjson.JSONArray;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.model.TransferOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.service.StockCarService;

import com.tilchina.timp.service.TransportOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * 扫描装车
 *
 * @author LiuShuqi
 * @version 1.1.0
 */
@RestController
@RequestMapping(value = {"/s1/app/load"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppLoadController extends BaseControllerImpl<StockCar> {
	
	@Autowired
	private StockCarService stockCarService;

	@Autowired
	private TransportOrderDetailService transportOrderDetailService;

	@Override
	protected BaseService<StockCar> getService() {
		return stockCarService;
	}
	
	@RequestMapping(value = "/updateBillStatus", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<String> updateBillStatus(@RequestBody AppApiParam<String> param) {
		log.debug("getCarList: {}", param);
		try {
			List<TransportOrderDetail> t=JSONArray.parseArray(param.getData(), TransportOrderDetail.class);
			transportOrderDetailService.updateBillStatus(t);

			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("扫描失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	/**
	 * 获取扫描裝車列表
	 * @param param 订单ID
	 * @return
	 */
	@RequestMapping(value = "/getLoadList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<Map<String, Object>>> getLoadList(@RequestBody AppApiParam<Map<String, Object>> param) {
		log.debug("getLoadList: {}", param);
		try {
			List<Map<String,Object>> transportOrderDetails = transportOrderDetailService.getLoadList();
			return ApiResult.success(transportOrderDetails);
		} catch (Exception e) {
			log.error("查询失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}


}
