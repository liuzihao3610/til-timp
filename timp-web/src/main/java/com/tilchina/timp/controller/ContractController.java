package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Contract;
import com.tilchina.timp.service.ContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 合同管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/contract"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class ContractController extends BaseControllerImpl<Contract>{

	@Autowired
	private ContractService contractService;
	
	@Override
	protected BaseService<Contract> getService() {
		return contractService;
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<Contract> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<List<Contract>> getList(@RequestBody ApiParam<Map<String, Object>> param) {

		try {
			PageInfo<Contract> pageInfo = contractService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
			return ApiResult.success(pageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {

		try {
			Contract contract = JSON.parseObject(param.getData(), Contract.class);
			contractService.add(contract);
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
			Contract contract = JSON.parseObject(param.getData(), Contract.class);
			contractService.updateSelective(contract);
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

	@PostMapping(value = "/checked")
	@Auth(ClientType.WEB)
	public ApiResult<String> checked(@RequestBody ApiParam<String> param) {

		try {
			Contract contract = JSON.parseObject(param.getData(), Contract.class);
			contractService.setDocumentsChecked(contract);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/unchecked")
	@Auth(ClientType.WEB)
	public ApiResult<String> setCheckState(@RequestBody ApiParam<String> param) {

		try {
			Contract contract = JSON.parseObject(param.getData(), Contract.class);
			contractService.setDocumentsUnchecked(contract);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/invalid")
	@Auth(ClientType.WEB)
	public ApiResult<String> invalid(@RequestBody ApiParam<String> param) {

		try {
			Contract contract = JSON.parseObject(param.getData(), Contract.class);
			contractService.setDocumentsInvalid(contract);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getReferenceList")
	@Auth(ClientType.WEB)
	public ApiResult<List<Map<String, String>>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {

		try {
			PageInfo<Map<String, String>> referenceList = contractService.getReferenceList(param.getData(),param.getPageNum(), param.getPageSize());
			return ApiResult.success(referenceList);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
