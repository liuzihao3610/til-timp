package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CityArea;
import com.tilchina.timp.model.CityAreaDetail;
import com.tilchina.timp.service.CityAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 虚拟城市档案
*
* @version 1.0.0
* @author WangShengguang
*/
@RestController
@RequestMapping(value = {"/s1/vrcity"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CityAreaController {

	@Autowired
	private CityAreaService cityAreaService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> add(@RequestBody ApiParam<CityArea> param) {
        log.debug("remove: {}", param);
        try {
            cityAreaService.add(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败！，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/update",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> update(@RequestBody ApiParam<CityArea> param) {
        log.debug("remove: {}", param);
        try {
            cityAreaService.updateSelective(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败！，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/remove",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            cityAreaService.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@RequestMapping(value = "/query",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<CityArea>> queryList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("query: {}",param);
        try {
            
        	PageInfo<CityArea> pageInfo =cityAreaService.queryList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/queryDetail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<CityAreaDetail>> queryDetail(@RequestBody ApiParam<Long> param) {
        log.debug("queryDetail: {}",param);
        try {
            
        	List<CityAreaDetail> details =cityAreaService.queryDetails(param.getData());
            return ApiResult.success(details);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/ref",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<CityArea>> queryRef(@RequestBody ApiParam<Map<String,Object>> param) {
        log.debug("queryRef: {}",param);
        try {
            PageInfo<CityArea> pageInfo =cityAreaService.queryList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
