package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.OutsourcingReconciliationDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 外协对账子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface OutsourcingReconciliationDetailMapper extends BaseMapper<OutsourcingReconciliationDetail> {

	List<OutsourcingReconciliationDetail> queryByReconciliationId(Long reconciliationId);

	void logicalDelete(Long reconciliationDetailId);

	void logicalDeleteByReconciliationId(Long reconciliationId);
}
