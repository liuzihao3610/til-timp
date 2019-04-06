package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.AccountingClosure;
import com.tilchina.timp.service.AccountingClosureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 关账
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/accountingclosure"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AccountingClosureController extends BaseControllerImpl<AccountingClosure>{

	@Autowired
	private AccountingClosureService accountingclosureservice;
	
	@Override
	protected BaseService<AccountingClosure> getService() {
		return accountingclosureservice;
	}

	@Override
	public ApiResult<List<AccountingClosure>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@PostMapping(value = "/close")
	@Auth(ClientType.WEB)
	public ApiResult<String> close(@RequestBody ApiParam<Map<String, String>> param) {

		try {
			Map<String, String> map = param.getData();
			Date accountPeriod = DateUtils.parseDate(map.get("accountPeriod"), "yyyy-MM-dd");
			accountingclosureservice.generateRecord(accountPeriod);
			return ApiResult.success();
		} catch (Exception e) {
			return ApiResult.failure("9999", e.getMessage());
		}
	}

}
