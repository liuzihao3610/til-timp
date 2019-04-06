package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.StockCarOut;

import java.util.List;

/**
* 商品车出库记录表
*
* @version 1.0.0
* @author Xiahong
*/
public interface StockCarOutService extends BaseService<StockCarOut> {

    void updateSelective(List<StockCarOut> outs);

    /**
	 * 修改车辆状态
	 * @param stockcarout
	 */
	void updateCarStatus(StockCarOut stockcarout);
	
	/**
	 * 作废单据
	 * @param stockcarout
	 */
	void updateCancellationBill(Long id);
	
	/**
	 * 恢复单据
	 * @param stockcarout
	 */
	void updateRecoverBill(Long id);

	List<StockCarOut> queryByStockIds(List<Long> outIds);
}
