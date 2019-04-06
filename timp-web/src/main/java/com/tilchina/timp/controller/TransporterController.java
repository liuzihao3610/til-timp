package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.service.TransporterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
* 轿运车档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transporter"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransporterController extends BaseControllerImpl<Transporter>{

	@Autowired
	private TransporterService transporterservice;
	
	@Override
	protected BaseService<Transporter> getService() {
		return transporterservice;
	}
	
    
    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<Transporter> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	Transporter t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<List<Transporter>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Transporter> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
    	log.debug("remove: {}", param);
        try {
        	transporterservice.deleteById(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Transporter t = JSON.parseObject(param.getData(), Transporter.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	transporterservice.logicDeleteByIdList(param.getData());
             return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/putCheck", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> Check(@RequestBody ApiParam<Transporter> param) {
        log.debug("putCheck: {}", param);
        try {
        	transporterservice.updateCheck(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getByDriverId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Transporter> getByDriverId(@RequestBody ApiParam<Long> param) {
        log.debug("getByDriverId: {}", param);
        try {
        	Transporter transporter = transporterservice.queryByDriverId(param.getData());
            return ApiResult.success(transporter);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Transporter>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<Transporter> driverStatuss = transporterservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(driverStatuss);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 审核
     * @param param
     * @return
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCheck(@RequestBody ApiParam<String> param) {
        log.debug("putCheck: {}", param);
        try {
        	Transporter transporter = JSON.parseObject(param.getData(), Transporter.class);
        	transporter.setBillStatus(1);
        	transporterservice.updateCheck(transporter);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 取消审核
     * @param param
     * @return
     */
    @RequestMapping(value = "/cancelCheck", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCancelCheck(@RequestBody ApiParam<String> param) {
        log.debug("putCheck: {}", param);
        try {
        	Transporter transporter = JSON.parseObject(param.getData(), Transporter.class);
        	transporter.setBillStatus(0);
        	transporterservice.updateCheck(transporter);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @PostMapping("changeContractor")
    @Auth(ClientType.WEB)
    public ApiResult<String> changeContractor(@RequestBody ApiParam<Map<String, Object>> params) {
        try {
            transporterservice.updateContractorById(params.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
}
