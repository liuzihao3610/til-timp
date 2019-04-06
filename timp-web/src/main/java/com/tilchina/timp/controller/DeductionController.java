package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Deduction;
import com.tilchina.timp.service.DeductionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 扣款项目
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/deduction"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DeductionController extends BaseControllerImpl<Deduction>{

	@Autowired
	private DeductionService deductionService;
	
	@Override
	protected BaseService<Deduction> getService() {
		return deductionService;
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<Deduction> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<List<Deduction>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
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
