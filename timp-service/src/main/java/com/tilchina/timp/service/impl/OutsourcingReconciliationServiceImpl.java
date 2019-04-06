package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.*;
import com.tilchina.timp.mapper.OutsourcingReconciliationMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
* 外协对账主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class OutsourcingReconciliationServiceImpl extends BaseServiceImpl<OutsourcingReconciliation> implements OutsourcingReconciliationService {

	@Autowired
    private OutsourcingReconciliationMapper outsourcingReconciliationMapper;

	@Autowired
	private BrandService carBrandService;

	@Autowired
	private CarTypeService carTypeService;

	@Autowired
	private CarService carModelService;

	@Autowired
	private CityService cityService;

	@Autowired
	private UnitService unitService;

	@Autowired
	private OutsourcingReconciliationDetailService reconciliationDetailService;

	@Autowired
	private OutsourcingDeductionService outsourcingDeductionService;

	@Autowired
	private QuotationDetailService quotationDetailService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private DeductionService deductionService;

	@Override
	protected BaseMapper<OutsourcingReconciliation> getMapper() {
		return outsourcingReconciliationMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(OutsourcingReconciliation outsourcingreconciliation) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "reconciliationNumber", "对账单号", outsourcingreconciliation.getReconciliationNumber(), 20));
        s.append(CheckUtils.checkInteger("YES", "carVinCount", "车架号数量", outsourcingreconciliation.getCarVinCount(), 10));
		// s.append(CheckUtils.checkBigDecimal("YES", "outsourcingAmount", "外协对账金额", outsourcingreconciliation.getOutsourcingAmount(), 10, 2));
		// s.append(CheckUtils.checkBigDecimal("YES", "actualAmount", "实际金额", outsourcingreconciliation.getActualAmount(), 10, 2));
		// s.append(CheckUtils.checkBigDecimal("YES", "deductionAmount", "累计扣款", outsourcingreconciliation.getDeductionAmount(), 10, 2));
        s.append(CheckUtils.checkString("YES", "deductionReason", "扣款原因", outsourcingreconciliation.getDeductionReason(), 200));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "单据状态", outsourcingreconciliation.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", outsourcingreconciliation.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", outsourcingreconciliation.getCheckDate()));
        s.append(CheckUtils.checkDate("YES", "confirmDate", "确认时间", outsourcingreconciliation.getConfirmDate()));
        s.append(CheckUtils.checkDate("YES", "settlementDate", "确认结算时间", outsourcingreconciliation.getSettlementDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", outsourcingreconciliation.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(OutsourcingReconciliation outsourcingreconciliation) {
        StringBuilder s = checkNewRecord(outsourcingreconciliation);
        s.append(CheckUtils.checkPrimaryKey(outsourcingreconciliation.getReconciliationId()));
		return s;
	}

	@Transactional
	@Override
	public void importReconciliation(Long vendorCorpId, MultipartFile file) throws Exception {

		String filePath;
		try {
			filePath = FileUtil.uploadExcel(file);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		Environment env = Environment.getEnv();

		Workbook workbook = ExcelUtil.getWorkbook(filePath);
		List<OutsourcingReconciliationDetail> details = parseExcelForReconciliation(workbook);

		int carVinCount = details.stream().map(OutsourcingReconciliationDetail::getCarVin).collect(Collectors.toList()).size();
		BigDecimal outsourcingAmount = details.stream().map(OutsourcingReconciliationDetail::getFreightRate).reduce(BigDecimal::add).get();

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationNumber(String.format("OR%s", DateUtil.dateToStringCode(new Date())));
		model.setVendorCorpId(vendorCorpId);
		model.setCarVinCount(carVinCount);
		model.setOutsourcingAmount(outsourcingAmount);
		model.setCreator(env.getUser().getUserId());
		model.setCreateDate(new Date());
		model.setCorpId(env.getUser().getCorpId());
		model.setBillStatus(BillStatus.Drafted.getIndex());
		model.setFlag(FlagStatus.No.getIndex());
		add(model);

		// 设置主表ID
		details.forEach(detail -> detail.setReconciliationId(model.getReconciliationId()));
		reconciliationDetailService.add(details);
	}

	@Transactional
	@Override
	public void checking(Long reconciliationId) {

		OutsourcingReconciliation reconciliation = Optional
				.ofNullable(queryById(reconciliationId))
				.orElseThrow(() -> new RuntimeException(String.format("未查询到ID: %s对应的记录, 请检查后重试。")));

		if (reconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态的单据无法对账，请检查后重试。");
		}
		if (reconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
			throw new RuntimeException("当前单据已结算，无法对账。");
		}

		List<OutsourcingReconciliationDetail> updateDetails = new ArrayList<>();
		List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliation.getReconciliationId());
		details = details.stream().filter(detail -> detail.getCheckStatus() == CheckStatus.Unchecked.getIndex()).collect(Collectors.toList());
		if (details.size() == 0) {
			throw new RuntimeException("当前单据中无可对账的车架号，请检查后重试。");
		}

		for (OutsourcingReconciliationDetail detail : details) {

			Map<String, Object> params = new HashMap<>();
			params.put("carVin", detail.getCarVin());
			params.put("vendorCorpId", reconciliation.getVendorCorpId());
			params.put("orderType", OrderType.Out.getIndex());

			OrderDetail outsourcingOrderDetail = orderDetailService.queryForOutsourcingReconciliation(params);
			if (outsourcingOrderDetail == null) {

				String vendorCorpName = Optional
						.ofNullable(corpService.queryById(reconciliation.getVendorCorpId()).getCorpName())
						.orElse("未查询到该承运商");
				String checkResult = String.format("未查询到车架号: %s与承运商: %s对应的订单记录", detail.getCarVin(), vendorCorpName);

				OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
				detailModel.setCheckStatus(CheckStatus.Unpassed.getIndex());
				detailModel.setCheckResult(checkResult);
				updateDetails.add(detailModel);
				continue;
			}

			if (outsourcingOrderDetail.getSettlement() != null && outsourcingOrderDetail.getSettlement() == SettlementStatus.YES.getIndex()) {

				String checkResult = String.format("车架号: %s已结算", detail.getCarVin());

				OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
				detailModel.setCheckStatus(CheckStatus.Unpassed.getIndex());
				detailModel.setCheckResult(checkResult);
				updateDetails.add(detailModel);
				continue;
			}

			if (outsourcingOrderDetail.getReconciliation() != null && outsourcingOrderDetail.getReconciliation() == ReconciliationStatus.Yes.getIndex()) {

				String checkResult = String.format("车架号: %s已对账", detail.getCarVin());

				OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
				detailModel.setCheckStatus(CheckStatus.Unpassed.getIndex());
				detailModel.setCheckResult(checkResult);
				updateDetails.add(detailModel);
				continue;
			}

			params = new HashMap<>();
			params.put("carVin", detail.getCarVin());
			Order originOrder = orderService.getOriginOrder(params);
			if (originOrder == null) {

				String vendorCorpName = Optional
						.ofNullable(corpService.queryById(reconciliation.getVendorCorpId()).getCorpName())
						.orElse("未查询到该承运商");
				String checkResult = String.format("未查询到车架号: %s与承运商: %s对应的订单记录", detail.getCarVin(), vendorCorpName);

				OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
				detailModel.setCheckStatus(CheckStatus.Unpassed.getIndex());
				detailModel.setCheckResult(checkResult);
				updateDetails.add(detailModel);
				continue;
			}

			params = new HashMap<>();
			params.put("orderId", originOrder.getOrderId());
			params.put("carVin", detail.getCarVin());
			OrderDetail originalOrderDetail = orderDetailService.queryForOutsourcingReconciliation(params);

			Order originalOrder    = orderService.queryById(originOrder.getOrderId());
			Order outsourcingOrder = orderService.queryById(outsourcingOrderDetail.getOrderId());

			Date orderDate    = outsourcingOrder.getOrderDate();                // 订单日期
			Long customerId   = outsourcingOrder.getCorpCustomerId();           // 客户
			Long vendorCorpId = outsourcingOrder.getCorpCarrierId();            // 承运商(外协）
			Long recvUnitId   = outsourcingOrderDetail.getReceiveUnitId();      // 收货单位
			Long sendUnitId   = outsourcingOrderDetail.getSendUnitId();         // 发货单位
			Long recvCityId   = unitService.queryById(recvUnitId).getCityId();  // 收货城市
			Long sendCityId   = unitService.queryById(sendUnitId).getCityId();  // 发货城市
			Long carBrandId   = outsourcingOrderDetail.getBrandId();            // 车辆品牌
			Long carTypeId    = outsourcingOrderDetail.getCarTypeId();          // 车辆类别
			Long carModelId   = outsourcingOrderDetail.getCarId();              // 车辆型号

//			QuotationVO quotationVO = new QuotationVO(){{
//				setOrderDate(orderDate);
//				setCustomerId(customerId);
//				setVendorCorpId(vendorCorpId);
//				setRecvCityId(recvCityId);
//				setSendCityId(sendCityId);
//				setCarBrandId(carBrandId);
//				setCarTypeId(carTypeId);
//				setCarModelId(carModelId);
//			}};
//
//			QuotationDetail quotationDetail = quotationDetailService.queryByQuotationVO(quotationVO);
//			if (quotationDetail == null) {
//
//				String customerCorpName = Optional.ofNullable(corpService.queryById(customerId)).orElseGet(Corp::new).getCorpName();
//				String vendorCorpName   = Optional.ofNullable(corpService.queryById(vendorCorpId)).orElseGet(Corp::new).getCorpName();
//				String sendCityName     = Optional.ofNullable(cityService.queryById(sendCityId)).orElseGet(City::new).getCityName();
//				String recvCityName     = Optional.ofNullable(cityService.queryById(recvCityId)).orElseGet(City::new).getCityName();
//				String carBrandName     = Optional.ofNullable(carBrandService.queryById(carBrandId)).orElseGet(Brand::new).getBrandName();
//				String carTypeName      = Optional.ofNullable(carTypeService.queryById(carTypeId)).orElseGet(CarType::new).getCarTypeName();
//				String carModelName     = Optional.ofNullable(carModelService.queryById(carModelId)).orElseGet(Car::new).getCarName();
//
//				String checkResult = String.format("未查询到从%s到%s，品牌：%s，类别：%s，型号：%s，%s给%s的报价，请维护报价档案后重试。",
//						sendCityName,
//						recvCityName,
//						carBrandName,
//						carTypeName,
//						carModelName,
//						customerCorpName,
//						vendorCorpName);
//
//				OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
//				detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
//				detailModel.setCheckStatus(CheckStatus.Unpassed.getIndex());
//				detailModel.setCheckResult(checkResult);
//				updateDetails.add(detailModel);
//				continue;
//			}

			BigDecimal freightRate       = detail.getFreightRate();                         // 运价（外协给东泽的报价）
			// BigDecimal actualFreightRate = quotationDetail.getTotalPrice();              // 实际运价（东泽给外协的报价）
			BigDecimal actualFreightRate = outsourcingOrderDetail.getCustomerQuotedPrice(); // 实际运价（东泽给外协的报价）
			BigDecimal hedgeAmount       = actualFreightRate.subtract(freightRate);         // 对冲金额

			OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
			detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
			detailModel.setCheckStatus(CheckStatus.Passed.getIndex());
			detailModel.setOriginalOrderDetailId(originalOrderDetail.getOrderDetailId());
			detailModel.setOriginalOrderNumber(originalOrder.getOrderCode());
			detailModel.setOutsourcingOrderDetailId(outsourcingOrderDetail.getOrderDetailId());
			detailModel.setOutsourcingOrderNumber(outsourcingOrder.getOrderCode());
			detailModel.setSendUnitId(sendUnitId);
			detailModel.setRecvUnitId(recvUnitId);
			detailModel.setSendCityId(sendCityId);
			detailModel.setRecvCityId(recvCityId);
			detailModel.setCarBrandId(carBrandId);
			detailModel.setCarTypeId(carTypeId);
			detailModel.setCarModelId(carModelId);
			detailModel.setFreightRate(freightRate);
			detailModel.setActualFreightRate(actualFreightRate);
			detailModel.setHedgeAmount(hedgeAmount);
			// detailModel.setQuotationId(quotationDetail.getQuotationId());
			detailModel.setQuotationId(outsourcingOrderDetail.getQuotedPriceId());
			updateDetails.add(detailModel);
		}


		// 更新子表记录
		updateDetails.forEach(updateDetail -> reconciliationDetailService.updateSelective(updateDetail));
		updateDetails = updateDetails.stream().filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex()).collect(Collectors.toList());

		BigDecimal outsourcingAmount = updateDetails
				.stream()
				.filter(detail -> detail.getFreightRate() != null)
				.map(OutsourcingReconciliationDetail::getFreightRate)
				.filter(Objects::nonNull)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
		// 实际金额
		BigDecimal actualAmount = updateDetails
				.stream()
				.map(OutsourcingReconciliationDetail::getActualFreightRate)
				.filter(Objects::nonNull)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
		// 红字总额
		BigDecimal redAmount = updateDetails
				.stream()
				.map(OutsourcingReconciliationDetail::getHedgeAmount)
				.filter(Objects::nonNull)
				.filter(hedgeAmount -> hedgeAmount.compareTo(BigDecimal.ZERO) < 0)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
		// 蓝字总额
		BigDecimal blueAmount = updateDetails
				.stream()
				.map(OutsourcingReconciliationDetail::getHedgeAmount)
				.filter(Objects::nonNull)
				.filter(hedgeAmount -> hedgeAmount.compareTo(BigDecimal.ZERO) > 0)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);

		OutsourcingReconciliation model = new OutsourcingReconciliation();
		model.setReconciliationId(reconciliation.getReconciliationId());
		model.setOutsourcingAmount(outsourcingAmount);
		model.setActualAmount(actualAmount);
		model.setRedAmount(redAmount);
		model.setBlueAmount(blueAmount);
		updateSelective(model);

		// 更新订单中车架号对账结算状态
		List<Long> originalOrderDetailIds    = updateDetails.stream().map(OutsourcingReconciliationDetail::getOriginalOrderDetailId).distinct().collect(Collectors.toList());
		List<Long> outsourcingOrderDetailIds = updateDetails.stream().map(OutsourcingReconciliationDetail::getOutsourcingOrderDetailId).distinct().collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(originalOrderDetailIds)) {
			orderService.updateForReconciliation(originalOrderDetailIds, false);
		}
		if (CollectionUtils.isNotEmpty(outsourcingOrderDetailIds)) {
			orderService.updateForReconciliation(outsourcingOrderDetailIds, false);
		}
	}

	@Transactional
	@Override
	public void logicalDelete(Long reconciliationId) {
		try {
			OutsourcingReconciliation outsourcingReconciliation = queryById(reconciliationId);
			if (outsourcingReconciliation.getBillStatus() == BillStatus.Audited.getIndex()) {
				throw new RuntimeException("当前单据已审核，无法进行删除操作。");
			}
			if (outsourcingReconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
				throw new RuntimeException("当前单据已结算，无法进行删除操作。");
			}

			List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
			if (CollectionUtils.isNotEmpty(details)) {
				List<Long> outsourcingOrderDetailIds = details.stream()
						.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
						.map(OutsourcingReconciliationDetail::getOutsourcingOrderDetailId)
						.distinct()
						.collect(Collectors.toList());
				orderService.updateForReconciliation(outsourcingOrderDetailIds, true);
			}

			outsourcingReconciliationMapper.logicalDelete(reconciliationId);
			reconciliationDetailService.logicalDeleteByReconciliationId(reconciliationId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void setDocumentsChecked(Long reconciliationId) {
		try {
			Environment env = Environment.getEnv();
			OutsourcingReconciliation outsourcingReconciliation = queryById(reconciliationId);

			if (outsourcingReconciliation.getBillStatus() == BillStatus.Audited.getIndex()) {
				throw new RuntimeException("当前单据已审核，无法进行审核操作。");
			}
			if (outsourcingReconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
				throw new RuntimeException("当前单据已结算，无法进行审核操作。");
			}

			OutsourcingReconciliation model = new OutsourcingReconciliation();
			model.setReconciliationId(outsourcingReconciliation.getReconciliationId());
			model.setChecker(env.getUser().getUserId());
			model.setCheckDate(new Date());
			model.setBillStatus(BillStatus.Audited.getIndex());
			updateDocumentsCheckState(model);

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void setDocumentsUnchecked(Long reconciliationId) {
		try {
			OutsourcingReconciliation outsourcingReconciliation = queryById(reconciliationId);

			if (outsourcingReconciliation.getBillStatus() == BillStatus.Drafted.getIndex()) {
				throw new RuntimeException("当前单据未审核，无法进行取消审核操作。");
			}
			if (outsourcingReconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
				throw new RuntimeException("当前单据已结算，无法进行取消审核操作。");
			}

			OutsourcingReconciliation model = new OutsourcingReconciliation();
			model.setReconciliationId(outsourcingReconciliation.getReconciliationId());
			model.setChecker(null);
			model.setCheckDate(null);
			model.setBillStatus(BillStatus.Drafted.getIndex());
			updateDocumentsCheckState(model);

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Workbook exportFeedback(Long reconciliationId) {

		OutsourcingReconciliation outsourcingReconciliation = queryById(reconciliationId);
		if (outsourcingReconciliation.getBillStatus() == BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("当前单据未审核，无法进行导出客户反馈操作。");
		}

		List<String> excelTitles = new ArrayList(){{
			add("车架号");
			add("收货城市");
			add("发货城市");
			add("实际运价");
		}};
		List<OutsourcingDeduction> deductions = outsourcingDeductionService.queryByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(deductions)) {
			List<String> deductionTitles = deductions.stream().map(OutsourcingDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
			excelTitles.addAll(deductionTitles);
		}

		int colCount = excelTitles.size();
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		Map<Long, Integer> position = new HashMap<>();
		// 设置EXCEL文件标题
		Row headRow = sheet.createRow(0);
		for (int i = 0; i < colCount; i++) {
			Cell cell = headRow.createCell(i);
			cell.setCellValue(excelTitles.get(i));
			if (deductionService.exists(excelTitles.get(i))) {
				Long deductionId = deductionService.queryByName(excelTitles.get(i));
				// 设置扣款项目ID与单元格位置的对应关系
				position.put(deductionId, cell.getColumnIndex());
			}
		}

		List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
		// 过滤未通过检查的数据
		details = details
				.stream()
				.filter(detail -> detail.getCheckStatus() == CheckStatus.Passed.getIndex())
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(details)) {

			int i = 1;
			for (OutsourcingReconciliationDetail detail : details) {
				String carVin                = detail.getCarVin();
				String recvCityName          = Optional.ofNullable(cityService.queryById(detail.getRecvCityId()).getCityName()).orElse("");
				String sendCityName          = Optional.ofNullable(cityService.queryById(detail.getSendCityId()).getCityName()).orElse("");
				BigDecimal actualFreightRate = detail.getActualFreightRate();

				Row row = sheet.createRow(i++);

				Cell[] cells = new Cell[colCount];
				for (int j = 0; j < colCount; j++) {
					cells[j] = row.createCell(j);
				}

				cells[0].setCellValue(carVin);
				cells[1].setCellValue(recvCityName);
				cells[2].setCellValue(sendCityName);
				cells[3].setCellValue(actualFreightRate.toString());

				List<OutsourcingDeduction> detailDeductions = outsourcingDeductionService.queryByReconciliationDetailId(detail.getReconciliationDetailId());
				for (OutsourcingDeduction detailDeduction : detailDeductions) {
					cells[position.get(detailDeduction.getDeductionId())].setCellValue(detailDeduction.getDeductionAmount().toString());
				}
			}
		}

		return workbook;
	}

	@Override
	public void updateDocumentsCheckState(OutsourcingReconciliation reconciliation) {
		try {
			outsourcingReconciliationMapper.updateDocumentsCheckState(reconciliation);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void confirmSettlement(Map<String, Object> params) throws ParseException {

		try {
			Long reconciliationId = NumberUtils.createLong(params.get("reconciliationId").toString());
			if (StringUtils.isBlank(params.get("confirmSettlementDate").toString())) {
				throw new RuntimeException("结算失败，请选择结算日期后重试。");
			}
			Date confirmSettlementDate = DateUtils.parseDate(params.get("confirmSettlementDate").toString(), "yyyy-MM-dd");

			Environment env = Environment.getEnv();
			OutsourcingReconciliation outsourcingReconciliation = queryById(reconciliationId);

			if (outsourcingReconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
				throw new RuntimeException("当前单据已结算，请检查后重试。");
			}

			if (outsourcingReconciliation.getBillStatus() == BillStatus.Drafted.getIndex()) {
				throw new RuntimeException("未审核的单据无法确认结算，请检查后重试。");
			}

			OutsourcingReconciliation model = new OutsourcingReconciliation();
			model.setReconciliationId(outsourcingReconciliation.getReconciliationId());
			model.setConfirmor(env.getUser().getUserId());
			model.setConfirmDate(new Date());
			model.setSettlementDate(confirmSettlementDate);
			model.setBillStatus(BillStatus.Settled.getIndex());
			updateSelective(model);

			List<OutsourcingReconciliationDetail> details = reconciliationDetailService.queryByReconciliationId(reconciliationId);
			if (CollectionUtils.isNotEmpty(details)) {

				List<Long> originalOrderDetailIds    = details.stream().map(OutsourcingReconciliationDetail::getOriginalOrderDetailId).distinct().collect(Collectors.toList());
				List<Long> outsourcingOrderDetailIds = details.stream().map(OutsourcingReconciliationDetail::getOutsourcingOrderDetailId).distinct().collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(originalOrderDetailIds)) {
					orderService.updateForSettlement(originalOrderDetailIds);
				}
				if (CollectionUtils.isNotEmpty(outsourcingOrderDetailIds)) {
					orderService.updateForSettlement(outsourcingOrderDetailIds);
				}
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	public List<OutsourcingReconciliationDetail> parseExcelForReconciliation(Workbook workbook) throws Exception {

		if (!ObjectUtils.allNotNull(workbook)) {
			throw new NullPointerException("workbook");
		}

		if (workbook.getNumberOfSheets() == 0) {
			throw new Exception("EXCEL文件中未找到工作表，请检查后重试。");
		}

		Environment env = Environment.getEnv();
		DataFormatter formatter = new DataFormatter();
		List<OutsourcingReconciliationDetail> detailList = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);

		// 手动指定列数
		int colCount = 7;
		int rowCount = ExcelUtil.getLastRowNumber(workbook);

		// 指定从第二行开始读取数据，跳过第一行标题行
		for (int rowNumber = 1; rowNumber <= rowCount; rowNumber++) {

			Cell[] cells = new Cell[colCount];
			for (int colNumber = 0; colNumber < colCount; colNumber++) {
				cells[colNumber] = sheet.getRow(rowNumber).getCell(colNumber);
			}

			String carVin      = formatter.formatCellValue(cells[0]);   // 车架号
			String freightRate = formatter.formatCellValue(cells[1]);   // 运价

			OutsourcingReconciliationDetail detailModel = new OutsourcingReconciliationDetail();
			detailModel.setCheckStatus(CheckStatus.Unchecked.getIndex());
			detailModel.setCarVin(carVin);
			detailModel.setFreightRate(new BigDecimal(freightRate));
			detailModel.setCorpId(env.getUser().getCorpId());
			detailModel.setFlag(FlagStatus.No.getIndex());
			detailList.add(detailModel);
		}

		return detailList;
	}
}
