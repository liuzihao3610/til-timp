package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.OutsourcingReconciliation;
import org.springframework.stereotype.Repository;

/**
* 外协对账主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface OutsourcingReconciliationMapper extends BaseMapper<OutsourcingReconciliation> {

	OutsourcingReconciliation queryByReconciliationNumber(String reconciliationNumber);

	void logicalDelete(Long reconciliationId);

	void updateDocumentsCheckState(OutsourcingReconciliation reconciliation);
}
