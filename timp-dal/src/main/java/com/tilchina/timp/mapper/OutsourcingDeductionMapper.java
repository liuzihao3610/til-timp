package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.OutsourcingDeduction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 外协对账扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface OutsourcingDeductionMapper extends BaseMapper<OutsourcingDeduction> {

	List<OutsourcingDeduction> queryByReconciliationId(Long reconciliationId);

	List<OutsourcingDeduction> queryByReconciliationDetailId(Long reconciliationDetailId);
}
