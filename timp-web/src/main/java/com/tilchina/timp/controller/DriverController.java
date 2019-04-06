package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.DriverService;
import com.tilchina.timp.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* 驾驶员档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/driver"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DriverController extends BaseControllerImpl<User>{

	@Autowired
	private DriverService driverService;
	
	@Override
	protected BaseService<User> getService() {
		return driverService;
	}
	
    
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            driverService.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    @RequestMapping(value = "/removeList",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	driverService.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<User>> param) {
        log.debug("put: {}", param);
        try {
            
        	driverService.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
   
	@RequestMapping(value = "/audit", produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverService.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/unaudit", produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
        	driverService.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<User> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	User t = driverService.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST , produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<List<User>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<User> pageInfo = driverService.getDriverList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addDriver", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<JSONObject> postDriver(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            User t = (User)JSON.parseObject(param.getData(), clazz);
            JSONObject json=driverService.addDriver(t);
            return ApiResult.success(json);
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
            User t = (User)JSON.parseObject(param.getData(), clazz);
            driverService.updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<User>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getReferenceList: {}", param);
        try {
            PageInfo<User> pageInfo=driverService.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /***
     * 用户解锁
     * @param param
     * @return
     */
    @RequestMapping(value = "/deblocking", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deblocking(@RequestBody ApiParam<User> param) {
        log.debug("putCheck: {}", param);
        try {
        	driverService.updateBlock(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 重置密码
     * @param param
     * @return
     */
    @RequestMapping(value = "/resetPasswords", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<JSONObject> resetPasswords(@RequestBody ApiParam<User> param) {
        log.debug("putCheck: {}", param);
        try {
        	JSONObject json = driverService.updatePasswords(param.getData());
            return ApiResult.success(json);
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/addHeadPhoto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addHeadPhoto(@RequestParam("file") MultipartFile file,
    								  	  @RequestParam("driverId") Long driverId
    								     ) {
        log.debug("addHeadPhoto: {}");
        try {
        	driverService.addHeadPhoto(file,driverId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        log.debug("importExcel: {}", file);
        try {
            driverService.importExcel(file);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入Excel 失败，param={}", file, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @GetMapping("exportExcel")
    @Auth(ClientType.WEB)
    public ApiResult<String> exportExcel(HttpServletResponse response) {
        try {
            Workbook workbook = driverService.exportExcel();
            ExcelUtil.returnToBrowser(response, workbook, "驾驶员档案");
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
