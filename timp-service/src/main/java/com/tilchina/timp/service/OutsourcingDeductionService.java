package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.OutsourcingDeduction;

import java.util.List;

/**
* 外协对账扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface OutsourcingDeductionService extends BaseService<OutsourcingDeduction> {

	List<OutsourcingDeduction> queryByReconciliationId(Long reconciliationId);

	List<OutsourcingDeduction> queryByReconciliationDetailId(Long reconciliationDetailId);
}
