package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Quotation;
import com.tilchina.timp.vo.QuotationVO;
import org.springframework.stereotype.Repository;

/**
* 客户报价
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface QuotationMapper extends BaseMapper<Quotation> {

	void updateFlagStateByPrimaryKey(Long quotationId);

	void updateCheckStateByPrimaryKey(Quotation quotation);

	Quotation selectByQuotationVO(QuotationVO quotationVO);
}
