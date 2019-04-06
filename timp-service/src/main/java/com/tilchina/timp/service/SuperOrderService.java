package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/13.
 */
public interface SuperOrderService {
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

    @Transactional
    void oneTouchOut(Long orderId, Long carrierId);

    void addSectionOrder(Order order);

    void addOrder(Order order);

    @Transactional
    void update(Order order);

    void checkDetailForOther(List<OrderDetail> details, boolean isSameCorp);

    void checkDetailForIn(List<OrderDetail> details);

    void checkOrder(Order order);
}
