package com.tilchina.timp.service.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.TransportOrder;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.TransportPrice;
import com.tilchina.timp.service.TransportPriceService;
import com.tilchina.timp.mapper.TransportPriceMapper;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class TransportPriceServiceImpl extends BaseServiceImpl<TransportPrice> implements TransportPriceService {

	@Autowired
    private TransportPriceMapper transportpricemapper;

	@Override
	protected BaseMapper<TransportPrice> getMapper() {
		return transportpricemapper;
	}
	private static boolean isNullAble(String nullable) {
        if ("YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase())) {
            return true;
        }
        return false;
    }
	
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment+"["+attName+"]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value "+decimal+ " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value "+decimal+ " scale out of range [" + scale + "].";
        }
        return "";
    }

	@Override
	protected StringBuilder checkNewRecord(TransportPrice transportprice) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transportPriceCode", "运价编号", transportprice.getTransportPriceCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "priceType", "报价方式:0=单台,1=按板按台,2=按板按板", transportprice.getPriceType(), 10));
        s.append(checkBigDecimal("NO", "price", "价格(元)", transportprice.getPrice(), 10, 2));
        s.append(CheckUtils.checkInteger("NO", "examination", "车检标志", transportprice.getExamination(), 10));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transportprice.getRemark(), 200));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "状态:0=制单,1=审核", transportprice.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "制单日期", transportprice.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", transportprice.getCheckDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportPrice transportprice) {
        StringBuilder s = checkNewRecord(transportprice);
        s.append(CheckUtils.checkPrimaryKey(transportprice.getTransportPriceId()));
		return s;
	}
}
