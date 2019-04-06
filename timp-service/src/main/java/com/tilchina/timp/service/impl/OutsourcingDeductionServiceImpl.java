package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.enums.CheckStatus;
import com.tilchina.timp.mapper.OutsourcingDeductionMapper;
import com.tilchina.timp.model.OutsourcingDeduction;
import com.tilchina.timp.model.OutsourcingReconciliation;
import com.tilchina.timp.model.OutsourcingReconciliationDetail;
import com.tilchina.timp.service.DeductionService;
import com.tilchina.timp.service.OutsourcingDeductionService;
import com.tilchina.timp.service.OutsourcingReconciliationDetailService;
import com.tilchina.timp.service.OutsourcingReconciliationService;
import com.tilchina.timp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* 外协对账扣款表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class OutsourcingDeductionServiceImpl extends BaseServiceImpl<OutsourcingDeduction> implements OutsourcingDeductionService {

	@Autowired
    private OutsourcingDeductionMapper deductionMapper;

	@Autowired
	private OutsourcingReconciliationService reconciliationService;

	@Autowired
	private OutsourcingReconciliationDetailService reconciliationDetailService;

	@Autowired
	private DeductionService deductionService;
	
	@Override
	protected BaseMapper<OutsourcingDeduction> getMapper() {
		return deductionMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(OutsourcingDeduction outsourcingdeduction) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkBigDecimal("NO", "deductionAmount", "扣款金额", outsourcingdeduction.getDeductionAmount(), 10, 2));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", outsourcingdeduction.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", outsourcingdeduction.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(OutsourcingDeduction outsourcingdeduction) {
        StringBuilder s = checkNewRecord(outsourcingdeduction);
        s.append(CheckUtils.checkPrimaryKey(outsourcingdeduction.getReconciliationDeductionId()));
		return s;
	}

	@Override
	public List<OutsourcingDeduction> queryByReconciliationId(Long reconciliationId) {

		try {
			List<OutsourcingDeduction> outsourcingDeductions = deductionMapper.queryByReconciliationId(reconciliationId);
			return outsourcingDeductions;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<OutsourcingDeduction> queryByReconciliationDetailId(Long reconciliationDetailId) {

		try {
			List<OutsourcingDeduction> outsourcingDeductions = deductionMapper.queryByReconciliationDetailId(reconciliationDetailId);
			return outsourcingDeductions;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void add(OutsourcingDeduction deduction) {

		Long deductionId            = deduction.getDeductionId();
		Long reconciliationId       = deduction.getReconciliationId();
		Long reconciliationDetailId = deduction.getReconciliationDetailId();

		Optional.ofNullable(deductionService.queryById(deductionId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", deductionId)));

		OutsourcingReconciliation reconciliation             = Optional
				.ofNullable(reconciliationService.queryById(reconciliationId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationId)));

		OutsourcingReconciliationDetail reconciliationDetail = Optional
				.ofNullable(reconciliationDetailService.queryById(reconciliationDetailId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationDetailId)));

		if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态的单据无法新增扣款项目，请检查后重试。");
		}
		if (reconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法新增扣款项目。");
		}

		Environment env = Environment.getEnv();
		deduction.setCreator(env.getUser().getUserId());
		deduction.setCreateDate(new Date());
		deduction.setCorpId(env.getUser().getCorpId());
		deduction.setFlag(0);
		deductionMapper.insertSelective(deduction);

		// 更新子表关联信息
		String detailDeductionReason     = "";
		BigDecimal hedgeAmount           = new BigDecimal(0);
		BigDecimal detailDeductionAmount = new BigDecimal(0);
		BigDecimal deductionAmount       = deduction.getDeductionAmount();              // 扣款金额
		BigDecimal freightRate           = reconciliationDetail.getFreightRate();       // 运价
		BigDecimal actualFreightRate     = reconciliationDetail.getActualFreightRate(); // 实际运价

		actualFreightRate = actualFreightRate.subtract(deductionAmount);    // 实际运价
		hedgeAmount = actualFreightRate.subtract(freightRate);              // 对冲金额

		List<OutsourcingDeduction> detailDeductions = queryByReconciliationDetailId(reconciliationDetailId);
		if (CollectionUtils.isNotEmpty(detailDeductions)) {
			// 汇总一条子表记录对应所有扣款记录的扣款总额
			detailDeductionAmount = detailDeductions.stream().map(OutsourcingDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			// 汇总一条子表记录对应所有扣款记录的扣款项目
			detailDeductionReason = detailDeductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.joining(","));
		}

		OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
		detailModel.setReconciliationDetailId(reconciliationDetailId);
		detailModel.setActualFreightRate(actualFreightRate);
		detailModel.setHedgeAmount(hedgeAmount);
		detailModel.setDeductionAmount(detailDeductionAmount);
		detailModel.setDeductionReason(detailDeductionReason);
		reconciliationDetailService.updateSelective(detailModel);


		// 更新主表关联信息
		String totalDeductionReason     = "";
		BigDecimal actualAmount         = new BigDecimal(0);
		BigDecimal redAmount            = new BigDecimal(0);
		BigDecimal blueAmount           = new BigDecimal(0);
		BigDecimal totalDeductionAmount = new BigDecimal(0);

		// 汇总一条主表记录对应所有子表的实际运价金额
		List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(details)) {
			actualAmount = details
					.stream()
					.filter(detail -> detail.getActualFreightRate() != null)
					.map(OutsourcingReconciliationDetail::getActualFreightRate)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));
			redAmount = details
					.stream()
					.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) < 0)
					.map(OutsourcingReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));

			blueAmount = details
					.stream()
					.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
					.filter(detail -> detail.getHedgeAmount().compareTo(BigDecimal.ZERO) > 0)
					.map(OutsourcingReconciliationDetail::getHedgeAmount)
					.reduce(BigDecimal::add)
					.orElse(new BigDecimal(0));
		}

		List<OutsourcingDeduction> deductions = queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(deductions)) {
			// 汇总一条主表记录对应所有子表的的扣款总额
			totalDeductionAmount = deductions.stream().map(OutsourcingDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			// 汇总一条主表记录对应所有子表的的所有扣款项目
			totalDeductionReason = deductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.joining(","));
		}

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationId(reconciliationId);
		model.setActualAmount(actualAmount);
		model.setRedAmount(redAmount);
		model.setBlueAmount(blueAmount);
		model.setDeductionAmount(totalDeductionAmount);
		model.setDeductionReason(totalDeductionReason);
		reconciliationService.updateSelective(model);
	}

	@Transactional
	@Override
	public void updateSelective(OutsourcingDeduction newDeduction) {

		BigDecimal deductionAmount     = newDeduction.getDeductionAmount();
		Long reconciliationDeductionId = newDeduction.getReconciliationDeductionId();
		Long reconciliationId          = newDeduction.getReconciliationId();
		Long reconciliationDetailId    = newDeduction.getReconciliationDetailId();

		if (!RegexUtil.validate("regex.amount.deduction", deductionAmount.toString())) {
			throw new RuntimeException(String.format("金额中包含非法字符或金额小于零，请检查后重试。", deductionAmount.toString()));
		}

		OutsourcingDeduction oldDeduction = Optional
				.ofNullable(queryById(reconciliationDeductionId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationDeductionId)));

		OutsourcingReconciliation reconciliation             = Optional
				.ofNullable(reconciliationService.queryById(reconciliationId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationId)));

		OutsourcingReconciliationDetail reconciliationDetail = Optional
				.ofNullable(reconciliationDetailService.queryById(reconciliationDetailId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationDetailId)));

		if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态的单据无法新增扣款项目，请检查后重试。");
		}
		if (reconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法新增扣款项目。");
		}

		deductionMapper.updateByPrimaryKeySelective(newDeduction);

		// 更新子表关联信息
		String detailDeductionReason     = "";
		BigDecimal hedgeAmount           = new BigDecimal(0);
		BigDecimal detailDeductionAmount = new BigDecimal(0);
		BigDecimal freightRate           = reconciliationDetail.getFreightRate();
		BigDecimal actualFreightRate     = reconciliationDetail.getActualFreightRate();

		List<OutsourcingDeduction> detailDeductions = queryByReconciliationDetailId(reconciliationDetailId);
		if (CollectionUtils.isNotEmpty(detailDeductions)) {
			// 汇总一条子表记录对应所有扣款记录的扣款总额
			detailDeductionAmount = detailDeductions.stream().map(OutsourcingDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			// 汇总一条子表记录对应所有扣款记录的扣款项目
			detailDeductionReason = detailDeductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.joining(","));
		}

		BigDecimal oldDeductionDeductionAmount = oldDeduction.getDeductionAmount();
		BigDecimal newDeductionDeductionAmount = newDeduction.getDeductionAmount();
		BigDecimal updateDeductionAmount       = oldDeductionDeductionAmount.subtract(newDeductionDeductionAmount);

		actualFreightRate = actualFreightRate.subtract(updateDeductionAmount);
		hedgeAmount       = actualFreightRate.subtract(freightRate);

		OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
		detailModel.setReconciliationDetailId(reconciliationDetailId);
		detailModel.setActualFreightRate(actualFreightRate);
		detailModel.setHedgeAmount(hedgeAmount);
		detailModel.setDeductionAmount(detailDeductionAmount);
		detailModel.setDeductionReason(detailDeductionReason);
		reconciliationDetailService.updateSelective(detailModel);


		// 更新主表关联信息
		String totalDeductionReason     = "";
		BigDecimal actualAmount         = new BigDecimal(0);
		BigDecimal totalDeductionAmount = new BigDecimal(0);

		// 汇总一条主表记录对应所有子表的实际运价金额
		List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(details)) {
			actualAmount = details
					.stream()
					.filter(detail -> detail.getActualFreightRate() != null)
					.map(OutsourcingReconciliationDetail::getActualFreightRate)
					.reduce(BigDecimal::add)
					.orElse(BigDecimal.ZERO);
		}

		List<OutsourcingDeduction> deductions = queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(deductions)) {
			// 汇总一条主表记录对应所有子表的的扣款总额
			totalDeductionAmount = deductions
					.stream()
					.filter(deduction -> deduction.getDeductionAmount() != null)
					.map(OutsourcingDeduction::getDeductionAmount)
					.reduce(BigDecimal::add)
					.orElse(BigDecimal.ZERO);
			// 汇总一条主表记录对应所有子表的的所有扣款项目
			totalDeductionReason = deductions
					.stream()
					.filter(deduction -> StringUtils.isNotBlank(deduction.getRefDeductionName()))
					.map(OutsourcingDeduction::getRefDeductionName)
					.distinct()
					.collect(Collectors.joining(","));
		}

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationId(reconciliationId);
		model.setActualAmount(actualAmount);
		model.setDeductionAmount(totalDeductionAmount);
		model.setDeductionReason(totalDeductionReason);
		reconciliationService.updateSelective(model);
	}

	@Override
	public void deleteById(Long reconciliationDeductionId) {

		OutsourcingDeduction deduction = Optional
				.ofNullable(queryById(reconciliationDeductionId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationDeductionId)));

		Long reconciliationId       = deduction.getReconciliationId();
		Long reconciliationDetailId = deduction.getReconciliationDetailId();

		OutsourcingReconciliation reconciliation             = Optional
				.ofNullable(reconciliationService.queryById(reconciliationId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationId)));

		OutsourcingReconciliationDetail reconciliationDetail = Optional
				.ofNullable(reconciliationDetailService.queryById(reconciliationDetailId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s的对应记录，请检查后重试。", reconciliationDetailId)));

		if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态的单据无法新增扣款项目，请检查后重试。");
		}
		if (reconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法新增扣款项目。");
		}

		deductionMapper.deleteByPrimaryKey(reconciliationDeductionId);

		// 更新子表关联信息
		String detailDeductionReason     = "";
		BigDecimal hedgeAmount           = new BigDecimal(0);
		BigDecimal detailDeductionAmount = new BigDecimal(0);
		BigDecimal deductionAmount       = deduction.getDeductionAmount();              // 扣款金额
		BigDecimal freightRate           = reconciliationDetail.getFreightRate();       // 运价
		BigDecimal actualFreightRate     = reconciliationDetail.getActualFreightRate(); // 实际运价

		actualFreightRate = actualFreightRate.add(deductionAmount);    // 实际运价
		hedgeAmount = actualFreightRate.subtract(freightRate);         // 对冲金额

		List<OutsourcingDeduction> detailDeductions = queryByReconciliationDetailId(reconciliationDetailId);
		if (CollectionUtils.isNotEmpty(detailDeductions)) {
			// 汇总一条子表记录对应所有扣款记录的扣款总额
			detailDeductionAmount = detailDeductions.stream().map(OutsourcingDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			// 汇总一条子表记录对应所有扣款记录的扣款项目
			detailDeductionReason = detailDeductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.joining(","));
		}

		OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
		detailModel.setReconciliationDetailId(reconciliationDetailId);
		detailModel.setActualFreightRate(actualFreightRate);
		detailModel.setHedgeAmount(hedgeAmount);
		detailModel.setDeductionAmount(detailDeductionAmount);
		detailModel.setDeductionReason(detailDeductionReason);
		reconciliationDetailService.updateSelective(detailModel);


		// 更新主表关联信息
		String totalDeductionReason     = "";
		BigDecimal actualAmount         = new BigDecimal(0);
		BigDecimal outsourcingAmount     = new BigDecimal(0);
		BigDecimal totalDeductionAmount = new BigDecimal(0);

		// 汇总一条主表记录对应所有子表的实际运价金额
		List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
		details = details.stream().filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(details)) {
			outsourcingAmount = details.stream().map(OutsourcingReconciliationDetail::getFreightRate).reduce(BigDecimal::add).get();
			actualAmount      = details.stream().map(OutsourcingReconciliationDetail::getActualFreightRate).reduce(BigDecimal::add).get();
		}

		List<OutsourcingDeduction> deductions = queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(deductions)) {
			// 汇总一条主表记录对应所有子表的的扣款总额
			totalDeductionAmount = deductions.stream().map(OutsourcingDeduction::getDeductionAmount).reduce(BigDecimal::add).get();
			// 汇总一条主表记录对应所有子表的的所有扣款项目
			totalDeductionReason = deductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.joining(","));
		}

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationId(reconciliationId);
		model.setOutsourcingAmount(outsourcingAmount);
		model.setActualAmount(actualAmount);
		model.setDeductionAmount(totalDeductionAmount);
		model.setDeductionReason(totalDeductionReason);
		reconciliationService.updateSelective(model);
	}
}
