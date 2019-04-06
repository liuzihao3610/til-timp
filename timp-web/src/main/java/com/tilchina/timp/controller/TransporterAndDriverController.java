package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransporterAndDriver;
import com.tilchina.timp.service.TransporterAndDriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* 轿运车司机关系表
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/transporteranddriver"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransporterAndDriverController extends BaseControllerImpl<TransporterAndDriver>{

	@Autowired
	private TransporterAndDriverService transporteranddriverservice;
	
	@Override
	protected BaseService<TransporterAndDriver> getService() {
		return transporteranddriverservice;
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
    	log.debug("remove: {}", param);
        try {
        	/*transporteranddriverservice.logicDeleteById(param.getData());*/
        	transporteranddriverservice.deleteById(param.getData());
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
        	TransporterAndDriver t = JSON.parseObject(param.getData(), TransporterAndDriver.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	/*transporteranddriverservice.logicDeleteByIdList(param.getData());*/
        	for (int del : param.getData()) {
        		transporteranddriverservice.deleteById(Long.valueOf(del));
			}
             return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
   
    @RequestMapping(value = "/getByKeyId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<TransporterAndDriver> getByDriverId(@RequestBody ApiParam<Map<String, Long>> param) {
        log.debug("getByKeyId: {}", param);
        try {
        	TransporterAndDriver transporterAndDriver = transporteranddriverservice.queryByKeyId(param.getData());
            return ApiResult.success(transporterAndDriver);
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
    public ApiResult<List<TransporterAndDriver>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<TransporterAndDriver> depts = transporteranddriverservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(depts);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<TransporterAndDriver> getById(@RequestBody ApiParam<Long> param) {
    	return super.getById(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<List<TransporterAndDriver>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
		return super.putPart(param);
	}

	@Auth(ClientType.WEB)
	@Override
	public ApiResult<String> put(@RequestBody ApiParam<String> param) {
		return super.put(param);
	}
}
