package com.tilchina.timp.app.controller;

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
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportSettlement;
import com.tilchina.timp.model.TransportSubsidy;
import com.tilchina.timp.service.TransportSettlementService;
import com.tilchina.timp.service.TransportSubsidyService;
import com.tilchina.timp.vo.AppSettlementDetailVO;
import com.tilchina.timp.vo.AppSettlementVO;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/app/transportsettlement"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppTransportSettlementController{

	@Autowired
	private TransportSettlementService transportsettlementservice;
	@Autowired
	private TransportSubsidyService transportSubsidyService;
	
	 /**
	  * 查看运单结算信息
	  * @param param
	  * @return
	  */
	 @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	 @Auth(ClientType.APP)
	 public ApiResult<List<AppSettlementVO>> getByTransportOrderId(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("get: {}", param);
        try {
        	PageInfo<AppSettlementVO> settlements = transportsettlementservice.queryAppSettlement(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", settlements);
            return ApiResult.success(settlements);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	 
    /**
	 * 查询运单结算明细
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/queryDetails", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
    public ApiResult<AppSettlementDetailVO> queryPrograms(@RequestBody AppApiParam<Map<String, Object>> param) {
        log.debug("post: {}", param);
        try {
        	if(param.getData().get("transportOrderId") == null) {
        		throw new BusinessException("请输入有效的运单id!");
        	}
        	AppSettlementDetailVO subsidys =  transportsettlementservice.queryPrograms(Long.valueOf(param.getData().get("transportOrderId").toString()));
            return ApiResult.success(subsidys);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
