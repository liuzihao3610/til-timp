package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CustomerReconciliation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 客户对账表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CustomerReconciliationMapper extends BaseMapper<CustomerReconciliation> {

	void updateCheckStateByPrimaryKey(CustomerReconciliation customerReconciliation);

	void logicalDelete(Long reconciliationId);

	List<CustomerReconciliation> selectByReconciliationNumber(String reconciliationNumber);
}
