package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CustomerReconciliationDetail;

import java.util.List;
import java.util.Map;

/**
* 客户对账明细表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CustomerReconciliationDetailService extends BaseService<CustomerReconciliationDetail> {

	void modifyHedgeAmount(Map<String, Object> map);

	void logicalDelete(Long reconciliationDetailId);

	List<CustomerReconciliationDetail> selectByReconciliationId(Long reconciliationId);

	CustomerReconciliationDetail queryForFeedback(Map<String, Object> params);

	List<String> selectCarVin();

	void updateAmountForReconciliationDetail(Long reconciliationId);

	void updateDeductionReasonsForReconciliationDetail(Long reconciliationDetailId);
}
