package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import com.tilchina.timp.model.DriverLicense;
import com.tilchina.timp.model.DriverPhoto;
import com.tilchina.timp.service.DriverLicenseService;

/**
* 驾驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/driverlicense"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DriverLicenseController{

	@Autowired
	private DriverLicenseService driverlicenseservice;
	
	
	protected BaseService<DriverLicense> getService() {
		return driverlicenseservice;
	}
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            driverlicenseservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/removeList", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	driverlicenseservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<DriverLicense>> param) {
        log.debug("put: {}", param);
        try {
            
        	driverlicenseservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/audit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverlicenseservice.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/unaudit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverlicenseservice.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<DriverLicense> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	DriverLicense t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<DriverLicense>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<DriverLicense> pageInfo = driverlicenseservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> add(@RequestBody ApiParam<DriverLicense> param) {
        log.debug("getList: {}", param);
        try {
            driverlicenseservice.add(param.getData());
            log.debug("get result: {}", param);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/addPhoto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addPhoto(@RequestParam("file") MultipartFile file,
    								  @RequestParam("licenseName") String  licenseName,
    								  @RequestParam("licenseType") int licenseType,
    								  @RequestParam("driverId") Long driverId
    								) {
        log.debug("getList: {}");
        try {
            driverlicenseservice.addPhoto(file,licenseName,licenseType,driverId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addInfoAndPhoto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addInfoAndPhoto(@RequestParam("driverLicense") String json,
			  					  @RequestParam("file") MultipartFile file) {
        log.debug("post: {}", json);
        try {
            DriverLicense driverLicense=JSON.parseObject(json, DriverLicense.class);
            driverlicenseservice.addInfoAndPhoto(driverLicense,file);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", json, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<DriverLicense> param) {
        log.debug("put: {}", param);
        try {
            getService().updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
