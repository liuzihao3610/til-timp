package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Dept;
import com.tilchina.timp.model.DriverStatus;
import com.tilchina.timp.service.DriverStatusService;

/**
* 司机状态
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/driverstatus"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DriverStatusController extends BaseControllerImpl<DriverStatus>{

	@Autowired
	private DriverStatusService driverstatusservice;
	
	@Override
	protected BaseService<DriverStatus> getService() {
		return driverstatusservice;
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	/*driverstatusservice.logicDeleteById(param.getData());*/
        	driverstatusservice.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        /*	driverstatusservice.logicDeleteByIdList(param.getData());*/
        	for (int del : param.getData()) {
        		driverstatusservice.deleteById(Long.valueOf(del));
			}
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getByDirverId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<DriverStatus> getByDriverId(@RequestBody ApiParam<Long> param) {
        log.debug("getByDirverId: {}", param);
        try {
        	DriverStatus driverStatus = driverstatusservice.queryByDirverId(param.getData());
            return ApiResult.success(driverStatus);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/putDiStatus", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> putDiStatus(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            DriverStatus t = (DriverStatus)JSON.parseObject(param.getData(), clazz);
            driverstatusservice.updateDriverStatus(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    
    /**
     * 参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<DriverStatus>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<DriverStatus> driverStatuss = driverstatusservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(driverStatuss);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
