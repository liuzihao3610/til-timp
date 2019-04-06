package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.enums.CheckStatus;
import com.tilchina.timp.mapper.OutsourcingReconciliationDetailMapper;
import com.tilchina.timp.model.OutsourcingDeduction;
import com.tilchina.timp.model.OutsourcingReconciliation;
import com.tilchina.timp.model.OutsourcingReconciliationDetail;
import com.tilchina.timp.service.OrderService;
import com.tilchina.timp.service.OutsourcingDeductionService;
import com.tilchina.timp.service.OutsourcingReconciliationDetailService;
import com.tilchina.timp.service.OutsourcingReconciliationService;
import com.tilchina.timp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* 外协对账子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class OutsourcingReconciliationDetailServiceImpl extends BaseServiceImpl<OutsourcingReconciliationDetail> implements OutsourcingReconciliationDetailService {

	@Autowired
    private OutsourcingReconciliationDetailMapper detailMapper;

	@Autowired
	private OutsourcingReconciliationService reconciliationService;

	@Autowired
	private OutsourcingDeductionService outsourcingDeductionService;

	@Autowired
	private OrderService orderService;

	@Override
	protected BaseMapper<OutsourcingReconciliationDetail> getMapper() {
		return detailMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(OutsourcingReconciliationDetail outsourcingreconciliationdetail) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkInteger("NO", "checkStatus", "数据检查结果（0:未检查", outsourcingreconciliationdetail.getCheckStatus(), 10));
		s.append(CheckUtils.checkString("YES", "checkResult", "未通过原因", outsourcingreconciliationdetail.getCheckResult(), 50));
		s.append(CheckUtils.checkString("YES", "originalOrderNumber", "原始订单号", outsourcingreconciliationdetail.getOriginalOrderNumber(), 20));
		s.append(CheckUtils.checkString("YES", "outsourcingOrderNumber", "外协订单号", outsourcingreconciliationdetail.getOutsourcingOrderNumber(), 20));
		s.append(CheckUtils.checkString("NO", "carVin", "车架号", outsourcingreconciliationdetail.getCarVin(), 20));
//		s.append(CheckUtils.checkBigDecimal("YES", "freightRate", "运价", outsourcingreconciliationdetail.getFreightRate(), 10, 2));
//		s.append(CheckUtils.checkBigDecimal("YES", "actualFreightRate", "实际运价", outsourcingreconciliationdetail.getActualFreightRate(), 10, 2));
//		s.append(CheckUtils.checkBigDecimal("YES", "hedgeAmount", "对冲金额", outsourcingreconciliationdetail.getHedgeAmount(), 10, 2));
//		s.append(CheckUtils.checkBigDecimal("YES", "deductionAmount", "扣款金额", outsourcingreconciliationdetail.getDeductionAmount(), 10, 2));
		s.append(CheckUtils.checkString("YES", "deductionReason", "扣款原因", outsourcingreconciliationdetail.getDeductionReason(), 200));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", outsourcingreconciliationdetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(OutsourcingReconciliationDetail outsourcingreconciliationdetail) {
        StringBuilder s = checkNewRecord(outsourcingreconciliationdetail);
        s.append(CheckUtils.checkPrimaryKey(outsourcingreconciliationdetail.getReconciliationDetailId()));
		return s;
	}

	@Override
	public List<OutsourcingReconciliationDetail> queryByReconciliationId(Long reconciliationId) {
		try {
			List<OutsourcingReconciliationDetail> details = detailMapper.queryByReconciliationId(reconciliationId);
			return details;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public PageInfo<OutsourcingReconciliationDetail> queryByReconciliationId(Long reconciliationId, int pageNum, int pageSize) {
		try {
			PageHelper.startPage(pageNum, pageSize);
			List<OutsourcingReconciliationDetail> details = detailMapper.queryByReconciliationId(reconciliationId);
			return new PageInfo(details);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void logicalDelete(Long reconciliationDetailId) {
		try {
			OutsourcingReconciliationDetail reconciliationDetail = queryById(reconciliationDetailId);
			OutsourcingReconciliation reconciliation = reconciliationService.queryById(reconciliationDetail.getReconciliationId());

			if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
				throw new RuntimeException("非制单状态的单据无法删除扣款项目，请检查后重试。");
			}
			if (reconciliation.getConfirmor() != null) {
				throw new RuntimeException("当前单据已结算，无法删除扣款项目。");
			}

			if (reconciliationDetail.getCheckStatus() == CheckStatus.Passed.getIndex()) {
				orderService.updateForReconciliation(new ArrayList(){{add(reconciliationDetail.getOutsourcingOrderDetailId());}}, true);
			}

			detailMapper.logicalDelete(reconciliationDetailId);

			List<OutsourcingReconciliationDetail> details = queryByReconciliationId(reconciliation.getReconciliationId());
			List<OutsourcingDeduction> deductions = outsourcingDeductionService.queryByReconciliationId(reconciliation.getReconciliationId());

			int carVinCount = details.stream().filter(detail -> detail.getCarVin() != null).map(OutsourcingReconciliationDetail::getCarVin).collect(Collectors.toList()).size();
			// 过滤未通过检查数据
			details = details.stream().filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex()).collect(Collectors.toList());
			List<Long> passedDetailIds = details
					.stream()
					.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
					.map(OutsourcingReconciliationDetail::getReconciliationDetailId)
					.collect(Collectors.toList());

			BigDecimal outsourcingAmount = details
					.stream()
					.filter(detail -> detail.getFreightRate() != null)
					.map(OutsourcingReconciliationDetail::getFreightRate)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			BigDecimal actualAmount = details
					.stream()
					.filter(detail -> detail.getActualFreightRate() != null)
					.map(OutsourcingReconciliationDetail::getActualFreightRate)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			BigDecimal redAmount = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
					.map(OutsourcingReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			BigDecimal blueAmount = details
					.stream()
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
					.map(OutsourcingReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			BigDecimal deductionAmount = deductions
					.stream()
					.filter(deduction -> passedDetailIds.contains(deduction.getReconciliationDetailId()))
					.map(OutsourcingDeduction::getDeductionAmount)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			String deductionReason = deductions
					.stream()
					.filter(deduction -> passedDetailIds.contains(deduction.getReconciliationDetailId()))
					.map(OutsourcingDeduction::getRefDeductionName)
					.collect(Collectors.joining(","));

			OutsourcingReconciliation model = new OutsourcingReconciliation();
			model.setReconciliationId(reconciliation.getReconciliationId());
			model.setCarVinCount(carVinCount);
			model.setOutsourcingAmount(outsourcingAmount);
			model.setActualAmount(actualAmount);
			model.setRedAmount(redAmount);
			model.setBlueAmount(blueAmount);
			model.setDeductionAmount(deductionAmount);
			model.setDeductionReason(deductionReason);
			reconciliationService.updateSelective(model);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void logicalDeleteByReconciliationId(Long reconciliationId) {
		try {
			detailMapper.logicalDeleteByReconciliationId(reconciliationId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void modifyHedgeAmount(Map<String, Object> params) {

		if (!RegexUtil.validate("regex.amount.hedge", params.get("hedgeAmount").toString())) {
			throw new RuntimeException(String.format("金额中包含非法字符，请检查后重试。", params.get("hedgeAmount").toString()));
		}

		Long reconciliationDetailId = null;
		BigDecimal newHedgeAmount = NumberUtils.createBigDecimal(params.get("hedgeAmount").toString());

		if (NumberUtils.isCreatable(params.get("reconciliationDetailId").toString())) {
			reconciliationDetailId = NumberUtils.createLong(params.get("reconciliationDetailId").toString());
		}

		OutsourcingReconciliationDetail reconciliationDetail = queryById(reconciliationDetailId);
		OutsourcingReconciliation reconciliation = reconciliationService.queryById(reconciliationDetail.getReconciliationId());
		if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态下单据无法修改对冲金额，请检查后重试。");
		}
		if (reconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法修改对冲金额。");
		}
		if (reconciliationDetail.getCheckStatus() != CheckStatus.Passed.getIndex()) {
			throw new RuntimeException("当前明细未通过检查，无法修改对冲金额。");
		}

		BigDecimal oldHedgeAmount;
		BigDecimal actualFreightRate;
		BigDecimal amountDifference;

		oldHedgeAmount    = Optional.ofNullable(reconciliationDetail.getHedgeAmount()).orElseGet(() -> new BigDecimal(0));
		actualFreightRate = Optional.ofNullable(reconciliationDetail.getActualFreightRate()).orElseGet(() -> new BigDecimal(0));
		amountDifference  = Optional.ofNullable(newHedgeAmount.subtract(oldHedgeAmount)).orElseGet(() -> new BigDecimal(0));
		actualFreightRate = Optional.ofNullable(actualFreightRate.add(amountDifference)).orElseGet(() -> new BigDecimal(0));

		OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
		detailModel.setReconciliationDetailId(reconciliationDetailId);
		detailModel.setHedgeAmount(newHedgeAmount);
		detailModel.setActualFreightRate(actualFreightRate);
		updateSelective(detailModel);

		List<OutsourcingReconciliationDetail> details = queryByReconciliationId(reconciliation.getReconciliationId());

		BigDecimal outsourcingAmount = details
				.stream()
				.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
				.filter(detail -> detail.getFreightRate() != null)
				.map(OutsourcingReconciliationDetail::getFreightRate)
				.reduce(BigDecimal::add)
				.orElse(new BigDecimal(0));

		BigDecimal actualAmount = details
				.stream()
				.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
				.filter(detail -> detail.getActualFreightRate() != null)
				.map(OutsourcingReconciliationDetail::getActualFreightRate)
				.reduce(BigDecimal::add)
				.orElse(new BigDecimal(0));

		BigDecimal redAmount = details
				.stream()
				.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
				.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
				.map(OutsourcingReconciliationDetail::getHedgeAmount)
				.reduce(BigDecimal::add)
				.orElse(new BigDecimal(0));

		BigDecimal blueAmount = details
				.stream()
				.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
				.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
				.map(OutsourcingReconciliationDetail::getHedgeAmount)
				.reduce(BigDecimal::add)
				.orElse(new BigDecimal(0));

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationId(reconciliation.getReconciliationId());
		model.setOutsourcingAmount(outsourcingAmount);
		model.setActualAmount(actualAmount);
		model.setRedAmount(redAmount);
		model.setBlueAmount(blueAmount);
		reconciliationService.updateSelective(model);
	}
}
