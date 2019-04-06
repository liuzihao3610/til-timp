package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.WarehouseLoadingPlan;

/**
* 仓库装车计划表
*
* @version 1.1.0
* @author Xiahong
*/
public interface WarehouseLoadingPlanService extends BaseService<WarehouseLoadingPlan> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);
	
	void updateByTransportOrderId(WarehouseLoadingPlan record);
	
	/**
	 * 根据车架号加预约单号修改
	 * @param carVin	车架号
	 * @param transportOrderId	运单主表id
	 * @param billStatus 状态
	 */
	void updateCarVin(String carVin,Long transportOrderId,Integer billStatus);
	
	/**
	 *	根据车架号加预约单号查询仓库装车计划信息 
	 * @param carVin	车架号
	 * @param transportOrderId	运单主表id
	 * @return
	 */
	WarehouseLoadingPlan queryByCarVin(String carVin,Long transportOrderId);
	
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
	
	/**
	 * 修改仓库装车计划状态
	 * @param record
	 */
	/*void updateStatus(WarehouseLoadingPlan record);*/
	
}
