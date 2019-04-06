package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.mapper.QuotationPriceMapper;
import com.tilchina.timp.model.QuotationPrice;
import com.tilchina.timp.service.QuotationPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* 价格描述表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class QuotationPriceServiceImpl extends BaseServiceImpl<QuotationPrice> implements QuotationPriceService {

	@Autowired
    private QuotationPriceMapper quotationpricemapper;
	
	@Override
	protected BaseMapper<QuotationPrice> getMapper() {
		return quotationpricemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(QuotationPrice quotationprice) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "quotationAmount", "数量", quotationprice.getQuotationAmount(), 10));
        // s.append(CheckUtils.checkBigDecimal("NO", "quotationPrice", "价格", quotationprice.getQuotationPrice(), 10, 2));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志(0:否", quotationprice.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(QuotationPrice quotationprice) {
        StringBuilder s = checkNewRecord(quotationprice);
        s.append(CheckUtils.checkPrimaryKey(quotationprice.getQuotationPriceId()));
		return s;
	}
}
