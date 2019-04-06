package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.StockCarHis;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 商品车状态变更记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface StockCarHisMapper extends BaseMapper<StockCarHis> {
	
	/**
	 *  通过主订单ID去删除商品车库存表信息
	 * @param orderId
	 */
	void logicDeleteByOrderId(Long orderId);
}
