package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.DamagePosition;
import com.tilchina.timp.service.DamagePositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 质损部位档案
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/damageposition"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DamagePositionController extends BaseControllerImpl<DamagePosition>{

	@Autowired
	private DamagePositionService damagePositionService;
	
	@Override
	protected BaseService<DamagePosition> getService() {
		return damagePositionService;
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<DamagePosition> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<List<DamagePosition>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {

		try {
			DamagePosition damagePosition = JSON.parseObject(param.getData(), DamagePosition.class);
			damagePositionService.add(damagePosition);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {

		try {
			DamagePosition damagePosition = JSON.parseObject(param.getData(), DamagePosition.class);
			damagePositionService.updateSelective(damagePosition);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> put(@RequestBody ApiParam<String> param) {
		return super.put(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
		return super.delete(param);
	}
}
