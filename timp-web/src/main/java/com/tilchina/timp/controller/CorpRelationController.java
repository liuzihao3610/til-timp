package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.CorpRelation;
import com.tilchina.timp.service.CorpRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 公司关系表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/corprelation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CorpRelationController extends BaseControllerImpl<CorpRelation>{

	@Autowired
	private CorpRelationService corpRelationService;
	
	@Override
	protected BaseService<CorpRelation> getService() {
		return corpRelationService;
	}

	@PostMapping("getRelation")
	@Auth(ClientType.WEB)
	public ApiResult<List<Corp>> getRelation(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			List<Corp> corps = corpRelationService.getRelation(params.getData());
			return ApiResult.success(corps, null, corps.size());
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("addRelation")
	@Auth(ClientType.WEB)
	public ApiResult<String> addRelation(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			corpRelationService.addRelation(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("delRelation")
	@Auth(ClientType.WEB)
	public ApiResult<String> delRelation(@RequestBody ApiParam<Map<String, Object>> params) {
		try {
			corpRelationService.delRelation(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
