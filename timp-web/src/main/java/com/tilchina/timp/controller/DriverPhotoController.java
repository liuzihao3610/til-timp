package com.tilchina.timp.controller;

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
import com.tilchina.timp.model.DriverPhoto;
import com.tilchina.timp.model.WayReport;
import com.tilchina.timp.service.DriverPhotoService;

import lombok.extern.slf4j.Slf4j;

/**
* 驾驶员照片档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/driverphoto"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DriverPhotoController {

	@Autowired
	private DriverPhotoService driverphotoservice;
	
	
	protected BaseService<DriverPhoto> getService() {
		return driverphotoservice;
	}
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            driverphotoservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	driverphotoservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<DriverPhoto>> param) {
        log.debug("put: {}", param);
        try {
            
        	driverphotoservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/audit", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverphotoservice.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/unaudit", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverphotoservice.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<DriverPhoto> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	DriverPhoto t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<DriverPhoto>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<DriverPhoto> pageInfo = driverphotoservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> add(@RequestBody ApiParam<DriverPhoto> param) {
        log.debug("getList: {}", param);
        try {
            driverphotoservice.add(param.getData());
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
    								  @RequestParam("photoDes") String  photoDes,
    								  @RequestParam("photoType") int photoType,
    								  @RequestParam("driverId") Long driverId
    								) {
        log.debug("getList: {}");
        try {
            driverphotoservice.addPhoto(file,photoDes,photoType,driverId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    

    @RequestMapping(value = "/addInfoAndPhoto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addInfoAndPhoto(@RequestParam("driverPhoto") String json,
								  @RequestParam("file") MultipartFile file) {
        log.debug("post: {}", json);
        try {
            
            DriverPhoto driverPhoto = JSON.parseObject(json,DriverPhoto.class);
            driverphotoservice.addInfoAndPhoto(driverPhoto,file);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", json, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<DriverPhoto> param) {
        log.debug("put: {}", param);
        try {
            driverphotoservice.updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
}


