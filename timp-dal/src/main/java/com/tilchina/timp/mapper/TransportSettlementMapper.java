package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.TransportSettlement;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface TransportSettlementMapper {
	
    int deleteByPrimaryKey(Long id);

    void insert(TransportSettlement record);
    
    void insert(List<TransportSettlement> records);
    
    void insertSelective(TransportSettlement record);

    TransportSettlement selectByPrimaryKey(Long id);
    
    TransportSettlement select(TransportSettlement record);

    void updateByPrimaryKeySelective(List<TransportSettlement> records);

    int updateByPrimaryKeySelective(TransportSettlement record);

    int updateByPrimaryKey(TransportSettlement record);
    
    void update(List<TransportSettlement> records);
    
    List<TransportSettlement> selectList(Map<String, Object> map);

    List<TransportSettlement> selectByTransportOrderIds(@Param("transportOrderIds")List<Long> transportOrderIds);
    
    
	TransportSettlement selectByTransportOrderId(Long transportOrderId);

	void deleteByTransportOrderId(Long transportOrderId);
	
	void deleteByTransportOrderIds(int[] transportOrderIds);

	void check(TransportSettlement updateSettlement);

	List<TransportSettlement> appSelectList(Map<String, Object> map);
    
}
