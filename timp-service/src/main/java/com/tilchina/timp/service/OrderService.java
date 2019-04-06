package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.model.StockCar;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单处理
 * 
 * @version 1.0.0 2018/5/21
 * @author WangShengguang   
 */ 
public interface OrderService {

    List<Order> queryForSettlement(Long customerId, Long carrierId, Date startDate, Date endDate);

    List<Order> queryForSettlement(Long customerId, Long carrierId, List<String> vins);

    List<Order> queryForSettlement(Map<String, Object> map);

    void updateForReconciliation(List<Long> orderDetailIds, boolean isReverse);

    void updateForTransfer(List<Long> orderDetailIds);

    void updateForSettlement(List<Long> orderDetailIds);

    List<Order> queryList(Map<String, Object> map);

    PageInfo<Order> queryList(Map<String, Object> map, int pageNum, int pageSize);

    List<OrderDetail> queryDetailByDetailIds(List<Long> detailIds);

    List<OrderDetail> queryDetailByOrderId(Long orderId);

    PageInfo<OrderDetail> queryDetails(Map<String, Object> map, int pageNum, int pageSize);

    List<OrderDetail> queryDetails(Map<String, Object> map);

    void add(Order order);

    void update(Order order);

    void updateDetailSelective(List<OrderDetail> details);

    void delete(Long orderId);

    void check(Long orderId);

    void unCheck(Long orderId);

    void release(Long orderId);

    void unRelease(Long orderId);
    
    Order queryById(Long orderId);

    @Transactional
    void addSection(Order order);

    void closeOrder(Long orderId);

    @Transactional
    void closeVin(Long detailId, String reason);

    void closeVin(Long detailId, List<StockCar> stocks);

    void updateDetailForClose(List<Long> orderDetilIds, String reason);
    /**
     * 通过车架号和订单类型获取原始订单
     * @param map
     * @return
     */
	Order getOriginOrder(Map<String, Object> map);

	Order queryByOrderNumber(String orderNumber);
	/**
	 * 通过客户查询原始订单并去重
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<Order> getOriginOrder(Map<String, Object> data, int pageNum, int pageSize);
}
