package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.Tractor;
import com.tilchina.timp.service.TractorService;

/**
* 轿运车车头型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/tractor"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TractorController extends BaseControllerImpl<Tractor>{

	@Autowired
	private TractorService tractorservice;
	
	@Override
	protected BaseService<Tractor> getService() {
		return tractorservice;
	}
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            tractorservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/removeList", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	tractorservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<Tractor>> param) {
        log.debug("put: {}", param);
        try {
            
        	tractorservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/audit", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	tractorservice.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/unaudit", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	tractorservice.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/getReferenceList", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Tractor>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	PageInfo<Tractor> pageInfo =tractorservice.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<Tractor> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	Tractor t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<List<Tractor>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Tractor> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            Tractor t = (Tractor)JSON.parseObject(param.getData(), clazz);
            getService().add(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            Tractor t = (Tractor)JSON.parseObject(param.getData(), clazz);
            getService().updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
