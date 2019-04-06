package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Contacts;
import com.tilchina.timp.service.ContactsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 收发货单位联系人
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/contacts"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class ContactController{

	@Autowired
	private ContactsService contactsservice;

	protected BaseService<Contacts> getService() {

		return contactsservice;
	}

	/**
	 * 通过联系人ID获取联系人
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/get"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<Contacts> getById(@RequestBody ApiParam<Long> param) {
		log.debug("get: {}", param);

		try {
			Contacts t = this.getService().queryById((Long)param.getData());
			log.debug("get result: {}", t);
			return ApiResult.success(t);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 获取所有收发货单位联系人
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/getList"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<Contacts>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		log.debug("getList: {}", param);

		try {
			PageInfo<Contacts> pageInfo = this.getService().queryList((Map)param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("get result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 新增收发货单位联系人
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/add"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<Contacts> param) {
		log.debug("post: {}", param);

		try {
			this.getService().add(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("添加失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 批量新增收发货单位联系人
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/adds"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> adds(@RequestBody ApiParam<List<Contacts>> param) {
		log.debug("adds: {}", param);

		try {
			contactsservice.add(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("批量添加失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 修改收发货单位联系人
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/putPart"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<Contacts> param) {
		log.debug("put: {}", param);

		try {
			contactsservice.updateSelective(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("修改联系人失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 通过联系人ID删除联系人
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
				this.getService().deleteById((Long)param.getData());
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
				throw new BaseException("请求参数不能为空！");
			} else {
				contactsservice.sequestration(param.getData());
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
				throw new BaseException("请求参数不能为空！");
			} else {
				contactsservice.unsequestration(param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("取消封存失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	@RequestMapping(value = "/removeList",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
		log.debug("removeList: {}", param);
		try {

			contactsservice.deleteList(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@RequestMapping(value = "/putPartList",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartList(@RequestBody ApiParam<List<Contacts>> param) {
		log.debug("putPartList: {}", param);
		try {

			contactsservice.update(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}




	@RequestMapping(value = "/queryByUnitId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<List<Contacts>> queryByUnitId(@RequestBody ApiParam<Long> param) {
		log.debug("queryByUnitId: {}", param);
		try {
			List<Contacts>conList= contactsservice.queryByUnitId(param.getData());
			return ApiResult.success(conList);
		} catch (Exception e) {
			log.error("查询失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

}
