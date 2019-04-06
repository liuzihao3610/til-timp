package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.StockCar;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 商品车库存表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface StockCarMapper extends BaseMapper<StockCar> {
	
	/**
	 * 通过车架号去查询商品车库存表信息
	 * @param carVin
	 * @return
	 */
	/*StockCar selectByCarVin(String carVin);*/
	
	/**
	 * 参照
	 * @param map
	 * @return
	 */
	List<StockCar> selectstockCarRefer(Map<String, Object> map);
	
	/**
	 * 运单参照
	 * @param map
	 * @return
	 */
	List<StockCar> transportOrderRefer(Map<String, Object> map);
	
	/**
	 *  通过子订单ID去查询商品车库存表信息
	 * @param orderDetailId
	 * @return
	 */
	/*StockCar selectByOrderDetailId(Long orderDetailId);*/
	
	/**
	 *  通过主订单ID去删除商品车库存表信息
	 * @param orderId
	 */
	void logicDeleteByOrderId(Long orderId);
	
	/**
	 * 通过车架号和所在单位去查询商品车库存表信息
	 * @param carVin
	 * @return
	 */
	StockCar selectByCarVin(@Param("carVin")String carVin);
	
	/**
	 * 通过商品车库存表信息查询商品车库存表信息
	 * @param stockCar
	 * @return
	 */
	StockCar selectByStockCar(StockCar stockCar);
	
	List<StockCar> selectListByStockCar(StockCar stockCar);

	List<StockCar> selectByVins(Map<String,Object> map);

	void updateByPrimaryKey(List<StockCar> records);

	void updateByPrimaryKeySelective(List<StockCar> records);
	//更新交接单状态
	void updateTransStatus(StockCar stockCar);

	StockCar selectByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

	StockCar selectByTransOrderDetailId(@Param("transportOrderDetailId") Long transportOrderDetailId);

	List<StockCar> getVinList(Map<String, Object> map);
}
