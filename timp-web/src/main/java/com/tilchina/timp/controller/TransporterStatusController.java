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
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverStatus;
import com.tilchina.timp.model.TransporterStatus;
import com.tilchina.timp.service.TransporterStatusService;

/**
* 轿运车状态
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transporterstatus"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransporterStatusController extends BaseControllerImpl<TransporterStatus>{

	@Autowired
	private TransporterStatusService transporterstatusservice;
	
	@Override
	protected BaseService<TransporterStatus> getService() {
		return transporterstatusservice;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            TransporterStatus t = (TransporterStatus)JSON.parseObject(param.getData(), TransporterStatus.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	/*transporterstatusservice.logicDeleteById(param.getData());*/
        	transporterstatusservice.deleteById(param.getData());
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
        	/*transporterstatusservice.logicDeleteByIdList(param.getData());*/
        	for (int del : param.getData()) {
        		transporterstatusservice.deleteById(Long.valueOf(del));
			}
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/putTrStatus", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> putTrStatus(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            TransporterStatus t = (TransporterStatus)JSON.parseObject(param.getData(), clazz);
            transporterstatusservice.updateById(t);
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
    public ApiResult<List<TransporterStatus>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	List<TransporterStatus> transporterStatuss = transporterstatusservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(transporterStatuss);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	
}
