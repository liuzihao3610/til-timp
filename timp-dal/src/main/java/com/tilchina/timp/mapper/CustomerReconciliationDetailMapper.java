package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CustomerReconciliationDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 客户对账明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CustomerReconciliationDetailMapper extends BaseMapper<CustomerReconciliationDetail> {

	void logicalDelete(Long reconciliationDetailId);

	List<CustomerReconciliationDetail> selectByReconciliationId(Long reconciliationId);

	CustomerReconciliationDetail queryForFeedback(Map<String, Object> params);

	List<String> selectCarVin();
}
