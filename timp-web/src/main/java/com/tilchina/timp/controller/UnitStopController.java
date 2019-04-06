package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.UnitStop;
import com.tilchina.timp.service.UnitStopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 接车点管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/unitstop"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class UnitStopController{

	@Autowired
	private UnitStopService unitstopservice;

	protected BaseService<UnitStop> getService() {
		return unitstopservice;
	}

	/**
	 * 通过停车点ID收发货单位停车点
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/get"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<UnitStop> getById(@RequestBody ApiParam<Long> param) {
		log.debug("get: {}", param);

		try {
			UnitStop t = this.getService().queryById((Long)param.getData());
			log.debug("get result: {}", t);
			return ApiResult.success(t);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 获取所有收发货单位停车点
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/getList"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<UnitStop>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		log.debug("getList: {}", param);

		try {
			PageInfo<UnitStop> pageInfo = unitstopservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("get result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 新增收发货单位停车点
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/add"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<UnitStop> param) {
		log.debug("post: {}", param);

		try {
			unitstopservice.add(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("添加停车点失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	/**
	 * 修改收发货单位停车点
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/putPart"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<UnitStop> param) {
		log.debug("put: {}", param);

		try {
			this.getService().updateSelective(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("修改失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 通过联系人ID删除收发货单位停车点
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/remove"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
		log.debug("remove: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("请求参数不能为空！");
			} else {
				unitstopservice.deleteById(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("删除失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 封存
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/sequestration"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> sequestration(@RequestBody ApiParam<Long> param) {
		log.debug("sequestration: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("参数不能为空！");
			} else {
				unitstopservice.sequestration(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("封存失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 取消封存
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/unsequestration"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> unsequestration(@RequestBody ApiParam<Long> param) {
		log.debug("unsequestration: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("参数不能为空！");
			} else {
				unitstopservice.unsequestration(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception e) {
			log.error("取消封存失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

}
