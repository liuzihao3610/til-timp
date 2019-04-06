package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.CarStatus;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.model.TransportOrderDetail;

/**
* 商品车库存表
*
* @version 1.0.0
* @author Xiahong
*/
public interface StockCarService extends BaseService<StockCar> {

	/**
	 * 修改车辆状态:添加商品车库存表变更记录信息
	 * @param stockCar
	 * @param orderBillStatus
	 */
	void updateCarStatus(List<StockCar> stockCar,Integer orderBillStatus);


	/**
	 * 修改车辆状态:添加商品车库存表变更记录信息
	 * @param stockCar
	 * @param orderBillStatus
	 */
	void updateCarStatus(StockCar stockCar,Integer orderBillStatus);
	
	/**
	 * 通过车架号修改车辆状态:添加商品车库存表变更记录信息
	 * @param carVin	车架号
	 * @param currentUnitId 所在单位(指当前车辆在哪个单位)单位id
	 * @param orderBillStatus
	 */
	/*void updateCarStatus(String carVin,Long currentUnitId,Integer orderBillStatus);*/
	
	/**
	 * 通过车架号去查询商品车库存表信息
	 * @param carVin
	 * @return
	 */
	StockCar queryByCarVin(String carVin);

    StockCar queryByTransOrderDetailId(Long transDetailOrderId);

    /**
	 * 查询商品车库存表信息
	 * @param stockCar
	 * @return
	 */
	StockCar queryByStockCar(StockCar stockCar);

	/**
	 * 关闭订单/取消计划/取消下达
	 * @param orderId 订单主表id
	 * @param corpId  承运公司
	 * @param shutType 0=关闭订单 1=取消计划 2=取消下达
	 * @return
	 */
	StringBuilder shutOrder(Long orderId,Long corpId,Integer shutType,String carVIn);
	
	/**
	 *  通过子订单ID去查询商品车库存表信息
	 * @param orderDetailId
	 * @return
	 */
/*	StockCar queryByOrderDetailId(Long orderDetailId);*/
	
	/**
	 * 通过订单id逻辑删除商品车库存表
	 * @param id
	 */
	void logicDeleteByOrderId(Long OrderId);
	
	/**
	 * 订单商品车库存表参照
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<StockCar> stockCarRefer(Map<String, Object> map,int pageNum, int pageSize);
	
	/**
	 * 运单车架号参照
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<StockCar> transportOrderRefer(Map<String, Object> map, int pageNum, int pageSize);
	
	/**
	 * 批量选择车架号
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	List<StockCar> batchSelectCarvin(Map<String, Object> map);

	List<StockCar> queryByVins(List<String> vins, Long corpId);

	void stockForOrder(Order order, boolean isReverse);

    void stockOut(List<StockCar> stocks, Order order);

	void stockOutReverse(List<StockCar> stocks);

	List<StockCar> stockForClose(Long corpId, Order order, List<OrderDetail> details);

	List<StockCar> stockForOut(Long corpId, Order order, List<OrderDetail> details);

	List<StockCar> stockForIn(Long corpId, Order order, List<OrderDetail> details);

	List<StockCar> updateStock(Long corpId, Order order, List<OrderDetail> details, CarStatus status);

	void updateTransStatus(StockCar stockCar);

    void closeCar(List<StockCar> stockCars);

    StockCar queryByOrderDetailId(Long orderDetailId);

    void updateBySelective(List<StockCar> stockCars);

	void updateBySelective(StockCar stockCar);
	/**
	 * 获取车辆状态为6-10的车架号列表
	 * @param map 
	 * @param pageSize 
	 * @param pageNum 
	 * @return
	 */
	PageInfo<StockCar> getVinList(Map<String, Object> map, int pageNum, int pageSize);
}
