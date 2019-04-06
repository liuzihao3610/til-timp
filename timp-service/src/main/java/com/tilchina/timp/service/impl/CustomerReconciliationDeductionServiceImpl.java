package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.mapper.CustomerReconciliationDeductionMapper;
import com.tilchina.timp.model.CustomerReconciliation;
import com.tilchina.timp.model.CustomerReconciliationDeduction;
import com.tilchina.timp.model.CustomerReconciliationDetail;
import com.tilchina.timp.service.CustomerReconciliationDeductionService;
import com.tilchina.timp.service.CustomerReconciliationDetailService;
import com.tilchina.timp.service.CustomerReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* 客户对账明细扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CustomerReconciliationDeductionServiceImpl extends BaseServiceImpl<CustomerReconciliationDeduction> implements CustomerReconciliationDeductionService {

	@Autowired
    private CustomerReconciliationDeductionMapper deductionMapper;

	@Autowired
	private CustomerReconciliationService reconciliationService;

	@Autowired
	private CustomerReconciliationDetailService reconciliationDetailService;

	@Override
	public PageInfo<CustomerReconciliationDeduction> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo(this.getMapper().selectList(map));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	protected BaseMapper<CustomerReconciliationDeduction> getMapper() {
		return deductionMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CustomerReconciliationDeduction customerreconciliationdeduction) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkBigDecimal("NO", "deductionAmount", "扣款金额", customerreconciliationdeduction.getDeductionAmount(), 10, 2));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", customerreconciliationdeduction.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", customerreconciliationdeduction.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CustomerReconciliationDeduction customerreconciliationdeduction) {
        StringBuilder s = checkNewRecord(customerreconciliationdeduction);
        s.append(CheckUtils.checkPrimaryKey(customerreconciliationdeduction.getReconciliationDeductionId()));
		return s;
	}

	@Override
	public void logicalDelete(Long reconciliationDeductionId) {

		try {
			CustomerReconciliationDeduction reconciliationDeduction = queryById(reconciliationDeductionId);
			if (reconciliationDeduction != null) {
				deductionMapper.logicalDelete(reconciliationDeductionId);
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void physicalDelete(Long reconciliationDeductionId) {
		try {
			CustomerReconciliationDeduction reconciliationDeduction = queryById(reconciliationDeductionId);
			if (reconciliationDeduction != null) {
				deductionMapper.physicalDelete(reconciliationDeductionId);
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void physicalDelete(List<Long> reconciliationDeductionIds) {
		try {
			deductionMapper.physicalDeleteRange(reconciliationDeductionIds);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void updateSelective(CustomerReconciliationDeduction newRecord) {

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(CheckUtils.checkLong("NO", "contractId", "对账扣款ID", newRecord.getReconciliationDeductionId(), 20));
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException(String.format("数据检查失败：%s", sb.toString()));
			}

			CustomerReconciliation customerReconciliation = reconciliationService.queryById(newRecord.getReconciliationId());
			CustomerReconciliationDetail customerReconciliationDetail = reconciliationDetailService.queryById(newRecord.getReconciliationDetailId());
			if (customerReconciliation == null) {
				throw new RuntimeException("对账单(ID)有误，请重新选择。");
			}
			if (customerReconciliationDetail == null) {
				throw new RuntimeException("对账单明细(ID)有误，请重新选择。");
			}
			if (customerReconciliation.getConfirmor() != null) {
				throw new RuntimeException("单据已结算，无法修改扣款项目。");
			}

			CustomerReconciliationDeduction oldRecord = queryById(newRecord.getReconciliationDeductionId());
			deductionMapper.updateByPrimaryKeySelective(newRecord);



			List<CustomerReconciliationDeduction> deductionList = selectDeductionsByReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			BigDecimal deductionAmount = deductionList.stream().map(CustomerReconciliationDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			List<String> detailDedcutionNames = deductionList.stream().map(CustomerReconciliationDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
			String detailDeductionReason = String.join(", ", detailDedcutionNames);

			BigDecimal hedgeAmount;
			BigDecimal quotationPrice = customerReconciliationDetail.getQuotationPrice();
			BigDecimal settlementPrice = customerReconciliationDetail.getSettlementPrice();

			if (ObjectUtils.allNotNull(settlementPrice)) {
				// 差价
				BigDecimal priceDifference = new BigDecimal(0);

				if (oldRecord.getDeductionAmount().compareTo(newRecord.getDeductionAmount()) > 0) {
					priceDifference = oldRecord.getDeductionAmount().subtract(newRecord.getDeductionAmount());
					settlementPrice = settlementPrice.add(priceDifference);
				}
				if (oldRecord.getDeductionAmount().compareTo(newRecord.getDeductionAmount()) < 0) {
					priceDifference = newRecord.getDeductionAmount().subtract(oldRecord.getDeductionAmount());
					settlementPrice = settlementPrice.subtract(priceDifference);
				}
			} else {
				settlementPrice = new BigDecimal(0);
				settlementPrice = settlementPrice.subtract(deductionAmount);
			}
			hedgeAmount = settlementPrice.subtract(quotationPrice);

			CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
			detailModel.setReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			detailModel.setHedgeAmount(hedgeAmount);
			detailModel.setDeductionAmount(deductionAmount);
			detailModel.setDeductionReason(detailDeductionReason);
			detailModel.setSettlementPrice(settlementPrice);
			reconciliationDetailService.updateSelective(detailModel);

			List<CustomerReconciliationDetail> details = reconciliationDetailService.selectByReconciliationId(customerReconciliation.getReconciliationId());
			List<CustomerReconciliationDeduction> deductions = selectDeductionsByReconciliationId(customerReconciliation.getReconciliationId());

			BigDecimal totalAmount = details
					.stream()
					.map(CustomerReconciliationDetail::getSettlementPrice)
					.reduce(BigDecimal::add)
					.get();

			BigDecimal redAmount = new BigDecimal(0);
			Optional<BigDecimal> redAmountOptional = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
					.map(CustomerReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add);
			if (redAmountOptional.isPresent()) {
				redAmount = redAmountOptional.get();
			}

			BigDecimal blueAmount = new BigDecimal(0);
			Optional<BigDecimal> blueAmountOptional = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
					.map(CustomerReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add);
			if (blueAmountOptional.isPresent()) {
				blueAmount = blueAmountOptional.get();
			}

			BigDecimal deductionTotalAmount = details
					.stream()
					.map(CustomerReconciliationDetail::getDeductionAmount)
					.reduce(BigDecimal::add)
					.get();

			List<String> deductionNames = deductions
					.stream()
					.map(CustomerReconciliationDeduction::getRefDeductionName)
					.distinct()
					.collect(Collectors.toList());

			String deductionReason = String.join(", ", deductionNames);

			CustomerReconciliation model = new CustomerReconciliation();
			model.setReconciliationId(customerReconciliation.getReconciliationId());
			model.setTotalAmount(totalAmount);
			model.setRedAmount(redAmount);
			model.setBlueAmount(blueAmount);
			model.setDeductionTotalAmount(deductionTotalAmount);
			model.setDeductionReason(deductionReason);
			reconciliationService.updateSelective(model);
		} catch (RuntimeException e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void createWithoutUpdate(CustomerReconciliationDeduction customerReconciliationDeduction) {
		try {
			deductionMapper.insertSelective(customerReconciliationDeduction);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void updateWithoutUpdate(CustomerReconciliationDeduction customerReconciliationDeduction) {
		try {
			deductionMapper.updateByPrimaryKeySelective(customerReconciliationDeduction);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void deleteWithoutUpdate(Long customerReconciliationDeductionId) {
		try {
			deductionMapper.logicalDelete(customerReconciliationDeductionId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(CustomerReconciliationDeduction record) {

		try {
			Environment env = Environment.getEnv();
			CustomerReconciliation customerReconciliation = reconciliationService.queryById(record.getReconciliationId());
			CustomerReconciliationDetail customerReconciliationDetail = reconciliationDetailService.queryById(record.getReconciliationDetailId());
			if (customerReconciliation == null) {
				throw new RuntimeException("对账单(ID)有误，请重新选择。");
			}
			if (customerReconciliationDetail == null) {
				throw new RuntimeException("对账单明细(ID)有误，请重新选择。");
			}

			if (customerReconciliation.getConfirmor() != null) {
				throw new RuntimeException("单据已结算，无法新增扣款项目。");
			}

			record.setCreator(env.getUser().getUserId());
			record.setCorpId(env.getUser().getCorpId());
			record.setCreateDate(new Date());

			StringBuilder sb = checkNewRecord(record);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException(String.format("数据检查失败：%s", sb.toString()));
			}
			deductionMapper.insertSelective(record);

			List<CustomerReconciliationDeduction> deductionList = selectDeductionsByReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			BigDecimal deductionAmount = deductionList.stream().map(CustomerReconciliationDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			List<String> detailDeductionNames = deductionList.stream().map(CustomerReconciliationDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
			String detailDeductionReason = String.join(", ", detailDeductionNames);

			BigDecimal hedgeAmount;
			BigDecimal quotationPrice = customerReconciliationDetail.getQuotationPrice();
			BigDecimal settlementPrice = customerReconciliationDetail.getSettlementPrice();

			if (ObjectUtils.allNotNull(settlementPrice)) {
				settlementPrice = settlementPrice.subtract(record.getDeductionAmount());
			} else {
				settlementPrice = new BigDecimal(0);
				settlementPrice = settlementPrice.subtract(deductionAmount);
			}
			hedgeAmount = settlementPrice.subtract(quotationPrice);

			CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
			detailModel.setReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			detailModel.setHedgeAmount(hedgeAmount);
			detailModel.setDeductionAmount(deductionAmount);
			detailModel.setDeductionReason(detailDeductionReason);
			detailModel.setSettlementPrice(settlementPrice);
			reconciliationDetailService.updateSelective(detailModel);

			List<CustomerReconciliationDetail> details = reconciliationDetailService.selectByReconciliationId(customerReconciliation.getReconciliationId());
			List<CustomerReconciliationDeduction> deductions = selectDeductionsByReconciliationId(customerReconciliation.getReconciliationId());

			BigDecimal totalAmount = details
					.stream()
					.map(CustomerReconciliationDetail::getSettlementPrice)
					.reduce(BigDecimal::add)
					.get();

			BigDecimal redAmount = new BigDecimal(0);
			Optional<BigDecimal> redAmountOptional = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
					.map(CustomerReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add);
			if (redAmountOptional.isPresent()) {
				redAmount = redAmountOptional.get();
			}

			BigDecimal blueAmount = new BigDecimal(0);
			Optional<BigDecimal> blueAmountOptional = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
					.map(CustomerReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add);
			if (blueAmountOptional.isPresent()) {
				blueAmount = blueAmountOptional.get();
			}

			BigDecimal deductionTotalAmount = details
					.stream()
					.map(CustomerReconciliationDetail::getDeductionAmount)
					.reduce(BigDecimal::add)
					.get();

			List<String> deductionNames = deductions
					.stream()
					.map(CustomerReconciliationDeduction::getRefDeductionName)
					.distinct()
					.collect(Collectors.toList());

			String deductionReason = String.join(", ", deductionNames);

			CustomerReconciliation model = new CustomerReconciliation();
			model.setReconciliationId(customerReconciliation.getReconciliationId());
			model.setTotalAmount(totalAmount);
			model.setRedAmount(redAmount);
			model.setBlueAmount(blueAmount);
			model.setDeductionTotalAmount(deductionTotalAmount);
			model.setDeductionReason(deductionReason);
			reconciliationService.updateSelective(model);

		} catch (RuntimeException e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void deleteById(Long id) {

		CustomerReconciliationDeduction deduction = queryById(id);
		StringBuilder sb = new StringBuilder();
		sb.append(CheckUtils.checkLong("NO", "contractId", "对账扣款ID", id, 20));
		if (!StringUtils.isBlank(sb)) {
			throw new RuntimeException(String.format("数据检查失败：%s", sb.toString()));
		}

		CustomerReconciliation customerReconciliation = reconciliationService.queryById(deduction.getReconciliationId());
		CustomerReconciliationDetail customerReconciliationDetail = reconciliationDetailService.queryById(deduction.getReconciliationDetailId());
		if (customerReconciliation == null) {
			throw new RuntimeException("对账单(ID)有误，请重新选择。");
		}
		if (customerReconciliationDetail == null) {
			throw new RuntimeException("对账单明细(ID)有误，请重新选择。");
		}
		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("单据已结算，无法删除扣款项目。");
		}
		deductionMapper.logicalDelete(id);

		List<CustomerReconciliationDeduction> detailDeductions = selectDeductionsByReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
		BigDecimal deductionAmount = detailDeductions.stream().map(CustomerReconciliationDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
		List<String> detailDedcutionNames = detailDeductions.stream().map(CustomerReconciliationDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
		String detailDeductionReason = String.join(", ", detailDedcutionNames);

		BigDecimal hedgeAmount;
		BigDecimal quotationPrice = customerReconciliationDetail.getQuotationPrice();
		BigDecimal settlementPrice = customerReconciliationDetail.getSettlementPrice();

		if (ObjectUtils.allNotNull(settlementPrice)) {
			settlementPrice = settlementPrice.add(deduction.getDeductionAmount());
		} else {
			settlementPrice = new BigDecimal(0);
			settlementPrice = settlementPrice.subtract(deductionAmount);
		}
		hedgeAmount = settlementPrice.subtract(quotationPrice);

		CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
		detailModel.setReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
		detailModel.setHedgeAmount(hedgeAmount);
		detailModel.setDeductionAmount(deductionAmount);
		detailModel.setDeductionReason(detailDeductionReason);
		detailModel.setSettlementPrice(settlementPrice);
		reconciliationDetailService.updateSelective(detailModel);

		List<CustomerReconciliationDetail> details = reconciliationDetailService.selectByReconciliationId(customerReconciliation.getReconciliationId());
		List<CustomerReconciliationDeduction> deductions = selectDeductionsByReconciliationId(customerReconciliation.getReconciliationId());

		BigDecimal totalAmount = details
				.stream()
				.map(CustomerReconciliationDetail::getSettlementPrice)
				.reduce(BigDecimal::add)
				.get();

		BigDecimal redAmount = new BigDecimal(0);
		Optional<BigDecimal> redAmountOptional = details
				.stream()
				.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
				.map(CustomerReconciliationDetail::getHedgeAmount)
				.reduce(BigDecimal::add);
		if (redAmountOptional.isPresent()) {
			redAmount = redAmountOptional.get();
		}

		BigDecimal blueAmount = new BigDecimal(0);
		Optional<BigDecimal> blueAmountOptional = details
				.stream()
				.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
				.map(CustomerReconciliationDetail::getHedgeAmount)
				.reduce(BigDecimal::add);
		if (blueAmountOptional.isPresent()) {
			blueAmount = blueAmountOptional.get();
		}

		BigDecimal deductionTotalAmount = details
				.stream()
				.map(CustomerReconciliationDetail::getDeductionAmount)
				.reduce(BigDecimal::add)
				.get();

		List<String> deductionNames = deductions
				.stream()
				.map(CustomerReconciliationDeduction::getRefDeductionName)
				.distinct()
				.collect(Collectors.toList());

		String deductionReason = String.join(", ", deductionNames);

		CustomerReconciliation model = new CustomerReconciliation();
		model.setReconciliationId(customerReconciliation.getReconciliationId());
		model.setTotalAmount(totalAmount);
		model.setRedAmount(redAmount);
		model.setBlueAmount(blueAmount);
		model.setDeductionTotalAmount(deductionTotalAmount);
		model.setDeductionReason(deductionReason);
		reconciliationService.updateSelective(model);
	}

	@Override
	public List<CustomerReconciliationDeduction> selectDeductionsByReconciliationId(Long reconciliationId) {
		try {
			List<CustomerReconciliationDeduction> deductions = deductionMapper.selectDeductionsByReconciliationId(reconciliationId);
			return deductions;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<CustomerReconciliationDeduction> selectDeductionsByReconciliationDetailId(Long reconciliationDetailId) {
		try {
			List<CustomerReconciliationDeduction> deductions = deductionMapper.selectDeductionsByReconciliationDetailId(reconciliationDetailId);
			return deductions;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
