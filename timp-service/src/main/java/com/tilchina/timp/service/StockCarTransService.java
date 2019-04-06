package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.StockCarTrans;

import java.util.List;

/**
* 商品车运输记录表
*
* @version 1.0.0
* @author Xiahong
*/
public interface StockCarTransService extends BaseService<StockCarTrans> {
	
	void logicDeleteByStockCarId(Long id);
	
	/**
	 * 检查数据
	 * @param transportOrderDetailId 运单子表id
	 * @return 
	 */
	StockCarTrans examineStockCarTrans(Long transportOrderDetailId);

    List<StockCarTrans> queryByDetailIds(List<Long> detailIds);
}
