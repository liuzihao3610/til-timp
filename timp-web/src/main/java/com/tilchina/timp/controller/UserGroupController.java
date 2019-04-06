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
import com.tilchina.timp.model.UserGroup;
import com.tilchina.timp.service.UserGroupService;

import java.util.List;

/**
* 用户关系表
*
* @version 1.0.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/usergroup"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class UserGroupController extends BaseControllerImpl<UserGroup>{

	@Autowired
	private UserGroupService usergroupservice;
	
	@Override
	protected BaseService<UserGroup> getService() {
		return usergroupservice;
	}
	

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	usergroupservice.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteList(@RequestBody ApiParam<Long[]> param) {
        log.debug("remove: {}", param);
        try {
            for (Long del : param.getData()) {
                usergroupservice.deleteById(del);
            }
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getByUserId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<UserGroup> getByDriverId(@RequestBody ApiParam<Long> param) {
        log.debug("getByUserId: {}", param);
        try {
        	UserGroup userGroup = usergroupservice.queryByUserId(param.getData());
            return ApiResult.success(userGroup);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
