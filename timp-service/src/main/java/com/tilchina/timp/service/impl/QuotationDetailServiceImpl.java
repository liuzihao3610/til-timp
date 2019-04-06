package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.QuotationDetailMapper;
import com.tilchina.timp.model.Order;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.service.QuotationDetailService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.vo.QuotationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* 客户报价明细
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class QuotationDetailServiceImpl extends BaseServiceImpl<QuotationDetail> implements QuotationDetailService {

	@Autowired
    private QuotationDetailMapper detailMapper;
	
	@Override
	protected BaseMapper<QuotationDetail> getMapper() {
		return detailMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(QuotationDetail quotationdetail) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkInteger("NO", "quotationPlan", "报价方案(0:按台", quotationdetail.getQuotationPlan(), 10));
		s.append(CheckUtils.checkInteger("YES", "jobType", "作业类型", quotationdetail.getJobType(), 10));
		s.append(CheckUtils.checkInteger("YES", "leadTime", "交期", quotationdetail.getLeadTime(), 10));
//		s.append(CheckUtils.checkBigDecimal("YES", "unitPrice", "每公里单价", quotationdetail.getUnitPrice(), 10, 2));
//		s.append(CheckUtils.checkBigDecimal("YES", "totalPrice", "总价", quotationdetail.getTotalPrice(), 10, 2));
//		s.append(CheckUtils.checkBigDecimal("YES", "taxRate", "税率", quotationdetail.getTaxRate(), 10, 2));
		s.append(CheckUtils.checkString("YES", "priceDesc", "价格描述ID", quotationdetail.getPriceDesc(), 200));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志(0:否", quotationdetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(QuotationDetail quotationdetail) {
        StringBuilder s = checkNewRecord(quotationdetail);
        s.append(CheckUtils.checkPrimaryKey(quotationdetail.getQuotationDetailId()));
		return s;
	}

	@Override
	public List<QuotationDetail> selectDetailsByQuotationId(Long quotationId) {

		try {
			List<QuotationDetail> details = detailMapper.selectDetailsByQuotationId(quotationId);
			return details;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public QuotationDetail selectByQuotationVO(QuotationVO quotationVO) {

		try {
			QuotationDetail quotationDetail = detailMapper.selectByQuotationVO(quotationVO);
			return quotationDetail;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<QuotationDetail> queryByCitys(Long customerId, Long vendorCorpId, Date orderDate, Integer jobType, List<QuotationDetail> citys){
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("customerId",customerId);
		paramMap.put("vendorCorpId",vendorCorpId);
		paramMap.put("effectiveDate",orderDate);
		paramMap.put("jobType",jobType);
		paramMap.put("citys",citys);
		return detailMapper.selectByCitys(paramMap);
	}

	@Override
	public void getQuotationByOrder(Order order){
        getQuotationByOrder(order.getCorpCustomerId(),order.getCorpCarrierId(),order.getOrderDate(),order.getWorkType(),order.getOds());
    }

	@Override
	public void getQuotationByOrder(Long customerId, Long vendorCorpId, Date orderDate, Integer jobType, List<OrderDetail> details) throws RuntimeException{
        Set<String> citys = new HashSet<>();
        for (OrderDetail detail : details) {
            // 汇总报价参数
            citys.add(detail.getSendCityId()+"_"+detail.getReceiveCityId());
        }
        // 构造获取报价参数
        List<QuotationDetail> paramCitys = new ArrayList<>();
        for (String city : citys) {
            QuotationDetail d = new QuotationDetail();
            String[] cs = city.split("_");
            d.setSendCityId(Long.valueOf(cs[0]));
            d.setRecvCityId(Long.valueOf(cs[1]));
            paramCitys.add(d);
        }

        List<QuotationDetail> quos = queryByCitys(customerId,vendorCorpId,orderDate,jobType,paramCitys);
        for (OrderDetail detail : details) {
            List<QuotationDetail> quo = quos.stream().filter(q ->
                    detail.getSendCityId().equals(q.getSendCityId()) && detail.getReceiveCityId().equals(q.getRecvCityId())
                    && detail.getBrandId().equals(q.getCarBrandId()) && detail.getCarTypeId() !=null
                    && q.getCarTypeId() != null && detail.getCarTypeId().equals(q.getCarTypeId())
					&& q.getCarModelId() !=null
                    && detail.getCarId().equals(q.getCarModelId())).collect(Collectors.toList());
            QuotationDetail qDetail = getSingle(quo);
            if(qDetail != null){
                detail.setQuotedPriceId(qDetail.getQuotationId());
                detail.setClaimDeliveryDate(DateUtil.getDate(detail.getClaimLoadDate(),qDetail.getLeadTime()));
                detail.setCustomerQuotedPrice(qDetail.getTotalPrice());
                continue;
            }

            quo = quos.stream().filter(q ->
                    detail.getSendCityId().equals(q.getSendCityId()) && detail.getReceiveCityId().equals(q.getRecvCityId())
                            && detail.getBrandId().equals(q.getCarBrandId()) && q.getCarTypeId() == null
							&& q.getCarModelId() !=null
                            && detail.getCarId().equals(q.getCarModelId())).collect(Collectors.toList());
            qDetail = getSingle(quo);
            if(qDetail != null){
                detail.setQuotedPriceId(qDetail.getQuotationId());
                detail.setClaimDeliveryDate(DateUtil.getDate(detail.getClaimLoadDate(),qDetail.getLeadTime()));
                detail.setCustomerQuotedPrice(qDetail.getTotalPrice());
                continue;
            }

            quo = quos.stream().filter(q ->
                    detail.getSendCityId().equals(q.getSendCityId()) && detail.getReceiveCityId().equals(q.getRecvCityId())
                            && detail.getBrandId().equals(q.getCarBrandId()) && detail.getCarTypeId() !=null
                            && q.getCarTypeId() != null && detail.getCarTypeId().equals(q.getCarTypeId())
							&& q.getCarModelId() == null)
                    .collect(Collectors.toList());
            qDetail = getSingle(quo);
            if(qDetail != null){
                detail.setQuotedPriceId(qDetail.getQuotationId());
                detail.setClaimDeliveryDate(DateUtil.getDate(detail.getClaimLoadDate(),qDetail.getLeadTime()));
                detail.setCustomerQuotedPrice(qDetail.getTotalPrice());
                continue;
            }

            quo = quos.stream().filter(q ->
                    detail.getSendCityId().equals(q.getSendCityId()) && detail.getReceiveCityId().equals(q.getRecvCityId())
                            && detail.getBrandId().equals(q.getCarBrandId())
							&& q.getCarModelId() == null && q.getCarTypeId() == null).collect(Collectors.toList());
            qDetail = getSingle(quo);
            if(qDetail != null){
                detail.setQuotedPriceId(qDetail.getQuotationId());
                detail.setClaimDeliveryDate(DateUtil.getDate(detail.getClaimLoadDate(),qDetail.getLeadTime()));
                detail.setCustomerQuotedPrice(qDetail.getTotalPrice());
                continue;
            }

            throw new BusinessException("车架号："+detail.getCarVin()+"，无法获取交期，请维护客户交期信息。");
        }
    }

    private QuotationDetail getSingle(List<QuotationDetail> quo){
	    if(CollectionUtils.isNotEmpty(quo)){
	        if(quo.size() == 1){
	            return quo.get(0);
            }else{
	            quo = quo.stream().sorted(Comparator.comparing(QuotationDetail::getRefEffectiveDate).reversed()).collect(Collectors.toList());
	            return quo.get(0);
            }
        }
        return null;
    }

	@Override
	public QuotationDetail queryByQuotationVO(QuotationVO quotationVO) {

		try {
			QuotationVO[] quotationVOs = new QuotationVO[4];
			for (int i = 0; i < quotationVOs.length; i++) {
				quotationVOs[i] = new QuotationVO();
				BeanUtils.copyProperties(quotationVO, quotationVOs[i]);
			}

			quotationVOs[1].setCarModelId(null);    // 品牌，类别
			quotationVOs[2].setCarTypeId(null);     // 品牌，型号
			quotationVOs[3].setCarModelId(null);    // 品牌
			quotationVOs[3].setCarTypeId(null);

			QuotationDetail detail;
			detail = detailMapper.queryByQuotationVO(quotationVOs[0]);
			if (detail != null) return detail;

			detail = detailMapper.queryByQuotationVO(quotationVOs[1]);
			if (detail != null) return detail;

			detail = detailMapper.queryByQuotationVO(quotationVOs[2]);
			if (detail != null) return detail;

			detail = detailMapper.queryByQuotationVO(quotationVOs[3]);
			if (detail != null) return detail;

			return null;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
