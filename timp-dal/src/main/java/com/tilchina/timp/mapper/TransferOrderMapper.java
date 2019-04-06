package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.TransferOrder;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 客户交接单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface TransferOrderMapper extends BaseMapper<TransferOrder> {

	void deleteList(int[] data);

	void updateByVin(Map<String, Object> map);

	TransferOrder selectByVin(Map<String, Object> map);

	List<TransferOrder> query(Long transportOrderId);

	List<TransferOrder> getorders(int[] trans);
	//获取所有未签收单的交接单
	List<Map<String, Object>> getOrders(Map<String, Object> map);

	//通过运单ID删除
    void deleteByTransportOrderId(Long transportOrderId);
}
