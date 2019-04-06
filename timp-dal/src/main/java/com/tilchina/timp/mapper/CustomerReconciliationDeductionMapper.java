package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CustomerReconciliationDeduction;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 客户对账明细扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CustomerReconciliationDeductionMapper extends BaseMapper<CustomerReconciliationDeduction> {

	void logicalDelete(Long reconciliationDeductionId);

	void physicalDelete(Long reconciliationDeductionId);

	void physicalDeleteRange(@Param("reconciliationDeductionIds") List<Long> reconciliationDeductionIds);

	List<CustomerReconciliationDeduction> selectDeductionsByReconciliationId(Long reconciliationId);

	List<CustomerReconciliationDeduction> selectDeductionsByReconciliationDetailId(Long reconciliationDetailId);
}
