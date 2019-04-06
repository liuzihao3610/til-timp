package com.tilchina.timp.controller;

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
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.OilPrice;
import com.tilchina.timp.service.OilPriceService;
import com.tilchina.timp.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* 油价档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/oilprice"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class OilPriceController extends BaseControllerImpl<OilPrice>{

	@Autowired
	private OilPriceService oilpriceservice;
	
	@Override
	protected BaseService<OilPrice> getService() {
		return oilpriceservice;
	}
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            oilpriceservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/removeList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	oilpriceservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<OilPrice>> param) {
        log.debug("put: {}", param);
        try {
            
        	oilpriceservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/getReferenceList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<OilPrice>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	PageInfo<OilPrice> pageInfo =oilpriceservice.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<OilPrice> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	OilPrice t = getService().queryById(param.getData());
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
    public ApiResult<List<OilPrice>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<OilPrice> pageInfo = oilpriceservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
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
            
            try {
            	Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            	OilPrice t = (OilPrice)JSON.parseObject(param.getData(), clazz);
            	getService().add(t);
			} catch (Exception e) {
				throw new BusinessException("信息输入有误,请核实!");
			}
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
            OilPrice t = (OilPrice)JSON.parseObject(param.getData(), clazz);
            getService().updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @PostMapping("importExcel")
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            oilpriceservice.importExcel(file);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @PostMapping("exportExcel")
    @Auth(ClientType.WEB)
    public ApiResult<String> exportExcel(HttpServletResponse response) {
        try {
            Workbook workbook = oilpriceservice.exportExcel();
            ExcelUtil.returnToBrowser(response, workbook, "油价档案");
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
}
