package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CustomerReconciliationDeduction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 客户对账明细扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CustomerReconciliationDeductionService extends BaseService<CustomerReconciliationDeduction> {

	void logicalDelete(Long reconciliationDeductionId);

	void physicalDelete(Long reconciliationDeductionId);

	void physicalDelete(@Param("reconciliationDeductionIds") List<Long> reconciliationDeductionIds);

	void createWithoutUpdate(CustomerReconciliationDeduction customerReconciliationDeduction);

	void updateWithoutUpdate(CustomerReconciliationDeduction customerReconciliationDeduction);

	void deleteWithoutUpdate(Long customerReconciliationDeductionId);

	List<CustomerReconciliationDeduction> selectDeductionsByReconciliationId(Long reconciliationId);

	List<CustomerReconciliationDeduction> selectDeductionsByReconciliationDetailId(Long reconciliationDetailId);
}
