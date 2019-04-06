package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.model.AssemblyResultDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.AssemblyResult;
import com.tilchina.timp.service.AssemblyResultService;

import java.util.List;
import java.util.Map;

/**
* 配板结果主表
*
* @version 1.0.0
* @author WangShengguang
*/
@RestController
@RequestMapping(value = {"/s1/assemblyresult"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AssemblyResultController {

	@Autowired
	private AssemblyResultService assemblyResultService;
	
	@RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public ApiResult<List<AssemblyResult>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		log.debug("getList: {}", param);
		try {
			PageInfo<AssemblyResult> pageInfo = assemblyResultService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("getList result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception e) {
			log.error("查询失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

    @RequestMapping(value = "/getDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<AssemblyResultDetail>> getDetails(@RequestBody ApiParam<Long> param) {
        log.debug("getDetails: {}", param);
        try {
            List<AssemblyResultDetail> details = assemblyResultService.queryDetails(param.getData());
            log.debug("getDetails result: {}", details);
            return ApiResult.success(details);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
