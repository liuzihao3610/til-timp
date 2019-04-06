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
import com.tilchina.timp.model.Car;
import com.tilchina.timp.service.CarService;
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
* 
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/car"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CarController extends BaseControllerImpl<Car>{

	@Autowired
	private CarService carservice;
	
	@Override
	protected BaseService<Car> getService() {
		return carservice;
	}
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            carservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/removeList", method = RequestMethod.POST,  produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	
        	carservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putPartList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> putPartList(@RequestBody ApiParam<List<Car>> param) {
        log.debug("put: {}", param);
        try {
            
        	carservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/audit", method =RequestMethod.POST,produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("audit: {}", param);
        try {
        	carservice.audit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/unaudit", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("unaudit: {}", param);
        try {
        	carservice.unaudit(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/getReferenceList", method =RequestMethod.POST,produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Car>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	PageInfo<Car> pageInfo =carservice.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	//快速查询
	@RequestMapping(value = "/quick", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Car>> quick(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	List<Car> cars =carservice.queryList(param.getData());
            return ApiResult.success(cars);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	 @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	    @Override
	    @Auth(ClientType.WEB)
	    public ApiResult<Car> getById(@RequestBody ApiParam<Long> param) {
	        log.debug("get: {}", param);
	        try {
	            Car t = getService().queryById(param.getData());
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
	    public ApiResult<List<Car>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
	        log.debug("getList: {}", param);
	        try {
	            PageInfo<Car> pageInfo = carservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
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
	            Car t = (Car)JSON.parseObject(param.getData(), clazz);
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
	            Car t = (Car)JSON.parseObject(param.getData(), clazz);
	            getService().updateSelective(t);
	            return ApiResult.success(LanguageUtil.getMessage("0000"));
	        } catch (Exception e) {
	            log.error("修改失败，param={}", param, e);
	            return ApiResult.failure("9999", e.getMessage());
	        }
	    }

	/**
	 * 封存
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/sequestration", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> sequestration(@RequestBody ApiParam<Long> param) {
		log.debug("sequestration: {}", param);
		try {
			carservice.sequestration(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("封存失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	/**
	 * 取消封存
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/unsequestration", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> unsequestration(@RequestBody ApiParam<Long> param) {
		log.debug("unsequestration: {}", param);
		try {
			carservice.unsequestration(param.getData());
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("取消封存失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	/**
	 * 导入Excel
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> importExccel(@RequestParam("file") MultipartFile file) {
		log.debug("importExcel: {}", file.getOriginalFilename());
		try {
			carservice.importExcel(file);
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("导入商品车型号失败，param={}", file.getOriginalFilename(), e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	/**
	 * 导出Excel
	 * @return
	 */
	@RequestMapping(value = "/exportExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> exportExcel(HttpServletResponse response) {
		try {
			Workbook workbook = carservice.exportExcel();
			ExcelUtil.returnToBrowser(response, workbook, "商品车型号档案");
			return ApiResult.success(LanguageUtil.getMessage("0000"));
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}



	   

}
