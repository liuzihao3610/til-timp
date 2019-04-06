package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.LoadingReservation;
import com.tilchina.timp.model.TransportOrder;

/**
* 预约装车计划主表
*
* @version 1.1.0
* @author XiaHong
*/
public interface LoadingReservationService extends BaseService<LoadingReservation> {
	
	 void logicDeleteById(Long id);
	
	 void logicDeleteByIdList(int[]  ids);
	 
	 /**
	  * 取消计划
	  * @param transportOrderId 运单主表id
	  * @param billStatus 预约装车单据状态
	  */
	 void updateBill(Long transportOrderId,Integer billStatus);
	 
	 LoadingReservation queryById(Map<String, Object> map, int pageNum, int pageSize);
	
	 /**
	  * 审核/取消审核
	  * @param loadingReservation
	  * @param checkType 0=审核 1=取消审核
	  */
	void check(LoadingReservation loadingReservation, Integer checkType);
	
	/**
	 * 根据运单主表id删除
	 * @param id	运单主表id
	 */
	void logicDeleteByTransportOrderId(Long id);
	
	/**
	 * 根据运单主表id 批量删除
	 * @param id	运单主表id
	 */
	void logicDeleteByTransportOrderId(int[] ids);
	 
}
