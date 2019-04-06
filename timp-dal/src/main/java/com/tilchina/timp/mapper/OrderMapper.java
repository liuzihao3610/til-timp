package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 订单主表
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface OrderMapper extends BaseMapper<Order> {

	void deleteList(int[] data);

	void unaudit(Long orderId);

	void untransmit(Long orderId);

	int check(Order order);

	int unCheck(Order order);

	int release(Order order);

	int unRelease(Order order);

	List<Order> selectByIds(@Param("orderIds") Set<Long> orderIds);

	Order queryById(Long orderId);

	void insertSelective(List<Order> orders);

	Order getOriginOrder(Map<String, Object> map);

	Order queryByOrderNumber(String orderNumber);
}
