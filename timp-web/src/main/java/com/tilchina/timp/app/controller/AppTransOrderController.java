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
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.ContactsOld;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.service.AppTransOrderService;
import com.tilchina.timp.vo.TransportPlanVO;

/**
* app:运单
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/app/transorder"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppTransOrderController{
	
	@Autowired
	private AppTransOrderService apptransOrderservice;
	
	/**
	 * 查询已分配、未完成的运单
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
	public ApiResult<List<TransportOrder>> getList(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	List<TransportOrder> transportOrders = apptransOrderservice.appQueryList(param.getData());
            log.debug("get result: {}", transportOrders);
            return ApiResult.success(transportOrders);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 历史运单查询，只显示当前司机、已完成的运单
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<TransportOrder>> query(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	List<TransportOrder> transportOrders = apptransOrderservice.appQueryListByPrimary(param.getData());
            log.debug("get result: {}", transportOrders);
            return ApiResult.success(transportOrders);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 确认接单
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/check", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.APP)
	public ApiResult<String> putPartCheck(@RequestBody AppApiParam<String> param) {
        try {
        	 TransportOrder t = (TransportOrder)JSON.parseObject(param.getData(), TransportOrder.class);
        	 apptransOrderservice.updateStatusCheck(t);
            log.debug("get result: {}", t);
            return ApiResult.success("已接单完成！");
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 发车
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/run", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<String> putPartRun(@RequestBody AppApiParam<String> param) {
        try {
        	 TransportOrder t = (TransportOrder)JSON.parseObject(param.getData(), TransportOrder.class);
        	 apptransOrderservice.updateStatusRun(t);
            log.debug("get result: {}", t);
            return ApiResult.success("该运单已成功发车！");
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 查看运输计划
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/route", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<TransportPlanVO> getListRoute(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	TransportPlanVO TransportPlanVO = apptransOrderservice.appQueryListRoute(param.getData());
            log.debug("get result: {}", TransportPlanVO);
            return ApiResult.success(TransportPlanVO);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 根据传入得经纬度提示距离为150公里内得收货单位得联系方式
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/contact", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<ContactsOld>> unitContact(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	List<ContactsOld>  contacts = apptransOrderservice.appQueryUnitContacts(param.getData());
            log.debug("get result: {}", contacts);
            return ApiResult.success(contacts);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 在途/地图：查看运输计划
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/assistRoute", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.APP)
	public ApiResult<List<TransportPlanVO>> getAassistRoute(@RequestBody AppApiParam<Map<String, Object>> param) {
        try {
        	List<TransportPlanVO> TransportPlanVOs = apptransOrderservice.appQueryListAassistRoute(param.getData());
            log.debug("get result: {}", TransportPlanVOs);
            return ApiResult.success(TransportPlanVOs);
        } catch (Exception e) {
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	
}