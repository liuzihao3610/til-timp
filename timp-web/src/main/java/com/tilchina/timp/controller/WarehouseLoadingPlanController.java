package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.WarehouseLoadingPlan;
import com.tilchina.timp.service.WarehouseLoadingPlanService;

/**
* 仓库装车计划表
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/warehouseloadingplan"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class WarehouseLoadingPlanController extends BaseControllerImpl<WarehouseLoadingPlan>{

	@Autowired
	private WarehouseLoadingPlanService warehouseloadingplanservice;
	
	@Override
	protected BaseService<WarehouseLoadingPlan> getService() {
		return warehouseloadingplanservice;
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	warehouseloadingplanservice.logicDeleteById(param.getData());
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
        	warehouseloadingplanservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
}
