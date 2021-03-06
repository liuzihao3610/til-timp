package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.SettleRoute;
import com.tilchina.timp.model.SettleRouteB;
import com.tilchina.timp.service.SettleRouteBService;
import com.tilchina.timp.service.SettleRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 结算路线主表
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/settleroute"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class SettleRouteController{

	@Autowired
	private SettleRouteService settlerouteservice;

	@Autowired
	private SettleRouteBService settleRouteBService;

	protected BaseService<SettleRoute> getService() {
		return settlerouteservice;
	}

	/**
	 * 通过结算路线主表ID获取信息
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/get"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<SettleRoute> getById(@RequestBody ApiParam<Long> param) {
		log.debug("get: {}", param);

		try {
			SettleRoute t = settlerouteservice.queryById(param.getData());
			log.debug("get result: {}", t);
			return ApiResult.success(t);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 查询所有结算路线
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/getList"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<SettleRoute>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		log.debug("getList: {}", param);

		try {
			PageInfo<SettleRoute> pageInfo = settlerouteservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("get result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 新增结算路线
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/add"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<SettleRoute> param) {
		log.debug("post: {}", param);

		try {
			settlerouteservice.add(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("添加结算路线失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 修改结算路线
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/putPart"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<SettleRoute> param) {
		log.debug("put: {}", param);

		try {
			settlerouteservice.updateSelective(param.getData());
			return ApiResult.success();
		} catch (Exception var4) {
			log.error("修改失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 删除结算路线
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
				settlerouteservice.deleteById(param.getData());
				return ApiResult.success();
			}
		} catch (Exception var3) {
			log.error("删除失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 通过结算路线主表ID查询路线子表
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/getBySettleRouteId"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<SettleRouteB>> getBySettleRouteId(@RequestBody ApiParam<Long> param) {
		log.debug("getBySettleRouteId: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("请求参数不能为空！");
			} else {
				List<SettleRouteB> settleRouteBS=settleRouteBService.getBySettleRouteId(param.getData());
				return ApiResult.success(settleRouteBS);
			}
		} catch (Exception var3) {
			log.error("查询路线子表失败，param={}", param, var3);
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
				throw new BaseException("请求参数不能为空！");
			} else {
				settlerouteservice.sequestration(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("封存路线失败，param={}", param, var3);
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
				throw new BaseException("请求参数不能为空！");
			} else {
				settlerouteservice.unsequestration(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("取消封存路线失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 导入结算路线
	 * @param file
	 * @return
	 */
	@RequestMapping(
			value = {"/importExcel"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
		log.info("importExcel: {}", file.getOriginalFilename());

		try {
			settlerouteservice.importExcel(file);
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var3) {
			log.error("导入失败，param={}", file.getOriginalFilename(), var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}



}
