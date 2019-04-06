package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.JobNotice;
import com.tilchina.timp.model.JobNoticeB;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.JobNoticeBService;
import com.tilchina.timp.service.JobNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 定时任务到期提醒
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/jobnotice"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class JobNoticeController{

	@Autowired
	private JobNoticeService jobnoticeservice;

	@Autowired
	private JobNoticeBService jobNoticeBService;

	/**
	 * 通过ID查询定时任务
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/get"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<JobNotice> getById(@RequestBody ApiParam<Long> param) {
		log.debug("get: {}", param);

		try {
			JobNotice t = jobnoticeservice.queryById((Long)param.getData());
			log.debug("get result: {}", t);
			return ApiResult.success(t);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 查询所有定时任务
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/getList"},
			method = {RequestMethod.POST, RequestMethod.GET},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<JobNotice>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		log.debug("getList: {}", param);

		try {
			PageInfo<JobNotice> pageInfo = jobnoticeservice.queryList((Map)param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("get result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 添加定时任务
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/add"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {
		log.debug("post: {}", param);

		try {
			JobNotice t = (JobNotice)JSON.parseObject((String)param.getData(), JobNotice.class);
			jobnoticeservice.add(t);
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("添加失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 修改定时任务
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/putPart"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
		log.debug("put: {}", param);

		try {
			JobNotice t = (JobNotice)JSON.parseObject((String)param.getData(), JobNotice.class);
			jobnoticeservice.updateSelective(t);
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception var4) {
			log.error("修改失败，param={}", param, var4);
			return ApiResult.failure("9999", var4.getMessage());
		}
	}

	/**
	 * 通过定时任务ID删除定时任务
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
				jobnoticeservice.deleteById((Long)param.getData());
				return ApiResult.success(LanguageUtil.getMessage("0000"));
			}
		} catch (Exception var3) {
			log.error("删除失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 通过定时任务ID查询任务子表
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/queryByJobNoticeId"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<JobNoticeB>> queryByJobNoticeId(@RequestBody ApiParam<Long> param) {
		log.debug("queryByJobNoticeId: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("请求参数不能为空！");
			} else {
				List<JobNoticeB> jobNoticeBS=jobNoticeBService.queryByJobNoticeId((Long)param.getData());
				return ApiResult.success(jobNoticeBS);
			}
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	/**
	 * 通过任务编码获取发送列表
	 * @param param
	 * @return
	 */
	@RequestMapping(
			value = {"/queryByJobNoticeCode"},
			method = {RequestMethod.POST},
			produces = {"application/json;charset=utf-8"}
	)
	@Auth(ClientType.WEB)
	public ApiResult<List<User>> queryByJobNoticeCode(@RequestBody ApiParam<String> param) {
		log.debug("queryByJobNoticeCode: {}", param);

		try {
			if (param.getData() == null) {
				throw new BaseException("请求参数不能为空！");
			} else {
				List<User> userList=jobNoticeBService.queryByJobNoticeCode(param.getData());
				return ApiResult.success(userList);
			}
		} catch (Exception var3) {
			log.error("查询发送列表失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

}
