package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.vo.QuotationVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 客户报价明细
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface QuotationDetailMapper extends BaseMapper<QuotationDetail> {

	List<QuotationDetail> selectDetailsByQuotationId(Long quotationId);

	List<QuotationDetail> selectByCitys(Map<String,Object> map);

	QuotationDetail selectByQuotationVO(QuotationVO quotationVO);

	QuotationDetail queryByQuotationVO(QuotationVO quotationVO);
}
