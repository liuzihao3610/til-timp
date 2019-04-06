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
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.service.BrandService;
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
* 品牌档案
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/brand"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class BrandController extends BaseControllerImpl<Brand> {

	@Autowired
	private BrandService brandservice;
	
	@Override
	protected BaseService<Brand> getService() {
		return brandservice;
	}
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<Brand> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            Brand t = brandservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
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
            Brand t = (Brand)JSON.parseObject(param.getData(), clazz);
            brandservice.add(t);
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
            Brand t = (Brand)JSON.parseObject(param.getData(), clazz);
            brandservice.updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            brandservice.deleteById(param.getData());
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

        	brandservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<Brand>> param) {
        log.debug("put: {}", param);
        try {
            
        	brandservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	//参照列表
	@RequestMapping(value = "/getReferenceList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Brand>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	PageInfo<Brand> pageInfo =brandservice.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
//	//快速查询
//	@RequestMapping(value = "/quick", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
//	@Auth(ClientType.WEB)
//    public ApiResult<List<Brand>> quick(@RequestBody ApiParam<Map<String, Object>> param) {
//        log.debug("put: {}",param);
//        try {
//            
//        	List<Brand> brands =brandservice.queryList(param.getData());
//            return ApiResult.success(brands);
//        } catch (Exception e) {
//            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
//            return ApiResult.failure("9999", e.getMessage());
//        }
//    }
	



    @RequestMapping(value = "/getList", method = RequestMethod.POST,  produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<List<Brand>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {

            PageInfo<Brand> pageInfo = brandservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("批量查询失败，param={}", param, e);
            return ApiResult.failure(LanguageUtil.getMessage("9999"), e.getMessage());
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Brand>> getList() {
        try {

            PageInfo<Brand> pageInfo = brandservice.queryList(null,0,0);
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("批量查询失败，param={}", e);
            return ApiResult.failure(LanguageUtil.getMessage("9999"), e.getMessage());
        }
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        log.debug("importExcel: {}", file);
        try {
            brandservice.importExcel(file);
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
            Workbook workbook = brandservice.exportExcel();
            ExcelUtil.returnToBrowser(response, workbook, "品牌档案");
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
}

