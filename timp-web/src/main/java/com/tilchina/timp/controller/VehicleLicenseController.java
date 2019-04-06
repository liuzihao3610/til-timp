package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.VehicleLicense;
import com.tilchina.timp.service.VehicleLicenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 证件管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/vehiclelicense"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class VehicleLicenseController extends BaseControllerImpl<VehicleLicense>{

	@Autowired
	private VehicleLicenseService vehiclelicenseservice;
	
	@Override
	protected BaseService<VehicleLicense> getService() {
		return vehiclelicenseservice;
	}

	@PostMapping("setDocumentsAudited")
	@Auth(ClientType.WEB)
	public ApiResult<String> setDocumentsAudited(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			vehiclelicenseservice.setDocumentsCheckStatus(params.getData().get("licenseId"), true);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@PostMapping("setDocumentsUnaudited")
	@Auth(ClientType.WEB)
	public ApiResult<String> setDocumentsUnaudited(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			vehiclelicenseservice.setDocumentsCheckStatus(params.getData().get("licenseId"), false);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<VehicleLicense> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<List<VehicleLicense>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {
		return super.post(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
		return super.putPart(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> put(@RequestBody ApiParam<String> param) {
		return super.put(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
		return super.delete(param);
	}
}
