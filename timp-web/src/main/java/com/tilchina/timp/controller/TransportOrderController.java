package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.service.StockCarService;
import com.tilchina.timp.service.TransportOrderDetailService;
import com.tilchina.timp.service.TransportOrderService;

/**
* 运单主表
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transportorder"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportOrderController extends BaseControllerImpl<TransportOrder>{

	@Autowired
	private TransportOrderService transportorderservice;
	
	@Autowired
	private TransportOrderDetailService transportorderdetailservice;

	@Autowired
	private StockCarService  stockCarService;
    
	@Override
	protected BaseService<TransportOrder> getService() {
		return transportorderservice;
	}
	
    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<TransportOrder> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            TransportOrder TransportOrder = getService().queryById(param.getData());
            log.debug("get result: {}", TransportOrder);
            return ApiResult.success(TransportOrder);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportOrder>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransportOrder> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@RequestMapping(value = "/getByprimary", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<TransportOrder> getByprimaryId(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("get: {}", param);
        try {
        	TransportOrder t = transportorderservice.queryById(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            TransportOrder t = (TransportOrder)JSON.parseObject(param.getData(), TransportOrder.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	transportorderservice.logicDeleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	transportorderservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/addDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<String> postDetail(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            TransportOrderDetail t = (TransportOrderDetail)JSON.parseObject(param.getData(), TransportOrderDetail.class);
            transportorderdetailservice.add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/removeDetailList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteDetailList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	transportorderdetailservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/putPartDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPartDetail(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            TransportOrderDetail t = (TransportOrderDetail)JSON.parseObject(param.getData(), TransportOrderDetail.class);
            transportorderdetailservice.updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/getDetail", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<TransportOrderDetail> getByDetailId(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	TransportOrderDetail t = transportorderdetailservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getDetailList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportOrderDetail>> getDetailList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<TransportOrderDetail> pageInfo = transportorderdetailservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
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
    public ApiResult<String> putCheck(@RequestBody ApiParam<TransportOrder> param) {
        log.debug("putCheck: {}", param);
        try {
        	transportorderservice.updateCheck(param.getData());
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
    public ApiResult<String> putCancelCheck(@RequestBody ApiParam<TransportOrder> param) {
        log.debug("putCheck: {}", param);
        try {
        	transportorderservice.updateCancelCheck(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    
    /**
     * 下达
     * @param param
     * @return
     */
    @RequestMapping(value = "/transmit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putTransmit(@RequestBody ApiParam<Long> param) {
        log.debug("putCheck: {}", param);
        try {
        	transportorderservice.updateTransmit(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("下达失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 取消下达
     * @param param
     * @return
     */
    @RequestMapping(value = "/cancelTransmit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCancelTransmit(@RequestBody ApiParam<Long> param) {
        log.debug("putCheck: {}", param);
        try {
        	transportorderservice.updateCancelTransmit(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消下达失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 批量选择明细数据
     * @param param
     * @return
     */
    @RequestMapping(value = "/batchSelect", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<StockCar>> getcarVinList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	List<StockCar> transportOrders = stockCarService.batchSelectCarvin(param.getData());
            return ApiResult.success(transportOrders);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 取消运单
     * @param param
     * @return
     */
    @RequestMapping(value = "/cancelBill", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putBill(@RequestBody ApiParam<Long> param) {
        log.debug("putCheck: {}", param);
        try {
        	transportorderservice.updateBill(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消下达失败，param={}", param, e);
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
    public ApiResult<List<TransportOrder>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<TransportOrder> transportOrders = transportorderservice.queryRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(transportOrders);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
   
    /**
     * 参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/getDetailReference", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportOrderDetail>> getDetailRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getDetailReference: {}", param);
        try {
        	PageInfo<TransportOrderDetail> detais = transportorderdetailservice.queryDetailRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(detais);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 修改到店时间（对外）
     * @param param
     * @return
     */
    @RequestMapping(value = "/putRrriveDateBesides", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putRrriveDateBesides(@RequestBody ApiParam<TransportOrder> param) {
        log.debug("putRrriveDateBesides: {}", param);
        try {
        	transportorderservice.updateRrriveDateBesides(param.getData().getDetails());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改到店时间（对外），param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    // Import: 导入EXCEL数据
    @PostMapping(value = "/importAssembly")
	@Auth(ClientType.WEB)
	public ApiResult<String> importAssembly(@RequestParam("file") MultipartFile file) {
		try {
			transportorderservice.importAssembly(file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

    /**
     * 通过订单号获取运单
     * @param param 订单ID
     * @return
     */
    @RequestMapping(value = "/getByOrderId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransportOrder>> getByOrderId(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getDetailReference: {}", param);
        try {
        	PageInfo<TransportOrder> transportOrders = transportorderservice.getByOrderId(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(transportOrders);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    
}
