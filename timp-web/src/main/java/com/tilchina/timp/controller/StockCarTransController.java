package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.StockCarTrans;
import com.tilchina.timp.service.StockCarTransService;

/**
* 商品车运输记录表
*
* @version 1.0.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/stockcartrans"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class StockCarTransController{

	@Autowired
	private StockCarTransService stockcartransservice;
	
	 @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	 @Auth(ClientType.WEB)
	    public ApiResult<StockCarTrans> getById(@RequestBody ApiParam<Long> param) {
	        log.debug("get: {}", param);
	        try {
	        	StockCarTrans t =stockcartransservice.queryById(param.getData());
	            log.debug("get result: {}", t);
	            return ApiResult.success(t);
	        } catch (Exception e) {
	            log.error("查询失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Auth(ClientType.WEB)
	    public ApiResult<List<StockCarTrans>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
	        log.debug("getList: {}", param);
	        try {
	            PageInfo<StockCarTrans> pageInfo =stockcartransservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
	            log.debug("get result: {}", pageInfo);
	            return ApiResult.success(pageInfo);
	        } catch (Exception e) {
	            log.error("查询失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Auth(ClientType.WEB)
	    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
	        log.debug("post: {}", param);
	        try {
	        	StockCarTrans t = JSON.parseObject(param.getData(), StockCarTrans.class);
	           stockcartransservice.add(t);
	            return ApiResult.success();
	        } catch (Exception e) {
	            log.error("添加失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Auth(ClientType.WEB)
	    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
	        log.debug("put: {}", param);
	        try {
	        	StockCarTrans t = JSON.parseObject(param.getData(), StockCarTrans.class);
	           stockcartransservice.updateSelective(t);
	            return ApiResult.success();
	        } catch (Exception e) {
	            log.error("修改失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Auth(ClientType.WEB)
	    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
	        log.debug("put: {}", param);
	        try {
	        	StockCarTrans t = JSON.parseObject(param.getData(), StockCarTrans.class);
	           stockcartransservice.update(t);
	            return ApiResult.success();
	        } catch (Exception e) {
	            log.error("修改失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Auth(ClientType.WEB)
	    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
	        log.debug("remove: {}", param);
	        try {
	           stockcartransservice.deleteById(param.getData());
	            return ApiResult.success();
	        } catch (Exception e) {
	            log.error("删除失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	    
}
