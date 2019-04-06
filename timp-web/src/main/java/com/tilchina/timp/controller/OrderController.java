package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.OrderManager;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.service.OrderDetailService;
import com.tilchina.timp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单主表
 *
 * @author LiuShuqi
 * @version 1.1.0
 */
@RestController
@RequestMapping(value = {"/s1/order"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderservice;

    @Autowired
    private OrderManager orderManager;

    @RequestMapping(value = "/add", method = {RequestMethod.POST}, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<Order> param) {
        log.debug("post: {}", param);
        try {
            Order order = param.getData();
            orderManager.add(order);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = {RequestMethod.POST}, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<Order> param) {
        log.debug("putPart: {}", param);
        try {
            Order order = param.getData();
            orderservice.update(order);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("delete: {}", param);
        try {
            orderservice.delete(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Order>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Order> pageInfo = orderservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OrderDetail>> getDetail(@RequestBody ApiParam<Long> param) {
        log.debug("getDetail: {}", param);
        try {
            List<OrderDetail> details = orderservice.queryDetailByOrderId(param.getData());
            log.debug("get result: {}", details);
            return ApiResult.success(details);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/audit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> audit(@RequestBody ApiParam<Long> param) {
        log.debug("audit: {}", param);
        try {
            orderservice.check(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/unaudit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> unaudit(@RequestBody ApiParam<Long> param) {
        log.debug("unaudit: {}", param);
        try {
            orderservice.unCheck(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // 关闭订单
    @RequestMapping(value = "/close", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> close(@RequestBody ApiParam<Long> param) {
        log.debug("getList: {}", param);
        try {
            if(param.getData() == null){
                throw new BusinessException("请选择一条订单！");
            }
            orderservice.closeOrder(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // 批量选择明细
    @RequestMapping(value = "/checkList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OrderDetail>> checkList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("selectByVin: {}", param);
        try {

            List<OrderDetail> orderDetails = orderservice.queryDetails(param.getData());
            return ApiResult.success(orderDetails);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    //批量导入
    @RequestMapping(value = "/upLoad", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> upLoad(@RequestParam("excelType") Integer excelType,
                                    @RequestParam("date") String date,
                                    @RequestParam("corpCustomerId") Long corpCustomerId,
                                    @RequestParam("sendUnitId") Long sendUnitId,
                                    @RequestParam("file") MultipartFile file,
                                    @RequestParam("auto") Boolean auto
    ) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            orderManager.importFile(excelType,date,corpCustomerId,sendUnitId,f,auto);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }

    @RequestMapping(value = "/transmit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> transmit(@RequestBody ApiParam<Long> param) {
        log.debug("transmit: {}", param);
        try {

            orderservice.release(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("下达失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/untransmit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> untransmit(@RequestBody ApiParam<Long> param) {
        log.debug("untransmit: {}", param);
        try {
            orderservice.unRelease(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("取消下达失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // 取消计划
    @RequestMapping(value = "/cancelPlan", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> cancelPlan(@RequestBody ApiParam<Map<String,Object>> param) {
        log.debug("cancelPlan: {}", param);
        try {
            if(param.getData().get("orderDetailId") == null){
                throw new BusinessException("请选择一条订单明细记录！");
            }
            Long orderDetailId = Long.valueOf(param.getData().get("orderDetailId").toString());
            String reason = param.getData().get("cancelReason") == null?null:param.getData().get("cancelReason").toString().trim();
            orderservice.closeVin(orderDetailId,reason);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("计划取消失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // 新增行表体车架号参照
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OrderDetail>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getReferenceList: {}", param);
        try {

            PageInfo<OrderDetail> pageInfo = orderservice.queryDetails(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 通过客户ID 获取所有客户订单并去重
     * @param param
     * @return
     */
    @RequestMapping(value = "/getOriginOrder", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Order>> getOriginOrder(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getReferenceList: {}", param);
        try {

            PageInfo<Order> pageInfo = orderservice.getOriginOrder(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
