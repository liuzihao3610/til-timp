package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.OutsourcingReconciliationDetail;

import java.util.List;
import java.util.Map;

/**
* 外协对账子表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface OutsourcingReconciliationDetailService extends BaseService<OutsourcingReconciliationDetail> {

	List<OutsourcingReconciliationDetail> queryByReconciliationId(Long reconciliationId);

	PageInfo<OutsourcingReconciliationDetail> queryByReconciliationId(Long reconciliationId, int pageNum, int pageSize);

	void logicalDelete(Long reconciliationDetailId);

	void logicalDeleteByReconciliationId(Long reconciliationId);

	void modifyHedgeAmount(Map<String, Object> params);
}
