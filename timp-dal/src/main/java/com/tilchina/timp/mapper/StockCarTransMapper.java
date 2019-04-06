package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.StockCarTrans;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 商品车运输记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface StockCarTransMapper extends BaseMapper<StockCarTrans> {
	
	int logicDeleteByStockCarId(Long id);
	
	/**
	 * 检查数据
	 * @param transportOrderDetailId
	 * @return
	 */
	StockCarTrans examineStockCarTrans(Long transportOrderDetailId);

	List<StockCarTrans> selectByDetailIds(List<Long> detailIds);
	
}
