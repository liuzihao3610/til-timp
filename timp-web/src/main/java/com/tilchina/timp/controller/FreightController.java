package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Freight;
import com.tilchina.timp.service.FreightService;

import javax.servlet.annotation.MultipartConfig;

/**
* 运价管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@MultipartConfig
@RequestMapping(value = {"/s1/freight"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class FreightController{

	@Autowired
	private FreightService freightservice;
	
	protected BaseService<Freight> getService() {
		return freightservice;
	}
	
	@RequestMapping(value = "/get", method = { RequestMethod.POST,RequestMethod.GET }, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<Freight> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            Freight t = freightservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = { RequestMethod.POST,RequestMethod.GET }, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Freight>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Freight> pageInfo = freightservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<Freight> param) {
        log.debug("post: {}", param);
        try {
            freightservice.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<Freight> param) {
        log.debug("put: {}", param);
        try {
        	freightservice.updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> put(@RequestBody ApiParam<Freight> param) {
        log.debug("put: {}", param);
        try {
        	freightservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
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
        	freightservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int []> param) {
        log.debug("removeList: {}", param);
        try {
        	freightservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("批量删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/audit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("audit: {}", param);
        try {
        	freightservice.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("批量删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/unaudit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("unaudit: {}", param);
        try {
        	freightservice.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("批量删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Freight>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getReferenceList: {}", param);
        try {
        	List<Freight> freights=freightservice.getReferenceList(param.getData());
            return ApiResult.success(freights);
        } catch (Exception e) {
            log.error("获取参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	/*
	 * 导入Excel
	 */
    @RequestMapping(value = "/importFile", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> importFile(@RequestParam("file") MultipartFile file
    									) {
    	 log.info("File: {}", file.getOriginalFilename());
         File f = null;
         try {
             freightservice.importFile(file);
             return ApiResult.success(LanguageUtil.getMessage("0000"));
         } catch (Exception e) {
             log.error("导入失败，param={}", e);
             return ApiResult.failure("9999", e.getMessage());
         }
    }
    
    @RequestMapping(value = "/getFreight", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Freight> getFreight(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getReferenceList: {}", param);
        try {
        	Freight freight=freightservice.getFreight(param.getData());
            return ApiResult.success(freight);
        } catch (Exception e) {
            log.error("获取参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
