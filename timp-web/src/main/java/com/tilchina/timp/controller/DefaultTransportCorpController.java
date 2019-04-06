package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.DefaultTransportCorp;
import com.tilchina.timp.model.DefaultTransportCorpDetail;
import com.tilchina.timp.service.DefaultTransportCorpDetailService;
import com.tilchina.timp.service.DefaultTransportCorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 默认运输公司表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/defaulttransportcorp"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DefaultTransportCorpController extends BaseControllerImpl<DefaultTransportCorp>{

	@Autowired
	private DefaultTransportCorpService defaulttransportcorpservice;

	@Autowired
	private DefaultTransportCorpDetailService detailService;
	
	@Override
	protected BaseService<DefaultTransportCorp> getService() {
		return defaulttransportcorpservice;
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<DefaultTransportCorp> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<List<DefaultTransportCorp>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {

		try {
			DefaultTransportCorp defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorp.class);
			defaulttransportcorpservice.add(defaultTransportCorp);
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
			DefaultTransportCorp defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorp.class);
			defaulttransportcorpservice.updateSelective(defaultTransportCorp);
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

	@PostMapping(value = "addDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> addDetail(@RequestBody ApiParam<String> param) {

		try {
			DefaultTransportCorpDetail defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorpDetail.class);
			detailService.add(defaultTransportCorp);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "putPartDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartDetail(@RequestBody ApiParam<String> param) {

		try {
			DefaultTransportCorpDetail defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorpDetail.class);
			detailService.updateSelective(defaultTransportCorp);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "removeDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> removeDetail(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			detailService.deleteById(map.get("defaultCorpDetailId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "getDetail")
	@Auth(ClientType.WEB)
	public ApiResult<List<DefaultTransportCorpDetail>> getDetail(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			List<DefaultTransportCorpDetail> details = detailService.selectByDefaultCorpId(map.get("defaultCorpId"));
			return ApiResult.success(details);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "addRange")
	@Auth(ClientType.WEB)
	public ApiResult<String> addRangeDetail(@RequestBody ApiParam<String> param) {

		try {
			DefaultTransportCorpDetail defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorpDetail.class);
			detailService.add(defaultTransportCorp);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "putPartRange")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartRange(@RequestBody ApiParam<String> param) {

		try {
			DefaultTransportCorpDetail defaultTransportCorp = JSON.parseObject(param.getData(), DefaultTransportCorpDetail.class);
			detailService.add(defaultTransportCorp);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "getReferenceList")
	@Auth(ClientType.WEB)
	public ApiResult<List<List<Map<Long, String>>>> getReferenceList(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Long defaultCorpId = param.getData().get("defaultCorpId");
			int pageNum = param.getPageNum();
			int pageSize = param.getPageSize();

			PageInfo<List<Map<Long, String>>> referenceList = defaulttransportcorpservice.getReferenceList(defaultCorpId, pageNum, pageSize);
			return ApiResult.success(referenceList);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
