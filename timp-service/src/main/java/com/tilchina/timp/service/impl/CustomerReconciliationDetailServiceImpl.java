package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.mapper.CustomerReconciliationDetailMapper;
import com.tilchina.timp.model.CustomerReconciliation;
import com.tilchina.timp.model.CustomerReconciliationDeduction;
import com.tilchina.timp.model.CustomerReconciliationDetail;
import com.tilchina.timp.service.CustomerReconciliationDeductionService;
import com.tilchina.timp.service.CustomerReconciliationDetailService;
import com.tilchina.timp.service.CustomerReconciliationService;
import com.tilchina.timp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 客户对账明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CustomerReconciliationDetailServiceImpl extends BaseServiceImpl<CustomerReconciliationDetail> implements CustomerReconciliationDetailService {

	@Autowired
    private CustomerReconciliationDetailMapper detailMapper;

	@Autowired
	private CustomerReconciliationService reconciliationService;

	@Autowired
	private CustomerReconciliationDeductionService deductionService;
	
	@Override
	protected BaseMapper<CustomerReconciliationDetail> getMapper() {
		return detailMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CustomerReconciliationDetail customerreconciliationdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "orderNumber", "订单号", customerreconciliationdetail.getOrderNumber(), 20));
        s.append(CheckUtils.checkString("YES", "productionNumber", "生产号", customerreconciliationdetail.getProductionNumber(), 20));
		s.append(CheckUtils.checkString("NO", "carVin", "车架号", customerreconciliationdetail.getCarVin(), 20));
        s.append(CheckUtils.checkString("YES", "deductionReason", "扣款原因", customerreconciliationdetail.getDeductionReason(), 200));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", customerreconciliationdetail.getFlag(), 10));
		// s.append(CheckUtils.checkString("NO", "transportOrderNumber", "运单号", customerreconciliationdetail.getTransportOrderNumber(), 20));
		// s.append(CheckUtils.checkDate("YES", "designatedLoadingDate", "指定装车日期", customerreconciliationdetail.getDesignatedLoadingDate()));
		// s.append(CheckUtils.checkDate("YES", "actualLoadingDate", "实际装车日期", customerreconciliationdetail.getActualLoadingDate()));
		// s.append(CheckUtils.checkBigDecimal("YES", "quotationPrice", "客户报价", customerreconciliationdetail.getQuotationPrice(), 10, 2));
		// s.append(CheckUtils.checkBigDecimal("YES", "hedgeAmount", "对冲金额", customerreconciliationdetail.getHedgeAmount(), 10, 2));
		// s.append(CheckUtils.checkBigDecimal("YES", "settlementPrice", "结算价格", customerreconciliationdetail.getSettlementPrice(), 10, 2));
		// s.append(CheckUtils.checkBigDecimal("YES", "deductionAmount", "扣款总额", customerreconciliationdetail.getDeductionAmount(), 10, 2));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CustomerReconciliationDetail customerreconciliationdetail) {
        StringBuilder s = checkNewRecord(customerreconciliationdetail);
        s.append(CheckUtils.checkPrimaryKey(customerreconciliationdetail.getReconciliationDetailId()));
		return s;
	}

	@Transactional
	@Override
	public void modifyHedgeAmount(Map<String, Object> map) {

		Long reconciliationDetailId;
		BigDecimal hedgeAmount;

		try {
			reconciliationDetailId = Long.parseLong(map.get("reconciliationDetailId").toString());
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		// 检查用户输入的对冲金额是否为有效数字格式
		if (RegexUtil.validate("regex.amount.hedge", map.get("hedgeAmount").toString())) {
			hedgeAmount = new BigDecimal(map.get("hedgeAmount").toString());
		} else {
			throw new RuntimeException("对冲金额中含有非法字符，请重新输入。");
		}

		CustomerReconciliationDetail detail = queryById(reconciliationDetailId);
		CustomerReconciliation customerReconciliation = reconciliationService.queryById(detail.getReconciliationId());
		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前对账单已确认结算，无法修改对冲金额。");
		}
		if (detail == null) {
			throw new RuntimeException("找不到该记录，请重新选择");
		}

		BigDecimal settlementPrice = new BigDecimal(0);
		BigDecimal quotationPrice = detail.getQuotationPrice();
		if (quotationPrice != null) {
			settlementPrice = quotationPrice.add(hedgeAmount);
		}

		CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
		detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
		detailModel.setHedgeAmount(hedgeAmount);
		detailModel.setSettlementPrice(settlementPrice);
		updateSelective(detailModel);

		reconciliationService.updateAmountForReconciliation(customerReconciliation.getReconciliationId());
	}

	@Override
	public void logicalDelete(Long reconciliationDetailId) {

		try {
			detailMapper.logicalDelete(reconciliationDetailId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateDeductionReasonsForReconciliationDetail(Long reconciliationDetailId) {

		CustomerReconciliationDetail reconciliationDetail = queryById(reconciliationDetailId);
		if (reconciliationDetail == null) {
			throw new RuntimeException(String.format("ID: %d记录不存在", reconciliationDetailId));
		}

		StringBuilder sb = new StringBuilder();
		List<CustomerReconciliationDeduction> deductions = deductionService.selectDeductionsByReconciliationDetailId(reconciliationDetailId);
		if (CollectionUtils.isNotEmpty(deductions)) {
			List<String> deductionReasons = deductions.stream().map(CustomerReconciliationDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
			deductionReasons.forEach(deductionReason -> sb.append(String.format("%s, ", deductionReason)));
		}

		String deductionReason = "";
		int length = sb.toString().length();
		if (length > 2) {
			deductionReason = sb.toString().substring(0, length -2);
		}

		CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
		detailModel.setReconciliationDetailId(reconciliationDetail.getReconciliationDetailId());
		detailModel.setDeductionReason(deductionReason);
		updateSelective(detailModel);
	}

	@Transactional
	@Override
	public void updateAmountForReconciliationDetail(Long reconciliationId) {

		List<CustomerReconciliationDetail> details = selectByReconciliationId(reconciliationId);

		if (CollectionUtils.isNotEmpty(details)) {
			for (CustomerReconciliationDetail detail : details) {

				BigDecimal hedgeAmount = new BigDecimal(0);
				BigDecimal quotationPrice = new BigDecimal(0);
				BigDecimal settlementPrice = new BigDecimal(0);
				BigDecimal deductionAmount = new BigDecimal(0);

				if (detail.getQuotationPrice() != null) {
					quotationPrice = detail.getQuotationPrice();
					settlementPrice = quotationPrice;
				}

				List<CustomerReconciliationDeduction> deductions = deductionService.selectDeductionsByReconciliationDetailId(detail.getReconciliationDetailId());
				if (CollectionUtils.isNotEmpty(deductions)) {
					for (CustomerReconciliationDeduction deduction : deductions) {
						deductionAmount = deductionAmount.add(deduction.getDeductionAmount());
					}
					settlementPrice = settlementPrice.subtract(deductionAmount);
				}

				if (detail.getHedgeAmount() != null) {
					hedgeAmount = detail.getHedgeAmount();
					settlementPrice = quotationPrice.add(hedgeAmount);
				}

				CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
				detailModel.setSettlementPrice(settlementPrice);
				detailModel.setHedgeAmount(hedgeAmount);
				detailModel.setDeductionAmount(deductionAmount);
				updateSelective(detailModel);
			}
		}
	}

	@Override
	public List<CustomerReconciliationDetail> selectByReconciliationId(Long reconciliationId) {

		try {
			List<CustomerReconciliationDetail> customerReconciliationDetails = detailMapper.selectByReconciliationId(reconciliationId);
			return customerReconciliationDetails;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public CustomerReconciliationDetail queryForFeedback(Map<String, Object> params) {

		try {
			CustomerReconciliationDetail customerReconciliationDetail = detailMapper.queryForFeedback(params);
			return customerReconciliationDetail;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<String> selectCarVin() {

		try {
			List<String> carVins = detailMapper.selectCarVin();
			return carVins;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

	}
}
