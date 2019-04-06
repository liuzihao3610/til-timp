package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.StockCarHis;

/**
* 商品车状态变更记录表
*
* @version 1.0.0
* @author Xiahong
*/
public interface StockCarHisService extends BaseService<StockCarHis> {
	
	/**
	 * 修改车辆状态
	 * @param stockCarHis
	 */
	void updateCarStatus(StockCarHis stockCarHis);
	
	/**
	 * 逻辑删除商品车库存表
	 * @param id
	 */
	void logicDeleteByOrderId(Long StockCarId);
	
}
