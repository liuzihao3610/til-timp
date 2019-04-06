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
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransportSettlement;
import com.tilchina.timp.model.TransportSubsidy;
import com.tilchina.timp.service.TransportSettlementService;
import com.tilchina.timp.service.TransportSubsidyService;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/transportsettlement"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportSettlementController{

	@Autowired
	private TransportSettlementService transportsettlementservice;
	@Autowired
	private TransportSubsidyService transportSubsidyService;
	
	 @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	 @Auth(ClientType.WEB)
	 public ApiResult<TransportSettlement> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportSettlement t =transportsettlementservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	 
	 /**
	  * 查看运单结算信息
	  * @param param
	  * @return
	  */
	 @RequestMapping(value = "/getByTransportOrderId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	 @Auth(ClientType.WEB)
	 public ApiResult<TransportSettlement> getByTransportOrderId(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportSettlement t = transportsettlementservice.queryByTransportOrderId(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	 
	 /**
	  * 修改补贴项目后刷新接口
	  * @param param
	  * @return
	  */
	 @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	 @Auth(ClientType.WEB)
	 public ApiResult<TransportSettlement> query(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportSettlement t = transportsettlementservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	 
    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportSettlement>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransportSettlement> pageInfo =transportsettlementservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
        	 TransportSettlement t = JSON.parseObject(param.getData(), TransportSettlement.class);
           transportsettlementservice.add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            TransportSettlement t = JSON.parseObject(param.getData(), TransportSettlement.class);
           transportsettlementservice.updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
        	 TransportSettlement t = JSON.parseObject(param.getData(), TransportSettlement.class);
           transportsettlementservice.update(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
           transportsettlementservice.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	/**
	 * 添加补贴项目
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/addPrograms", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> postPrograms(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
        	TransportSubsidy transportSubsidy = JSON.parseObject(param.getData(), TransportSubsidy.class);
            transportsettlementservice.addPrograms(transportSubsidy);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	/**
	 * 删除补贴项目
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/delPrograms", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delPrograms(@RequestBody ApiParam<Long> param) {
        log.debug("post: {}", param);
        try {
        	transportsettlementservice.delPrograms(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
	 * 查询补贴项目
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/queryPrograms", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportSubsidy>> queryPrograms(@RequestBody ApiParam<Long> param) {
        log.debug("post: {}", param);
        try {
        	List<TransportSubsidy> subsidys =  transportSubsidyService.queryByTransportOrderId(param.getData());
            return ApiResult.success(subsidys);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
