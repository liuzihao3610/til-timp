package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.vo.DriverSettlementVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.model.DriverSettlement;
import com.tilchina.timp.service.DriverSettlementService;
import org.springframework.web.multipart.MultipartFile;

/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/driversettlement"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class DriverSettlementController {

	@Autowired
	private DriverSettlementService driversettlementservice;
	
    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<DriverSettlement> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            DriverSettlement t = driversettlementservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetails", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<DriverSettlementVO>> getDetails(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            List<DriverSettlementVO> t = driversettlementservice.queryDetailsById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<DriverSettlement>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<DriverSettlement> pageInfo = driversettlementservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 结算
     * @param param
     * @return
     */
    @RequestMapping(value = "/settlement", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> settlement(@RequestBody ApiParam<String> param) {
        log.debug("settlement: {}", param);
        try {
        	DriverSettlement driverSettlement = JSON.parseObject(param.getData(), DriverSettlement.class);
            driversettlementservice.settlement(driverSettlement);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("结算失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 批量结算
     * @return
     */
    @RequestMapping(value = "/allSettlement", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> allSettlement(/*@RequestBody ApiParam<Map<String, Object>> param*/) {
        log.debug("allSettlement: {}");
        try {
            driversettlementservice.settlementList();
            return ApiResult.success();
        } catch (Exception e) {
            log.error("批量结算失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> POST(@RequestBody ApiParam<String> param) {
        log.debug("POST: {}", param);
        try {
        	  DriverSettlement t = JSON.parseObject(param.getData(), DriverSettlement.class);
            driversettlementservice.add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
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
    public ApiResult<String> putCheck(@RequestBody ApiParam<Long> param) {
        log.debug("putCheck: {}", param);
        try {
            driversettlementservice.updateCheck(param.getData(),0);
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
    public ApiResult<String> cancelCheck(@RequestBody ApiParam<Long> param) {
        log.debug("putCheck: {}", param);
        try {
            driversettlementservice.updateCheck(param.getData(),1);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
        	  DriverSettlement t = JSON.parseObject(param.getData(), DriverSettlement.class);
            driversettlementservice.updateSelective(t);
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
            DriverSettlement t = JSON.parseObject(param.getData(), DriverSettlement.class);
            driversettlementservice.update(t);
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
            driversettlementservice.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 导入补贴信息
     * @param file
     * @param settlementMonth
     * @return
     */
    @PostMapping(value = "/importSubsidy")
    @Auth(ClientType.WEB)
    public ApiResult<String> importSubsidy(@RequestParam("file") MultipartFile file,@RequestParam("settlementMonth") String settlementMonth) {
        try {
            driversettlementservice.importSubsidy(file,settlementMonth);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


    @RequestMapping(value = "/getSettlementMonth", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<String>> getSettlementMonth() {
        log.debug("getSettlementMonth: ");
        try {
            List<String> settlementMonths = driversettlementservice.querySettlementMonth();
            log.debug("get result: {}", settlementMonths);
            return ApiResult.success(settlementMonths);
        } catch (Exception e) {
            log.error("查询失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
}
