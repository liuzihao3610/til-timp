package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.vo.PendingOrderVO;

import java.util.List;
import java.util.Map;

/**
* 订单子表
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface OrderDetailService extends BaseService<OrderDetail> {

	void deleteList(int[] data);
	
	void add(Order order,OrderDetail orderDetail);
	
	OrderDetail queryByVin(String  carVin);

	List<OrderDetail> queryByOrderId(Long data);

	void deleteByOrderId(Long orderId);

	List<OrderDetail> checkList(Map<String, Object> map);

	List<OrderDetail> queryOrderDetail(Map<String, Object> map);

	OrderDetail getByVin(Map<String, Object> map);

    List<PendingOrderVO> queryForAssembly(Map<String, Object> map);

    PageInfo<OrderDetail> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	OrderDetail getOne(Map<String, Object> map);

	List<OrderDetail> queryByCarVin(String carVin);

	OrderDetail queryForOutsourcingReconciliation(Map<String, Object> params);
}
