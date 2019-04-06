package com.tilchina.timp.manager;

import com.tilchina.timp.model.Order;

import java.io.File;

/**
 * 
 * 
 * @version 1.0.0 2018/5/21
 * @author WangShengguang   
 */ 
public interface OrderManager {
    void add(Order order);

    void importFile(Integer fileType, String orderDate, Long corpCustomerId, Long sendUnitId, File file, boolean auto);

    String checkDefault(Order order, Long sendUnitId);
}
