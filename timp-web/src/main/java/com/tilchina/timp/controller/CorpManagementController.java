package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CorpManagement;
import com.tilchina.timp.service.CorpManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 公司管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/corpmanagement"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CorpManagementController extends BaseControllerImpl<CorpManagement>{

	@Autowired
	private CorpManagementService corpManagementService;
	
	@Override
	protected BaseService<CorpManagement> getService() {
		return corpManagementService;
	}

	@PostMapping("getManagement")
	@Auth(ClientType.WEB)
	public ApiResult<List<CorpManagement>> getManagement(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			List<CorpManagement> corps = corpManagementService.getManagement(params.getData());
			return ApiResult.success(corps);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("addManagement")
	@Auth(ClientType.WEB)
	public ApiResult<String> addManagement(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			corpManagementService.addManagement(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("delManagement")
	@Auth(ClientType.WEB)
	public ApiResult<String> delManagement(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			corpManagementService.delManagement(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("getSelfCorpList")
	@Auth(ClientType.WEB)
	public ApiResult<List<Map<String, Object>>> getSelfCorpList() {
		try {
			List<Map<String, Object>> corpList = corpManagementService.getSelfCorpList();
			return ApiResult.success(corpList);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
