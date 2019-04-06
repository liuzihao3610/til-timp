package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.vo.QuotationVO;

import java.util.Date;
import java.util.List;

/**
* 客户报价明细
*
* @version 1.0.0
* @author XueYuSong
*/
public interface QuotationDetailService extends BaseService<QuotationDetail> {

	List<QuotationDetail> selectDetailsByQuotationId(Long quotationId);

	List<QuotationDetail> queryByCitys(Long customerId, Long vendorCorpId, Date orderDate, Integer jobType, List<QuotationDetail> citys);

    void getQuotationByOrder(Order order);

    void getQuotationByOrder(Long customerId, Long vendorCorpId, Date orderDate, Integer jobType, List<OrderDetail> details);

	QuotationDetail selectByQuotationVO(QuotationVO quotationVO);

	QuotationDetail queryByQuotationVO(QuotationVO quotationVO);
}
