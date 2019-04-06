package com.tilchina.timp.service;


import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.model.TransferOrder;
import com.tilchina.timp.model.TransportOrder;

/**
* 客户交接单
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface TransferOrderService extends BaseService<TransferOrder> {

	void deleteList(int[] data);

	List<Map<String, Object>> getCarStatus();

	void updateByVin(List<TransferOrder> transferOrders);

	TransferOrder selectByVin(Map<String, Object> map);
	/**
	 * 客户接单后生成交接单
	 * @param transportOrder
	 */
	void addTranferOrder(TransportOrder transportOrder);
	
	//所有未签收的交接单
	List<Map<String, Object>> getOrders(Map<String, Object> data);
	//添加照片
	void addPhoto(MultipartFile file, Long transferOrderId);
	//添加照片和信息
	void addInfoAndPhoto(TransferOrder transferOrder, MultipartFile file);
	/**
	 * 通过车架号获取交接单信息
	 * @param transferOrder
	 * @throws Exception
	 */
	void addOrder(TransferOrder transferOrder);

	/**
	 *通过运单ID删除交接单
	 * @param transportOrderId
	 */
    void deleteByTransportOrderId(Long transportOrderId);
	//更改交接单状态为回单
	void updateReceive(Map<String,Object> map);
}
