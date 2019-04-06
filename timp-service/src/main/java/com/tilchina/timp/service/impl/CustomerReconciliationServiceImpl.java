package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CustomerReconciliationMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.vo.QuotationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
* 客户对账表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CustomerReconciliationServiceImpl extends BaseServiceImpl<CustomerReconciliation> implements CustomerReconciliationService {

	@Autowired
    private CustomerReconciliationMapper reconciliationMapper;

	@Autowired
	private CustomerReconciliationDetailService detailService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private StockCarService stockCarService;

	@Autowired
	private StockCarTransService stockCarTransService;

	@Autowired
	private TransportOrderService transportOrderService;

	@Autowired
	private TransportOrderDetailService transportOrderDetailService;

	@Autowired
	private UnitService unitService;

	@Autowired
	private DeductionService deductionService;

	@Autowired
	private CustomerReconciliationDeductionService reconciliationDeductionService;

	@Autowired
	private QuotationDetailService quotationDetailService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private CarService carService;

	@Autowired
	private CarTypeService carTypeService;

	@Autowired
	private CityService cityService;

	@Override
	protected BaseMapper<CustomerReconciliation> getMapper() {
		return reconciliationMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CustomerReconciliation customerreconciliation) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "reconciliationNumber", "对账单号", customerreconciliation.getReconciliationNumber(), 20));
        s.append(CheckUtils.checkDate("YES", "reconciliationStartDate", "起始日期", customerreconciliation.getReconciliationStartDate()));
        s.append(CheckUtils.checkDate("YES", "reconciliationEndDate", "截止日期", customerreconciliation.getReconciliationEndDate()));
        s.append(CheckUtils.checkInteger("YES", "carVinCount", "车架号数量", customerreconciliation.getCarVinCount(), 10));
        // s.append(CheckUtils.checkBigDecimal("YES", "totalAmount", "总金额", customerreconciliation.getTotalAmount(), 10, 2));
        // s.append(CheckUtils.checkBigDecimal("YES", "redAmount", "红字总额", customerreconciliation.getRedAmount(), 10, 2));
        // s.append(CheckUtils.checkBigDecimal("YES", "blueAmount", "蓝字总额", customerreconciliation.getBlueAmount(), 10, 2));
        // s.append(CheckUtils.checkBigDecimal("YES", "deductionTotalAmount", "累计扣款", customerreconciliation.getDeductionTotalAmount(), 10, 2));
        s.append(CheckUtils.checkString("YES", "deductionReason", "扣款原因", customerreconciliation.getDeductionReason(), 200));
        s.append(CheckUtils.checkDate("YES", "customerFeedbackDate", "客户反馈时间", customerreconciliation.getCustomerFeedbackDate()));
        s.append(CheckUtils.checkDate("YES", "settlementDate", "结算时间", customerreconciliation.getSettlementDate()));
        s.append(CheckUtils.checkDate("YES", "confirmDate", "确认日期", customerreconciliation.getConfirmDate()));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", customerreconciliation.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", customerreconciliation.getCheckDate()));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "单据状态", customerreconciliation.getBillStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", customerreconciliation.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CustomerReconciliation customerreconciliation) {
        StringBuilder s = checkNewRecord(customerreconciliation);
        s.append(CheckUtils.checkPrimaryKey(customerreconciliation.getReconciliationId()));
		return s;
	}

	@Override
	public PageInfo<CustomerReconciliation> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo(reconciliationMapper.selectList(map));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void checking(Map<String, String> map) {

		Environment env = Environment.getEnv();
		Map<String, Object> params;

		Long customerId;
		Date startDate;
		Date endDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			customerId = Long.parseLong(map.get("customerId"));
			startDate  = format.parse(map.get("startDate"));
			endDate    = format.parse(map.get("endDate"));
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("起始或截止日期格式有误，示例：2000-01-01");
		}

		Long carrierId = env.getUser().getCorpId();
		List<Order> orders = orderService.queryForSettlement(customerId, carrierId, startDate, endDate);
		if (CollectionUtils.isEmpty(orders)) {
			throw new RuntimeException("当前无订单可用于对账");
		}

		StringBuilder messages = new StringBuilder();
		try {
			List<String> carVins = detailService.selectCarVin();

			Integer orderDetailsCount = 0;
			for (Order order : orders) {
				List<OrderDetail> orderDetails = orderDetailService.queryByOrderId(order.getOrderId());
				if (CollectionUtils.isNotEmpty(carVins)) {
					orderDetails = orderDetails.stream().filter(orderDetail -> !carVins.contains(orderDetail.getCarVin())).collect(Collectors.toList());
				}
				if (CollectionUtils.isNotEmpty(orderDetails)) {
					orderDetailsCount += orderDetails.size();
				}
			}
			if (orderDetailsCount == 0) {
				throw new RuntimeException("当前无订单可用于对账");
			}

			// 主表对象填充数据
			CustomerReconciliation model = new CustomerReconciliation();
			model.setReconciliationNumber(String.format("CR%s", DateUtil.dateToStringCode(new Date())));
			model.setCustomerId(customerId);
			model.setReconciliationStartDate(startDate);
			model.setReconciliationEndDate(endDate);
			model.setCarVinCount(orderDetailsCount);
			model.setCreator(env.getUser().getUserId());
			model.setCreateDate(new Date());
			model.setCorpId(env.getUser().getCorpId());
			model.setBillStatus(BillStatus.Drafted.getIndex());
			model.setFlag(FlagStatus.No.getIndex());
			add(model);

			for (Order order : orders) {

				// 根据订单主表ID查询出所有子表记录
				List<OrderDetail> orderDetails = orderDetailService.queryByOrderId(order.getOrderId());
				if (CollectionUtils.isEmpty(orderDetails)) {
					messages.append(String.format("订单编号：%s下无未对账订单明细", order.getOrderCode()));
				}
				if (CollectionUtils.isNotEmpty(carVins)) {
					orderDetails = orderDetails.stream().filter(orderDetail -> !carVins.contains(orderDetail.getCarVin())).collect(Collectors.toList());
				}
				if (CollectionUtils.isEmpty(orderDetails)) {
					continue;
				}

				for (OrderDetail orderDetail : orderDetails) {

					// 通过收发货单位ID查询收发货城市ID
					Long sendCityId = unitService.queryById(orderDetail.getSendUnitId()).getCityId();
					Long recvCityId = unitService.queryById(orderDetail.getReceiveUnitId()).getCityId();

					String sendCityName = unitService.queryById(orderDetail.getSendUnitId()).getRefCityName();
					String recvCityName = unitService.queryById(orderDetail.getReceiveUnitId()).getRefCityName();

//					QuotationVO quotationVO = new QuotationVO();
//					quotationVO.setCustomerId(order.getCorpCustomerId());
//					quotationVO.setVendorCorpId(order.getCorpCarrierId());
//					quotationVO.setSendCityId(sendCityId);
//					quotationVO.setRecvCityId(recvCityId);
//					quotationVO.setCarBrandId(orderDetail.getBrandId());
//					quotationVO.setCarTypeId(orderDetail.getCarTypeId());
//					quotationVO.setCarModelId(orderDetail.getCarId());
//					quotationVO.setJobType(order.getWorkType());
//					quotationVO.setOrderDate(order.getOrderDate());
//
//					QuotationDetail quotationDetail = quotationDetailService.queryByQuotationVO(quotationVO);
//					if (quotationDetail == null || quotationDetail.getTotalPrice() == null) {
//						throw new RuntimeException(String.format("客户报价中未查询到从%s到%s的报价，请维护客户报价后重试。", sendCityName, recvCityName));
//					}

					params = new HashMap(){{
						put("customerCorpId", order.getCorpCustomerId());
						put("recvSendUnitId", orderDetail.getReceiveUnitId());
					}};
					// TODO XYS - 默认运输公司作废后代码逻辑需要更新
//					List<CustomerInfo> customerInfos = customerInfoService.queryList(params);
//					if (CollectionUtils.isEmpty(customerInfos)) {
//						Corp corp = corpService.queryById(order.getCorpCustomerId());
//						Unit unit = unitService.queryById(orderDetail.getReceiveUnitId());
//						if (corp != null && unit != null)
//						throw new RuntimeException(String.format("客户定制信息中未查询到该经销商，请维护客户定制信息档案后重试。"));
//					}
//					CustomerInfo customerInfo = customerInfos.stream().findFirst().get();


					Long transportOrderDetailId = null;
					String transportOrderNumber = null;
					Date actualLoadingDate = null;
					Date designatedLoadingDate = orderDetail.getClaimLoadDate();

					// 通过商品车库存表查询商品车ID，通过商品车运输记录表查询到运单子表ID
					params = new HashMap(){{
						put("orderId", order.getOrderId());
						put("orderDetailId", orderDetail.getOrderDetailId());
						put("carVin", orderDetail.getCarVin());
					}};
					List<StockCar> stockCarList = stockCarService.queryList(params);
					if (CollectionUtils.isNotEmpty(stockCarList)) {

						StockCar stockCar = stockCarList.stream().findFirst().get();
						params = new HashMap(){{
							put("stockCarId", stockCar.getStockCarId());
						}};
						List<StockCarTrans> stockCarTransList = stockCarTransService.queryList(params);


						if (CollectionUtils.isNotEmpty(stockCarTransList)) {
							StockCarTrans stockCarTrans = stockCarTransList.stream().findFirst().get();

							TransportOrder transportOrder = transportOrderService.queryById(stockCarTrans.getTransportOrderId());
							if (transportOrder == null) {
								throw new RuntimeException(String.format("商品车运输记录表对应的运单不存在，请检查后重试。"));
							}
							TransportOrderDetail transportOrderDetail = transportOrderDetailService.queryById(stockCarTrans.getTransportOrderDetailId());
							if (transportOrderDetail == null) {
								throw new RuntimeException(String.format("运单号: %s对应的运单明细不存在，请检查后重试。",transportOrderDetail.getTransportOrderCode()));
							}

							transportOrderDetailId = transportOrderDetail.getTransportOrderDetailId();
							transportOrderNumber = transportOrderDetail.getTransportOrderCode();
							actualLoadingDate = transportOrderDetail.getRealityLoadingDate();
						}
					}

					CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
					detailModel.setReconciliationId(model.getReconciliationId());
					detailModel.setOrderDetailId(orderDetail.getOrderDetailId());
					detailModel.setOrderNumber(order.getOrderCode());
					detailModel.setTransportOrderDetailId(transportOrderDetailId);
					detailModel.setTransportOrderNumber(transportOrderNumber);
					detailModel.setSendCityId(sendCityId);
					detailModel.setRecvCityId(recvCityId);
					// detailModel.setDistance(quotationDetail.getDistance());
					detailModel.setProductionNumber(orderDetail.getProductNumber());
					detailModel.setCarBrandId(orderDetail.getBrandId());
					detailModel.setCarTypeId(orderDetail.getCarTypeId());
					detailModel.setCarModelId(orderDetail.getCarId());
					detailModel.setCarVin(orderDetail.getCarVin());
					// detailModel.setCustomerId(customerInfo.getCustomerId());
					detailModel.setCustomerCorpId(order.getCorpCustomerId());
					detailModel.setDesignatedLoadingDate(designatedLoadingDate);
					detailModel.setActualLoadingDate(actualLoadingDate);
					detailModel.setQuotationId(orderDetail.getQuotedPriceId());
					detailModel.setQuotationPrice(orderDetail.getCustomerQuotedPrice());
					// detailModel.setQuotationId(quotationDetail.getQuotationId());
					// detailModel.setQuotationPrice(quotationDetail.getTotalPrice());
					detailModel.setSendUnitId(orderDetail.getSendUnitId());
					detailModel.setRecvUnitId(orderDetail.getReceiveUnitId());
					detailModel.setCorpId(env.getUser().getCorpId());
					detailModel.setFlag(FlagStatus.No.getIndex());
					detailService.add(detailModel);
				}
			}
			detailService.updateAmountForReconciliationDetail(model.getReconciliationId());
			updateAmountForReconciliation(model.getReconciliationId());
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void confirmSettlement(Map<String, Object> params) {

		Long reconciliationId = null;
		Date confirmSettlementDate = null;

		if (Objects.isNull(confirmSettlementDate)) {
			throw new RuntimeException("结算日期为空，请选择结算日期后重试。");
		}

		try {
			reconciliationId = Long.parseLong(params.get("reconciliationId").toString());
			confirmSettlementDate = DateUtils.parseDate(params.get("confirmSettlementDate").toString(), "yyyy-MM-dd");
		} catch (ParseException e) {
			log.error("{}", e);
			throw new RuntimeException("JSON格式有误，请检查后重试。");
		}

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation == null) {
			throw new RuntimeException("记录不存在，请检查后重试。");
		}
		if (customerReconciliation.getBillStatus() == BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("当前单据处于制单状态，无法进行结算操作，请审核后重试。");
		}
		if (customerReconciliation.getBillStatus() == BillStatus.Settled.getIndex()) {
			throw new RuntimeException("当前单据已结算，请勿重复操作。");
		}

		List<CustomerReconciliationDetail> details = detailService.selectByReconciliationId(reconciliationId);
		if (CollectionUtils.isEmpty(details)) {
			throw new RuntimeException(String.format("对账单号: %s下无明细可用于结算", customerReconciliation.getReconciliationNumber()));
		}
		List<Long> orderDetailIds = details.stream().map(CustomerReconciliationDetail::getOrderDetailId).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(orderDetailIds)) {
			try {
				orderService.updateForSettlement(orderDetailIds);
			} catch (Exception e) {
				log.error("{}", e);
				throw e;
			}
		}
		try {
			Environment env = Environment.getEnv();
			customerReconciliation.setSettlementDate(confirmSettlementDate);
			customerReconciliation.setConfirmor(env.getUser().getUserId());
			customerReconciliation.setConfirmDate(new Date());
			customerReconciliation.setBillStatus(BillStatus.Settled.getIndex());
			updateSelective(customerReconciliation);
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("结算失败，请重试。");
		}
	}

	@Transactional
	@Override
	public List<CustomerReconciliation> selectByReconciliationNumber(String reconciliationNumber) {

		try {
			List<CustomerReconciliation> customerReconciliations = reconciliationMapper.selectByReconciliationNumber(reconciliationNumber);
			return customerReconciliations;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void refreshQuotation(Long reconciliationId) {

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("非制单状态下的单据不可刷新客户报价，请检查后重试。");
		}
		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法刷新客户报价。");
		}

		List<CustomerReconciliationDetail> details = detailService.selectByReconciliationId(reconciliationId);

		for (CustomerReconciliationDetail detail : details) {

			OrderDetail orderDetail = orderDetailService.queryById(detail.getOrderDetailId());
			Order order = orderService.queryById(orderDetail.getOrderId());

			Long sendUnitId    = orderDetail.getSendUnitId();
			Long receiveUnitId = orderDetail.getReceiveUnitId();

			Long sendCityId = unitService.queryById(sendUnitId).getCityId();
			Long recvCityId = unitService.queryById(receiveUnitId).getCityId();

			QuotationVO quotationVO = new QuotationVO();
			quotationVO.setCustomerId(order.getCorpCustomerId());
			quotationVO.setVendorCorpId(order.getCorpCarrierId());
			quotationVO.setSendCityId(sendCityId);
			quotationVO.setRecvCityId(recvCityId);
			quotationVO.setCarBrandId(orderDetail.getBrandId());
			quotationVO.setCarTypeId(orderDetail.getCarTypeId());
			quotationVO.setCarModelId(orderDetail.getCarId());
			quotationVO.setJobType(order.getWorkType());
			quotationVO.setOrderDate(order.getOrderDate());

			if (sendCityId == null) throw new RuntimeException("发货城市为空");
			if (recvCityId == null) throw new RuntimeException("收货城市为空");

			String sendCityName = cityService.queryById(sendCityId).getCityName();
			String recvCityName = cityService.queryById(recvCityId).getCityName();

			QuotationDetail quotationDetail = quotationDetailService.queryByQuotationVO(quotationVO);
			if (quotationDetail == null || quotationDetail.getTotalPrice() == null) {
				throw new RuntimeException(String.format("未找到从%s到%s的客户报价，请检查后重试。", sendCityName, recvCityName));
			}

			CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
			detailModel.setReconciliationDetailId(detail.getReconciliationDetailId());
			detailModel.setQuotationPrice(quotationDetail.getTotalPrice());
			detailService.updateSelective(detailModel);
		}

		updateAmountForReconciliation(reconciliationId);
	}

	@Transactional
	@Override
	public String importFile(Integer fileType, String reconciliationNumber, MultipartFile file) throws Exception {

		StringBuilder messages = new StringBuilder();

		reconciliationNumber = reconciliationNumber.trim();

		if (StringUtils.isBlank(reconciliationNumber)) {
			throw new RuntimeException("对账单号不能为空，请重新输入。");
		}

		if (fileType == null) {
			throw new RuntimeException("请选择导入类型后重试。");
		}

		switch (fileType) {
			case 0: break;
			case 1: break;
			default: throw new RuntimeException("导入模板类型有误，请重新选择。");
		}

		List<CustomerReconciliation> customerReconciliationList = selectByReconciliationNumber(reconciliationNumber);
		if (CollectionUtils.isEmpty(customerReconciliationList)) {
			throw new RuntimeException("对账单号有误，请重新输入。");
		}
		CustomerReconciliation customerReconciliation = customerReconciliationList.stream().findFirst().get();

		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法进行当前操作。");
		}

		Map<String, Object> params = new HashMap(){{
			put("reconciliationId", customerReconciliation.getReconciliationId());
		}};
		List<CustomerReconciliationDetail> customerReconciliationDetails = detailService.queryList(params);
		if (CollectionUtils.isEmpty(customerReconciliationDetails)) {
			throw new RuntimeException("该对账单号下无相关明细数据。");
		}

		String filePath;
		try {
			// 保存用户上传的客户反馈EXCEL表格
			filePath = FileUtil.uploadExcel(file);
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("保存文件失败，请重新上传。");
		}

		InputStream fileStream;
		try {
			fileStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("读取文件失败，请重新上传。");
		}

		Workbook workbook;
		try {
			if (filePath.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fileStream);
			} else if (filePath.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fileStream);
			} else {
				throw new RuntimeException("文件类型有误，请重新上传。");
			}
		} catch (IOException e) {
			log.error("{}", e);
			throw e;
		}

		switch (fileType) {
			case 0: parseExcelOfRedBlueHedge(customerReconciliation, workbook);break;
			case 1: parseExcelOfCustomerFeedBack(customerReconciliation, workbook); break;
			default: throw new RuntimeException("未知导入类型，请检查后重试。");
		}

		return null;
	}

	@Transactional
	@Override
	public String importReconciliation(Long customerId, String startDateString, String endDateString, MultipartFile file) throws Exception {

		if (customerId == null)
			throw new BusinessException("9001", "客户");
		if (startDateString == null)
			throw new BusinessException("9001", "起始日期");
		if (endDateString == null)
			throw new BusinessException("9001", "截止日期");
		if (file == null)
			throw new BusinessException("9001", "对账文件");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate, endDate;

		try {
			startDate = format.parse(startDateString);
			endDate = format.parse(endDateString);
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("日期格式有误，请重新选择。");
		}

		Corp corp = corpService.queryById(customerId);
		if (corp == null) throw new BusinessException("9008", "客户");

		String filePath;
		try {
			// 保存用户上传的客户反馈EXCEL表格
			filePath = FileUtil.uploadFile(file, "EXCEL");
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("保存文件失败，请重新上传。");
		}

		InputStream fileStream;
		try {
			fileStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("读取文件失败，请重新上传。");
		}

		Workbook workbook;
		try {
			if (filePath.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fileStream);
			} else if (filePath.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fileStream);
			} else {
				throw new RuntimeException("文件类型有误，请重新上传。");
			}
		} catch (IOException e) {
			throw e;
		}

		String messages = parseExcelOfReconciliation(customerId, startDate, endDate, workbook);
		return messages;
	}

	@Transactional
	@Override
	public Workbook exportExcel(Long reconciliationId) {

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation == null) {
			throw new RuntimeException("对账单（ID）有误，请重新选择。");
		}
		if (customerReconciliation.getBillStatus() == BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("当前单据未审核，无法导出对账单。");
		}

		List<CustomerReconciliationDetail> customerReconciliationDetails = detailService.selectByReconciliationId(reconciliationId);
		if (customerReconciliationDetails.size() == 0) {
			throw new RuntimeException("当前单据无明细数据，无法导出对账单。");
		}

		File file = new File(PropertiesUtils.getString("templates.mercedesbenz"));
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Sheet sheet = workbook.getSheetAt(0);

		for (int i = 0; i < customerReconciliationDetails.size(); i++) {

			OrderDetail orderDetail = orderDetailService.queryById(customerReconciliationDetails.get(i).getOrderDetailId());
			if (orderDetail == null) {
				throw new RuntimeException("对账明细记录与订单明细记录不符，请检查订单后重试。");
			}
			Order order = orderService.queryById(orderDetail.getOrderId());
			if (order == null) {
				throw new RuntimeException("对账记录与订单记录不符，请检查订单后重试。");
			}

			String vendorName = corpService.queryById(order.getCorpCarrierId()).getCorpName();

			String carName = carService.queryById(orderDetail.getCarId()).getCarName();

			Map<String, Object> params = new HashMap(){{
				put("customerCorpId", order.getCorpCustomerId());
				put("recvSendUnitId", orderDetail.getReceiveUnitId());
			}};
//			List<CustomerInfo> customerInfos = customerInfoService.queryList(params);
//			if (CollectionUtils.isEmpty(customerInfos)) {
//				throw new RuntimeException("客户定制信息中未查询到该经销商，请维护客户定制信息档案后重试。");
//			}
//			CustomerInfo customerInfo = customerInfos.stream().findFirst().get();

//			String dealerName = null;
//			Corp corp = corpService.queryById(customerInfo.getCustomerCorpId());
//			if (corp != null) {
//				dealerName = corp.getCorpName();
//			}

			CustomerReconciliationDetail customerReconciliationDetail = customerReconciliationDetails.get(i);
			String carBrandName = Optional.ofNullable(customerReconciliationDetail.getRefCarBrandName()).orElse("");
			String carTypeName  = Optional.ofNullable(customerReconciliationDetail.getRefCarTypeName()).orElse("");
			String carModelName = Optional.ofNullable(customerReconciliationDetail.getRefCarModelName()).orElse("");
			String sendCityName = Optional.ofNullable(customerReconciliationDetail.getRefSendCityName()).orElse("");
			String recvCityName = Optional.ofNullable(customerReconciliationDetail.getRefRecvCityName()).orElse("");

			Long sendCityId = customerReconciliationDetail.getSendCityId();
			Long recvCityId = customerReconciliationDetail.getRecvCityId();

			QuotationVO quotationVO = new QuotationVO();
			quotationVO.setCustomerId(order.getCorpCustomerId());
			quotationVO.setVendorCorpId(order.getCorpCarrierId());
			quotationVO.setSendCityId(sendCityId);
			quotationVO.setRecvCityId(recvCityId);
			quotationVO.setCarBrandId(orderDetail.getBrandId());
			quotationVO.setCarTypeId(orderDetail.getCarTypeId());
			quotationVO.setCarModelId(orderDetail.getCarId());
			quotationVO.setJobType(order.getWorkType());
			quotationVO.setOrderDate(order.getOrderDate());

			QuotationDetail quotationDetail = quotationDetailService.queryByQuotationVO(quotationVO);
			if (quotationDetail == null || quotationDetail.getTotalPrice() == null) {
				throw new RuntimeException(String.format("未找到从%s到%s的客户报价，请检查后重试。", sendCityName, recvCityName));
			}

			Row row = sheet.createRow(i + 2);

			Cell cell = row.createCell(0);
			cell.setCellValue(i + 1);

			cell = row.createCell(1);
			cell.setCellValue(vendorName);

			cell = row.createCell(3);
			cell.setCellValue(orderDetail.getProductNumber());

			cell = row.createCell(4);
			cell.setCellValue(carModelName);

			cell = row.createCell(5);
			cell.setCellValue(orderDetail.getCarVin());

//			cell = row.createCell(6);
//			cell.setCellValue(customerInfo.getDealerNumber());
//
//			cell = row.createCell(7);
//			cell.setCellValue(dealerName);

			cell = row.createCell(8);
			cell.setCellValue(customerReconciliationDetails.get(i).getDesignatedLoadingDate());

			cell = row.createCell(9);
			cell.setCellValue(customerReconciliationDetails.get(i).getActualLoadingDate());

			cell = row.createCell(10);
			cell.setCellValue(carTypeName);

			cell = row.createCell(11);
			cell.setCellValue(customerReconciliationDetails.get(i).getRefRecvCityName());

			BigDecimal totalAmount;
			BigDecimal settlementPrice = customerReconciliationDetails.get(i).getSettlementPrice();
			BigDecimal quotationPrice = quotationDetail.getTotalPrice();
			if (settlementPrice != null) {
				totalAmount = settlementPrice;
			} else  if (quotationDetail != null) {
				totalAmount = quotationPrice;
			} else {
				totalAmount = new BigDecimal(0);
			}

			BigDecimal vatFee = totalAmount.multiply(quotationDetail.getTaxRate()).setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal vatExclusiveFee = totalAmount.subtract(vatFee).setScale(2, RoundingMode.HALF_DOWN);

			cell = row.createCell(12);
			cell.setCellValue(vatExclusiveFee.toString());

			cell = row.createCell(13);
			cell.setCellValue(vatFee.toString());

			cell = row.createCell(14);
			cell.setCellValue(totalAmount.toString());
		}

		return workbook;
	}

	@Transactional
	@Override
	public void setDocumentsChecked(Long reconciliationId) {

		try {
			Environment env = Environment.getEnv();
			CustomerReconciliation customerReconciliation = queryById(reconciliationId);
			if (customerReconciliation == null) {
				throw new RuntimeException("未找到对账单，请重新选择后重试。");
			}
			if (customerReconciliation.getConfirmor() != null) {
				throw new RuntimeException("当前单据已结算，无法进行审核操作。");
			}

			if (customerReconciliation.getBillStatus() == 0) {
				customerReconciliation.setBillStatus(1);
				customerReconciliation.setChecker(env.getUser().getUserId());
				customerReconciliation.setCheckDate(new Date());
				reconciliationMapper.updateCheckStateByPrimaryKey(customerReconciliation);

				List<Long> orderDetailIds = detailService.selectByReconciliationId(reconciliationId).stream().map(CustomerReconciliationDetail::getOrderDetailId).collect(Collectors.toList());
				orderService.updateForReconciliation(orderDetailIds, false);
			} else {
				throw new RuntimeException("非制单状态下的对账单无法进行审核");
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void setDocumentsUnchecked(Long reconciliationId) {

		try {
			CustomerReconciliation customerReconciliation = queryById(reconciliationId);
			if (customerReconciliation == null) {
				throw new RuntimeException("未找到对账单，请重新选择后重试。");
			}
			if (customerReconciliation.getConfirmor() != null) {
				throw new RuntimeException("当前单据已结算，无法取消审核。");
			}

			if (customerReconciliation.getBillStatus() == 1) {
				customerReconciliation.setBillStatus(0);
				customerReconciliation.setChecker(null);
				customerReconciliation.setCheckDate(null);
				reconciliationMapper.updateCheckStateByPrimaryKey(customerReconciliation);

				List<Long> orderDetailIds = detailService.selectByReconciliationId(reconciliationId).stream().map(CustomerReconciliationDetail::getOrderDetailId).collect(Collectors.toList());
				orderService.updateForReconciliation(orderDetailIds, true);
			} else {
				throw new RuntimeException("非审核状态下的对账单无法取消审核");
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void parseExcelOfRedBlueHedge(CustomerReconciliation customerReconciliation, Workbook workbook) {
		throw new RuntimeException("红蓝对冲暂时合并于客户反馈功能中");
	}

	@Transactional
	@Override
	public String parseExcelOfReconciliation(Long customerId, Date startDate, Date endDate, Workbook workbook) {

		Map<String, Object> params;

		Environment env = Environment.getEnv();
		Long carrierId = env.getUser().getCorpId();
		List<Order> orders = orderService.queryForSettlement(customerId, carrierId, startDate, endDate);
		if (CollectionUtils.isEmpty(orders)) {
			throw new RuntimeException("当前无订单可用于对账");
		}

		if (workbook.getNumberOfSheets() == 0) {
			throw new RuntimeException("文件中未找到任何工作表，请检查后重试。");
		}

		Sheet sheet = workbook.getSheetAt(0);
		int rowCount = ExcelUtil.getLastRowNumber(workbook);

		DataFormatter formatter = new DataFormatter();
		List<String> excelCarVins = new ArrayList<>();
		for (int rowNumber = 1; rowNumber <= rowCount; rowNumber++) {
			excelCarVins.add(formatter.formatCellValue(sheet.getRow(rowNumber).getCell(0)));
		}

		if (CollectionUtils.isEmpty(excelCarVins)) {
			throw new RuntimeException("对账单中无车架号，请检查后重试。");
		}

		StringBuilder messages = new StringBuilder();
		try {
			// 获取客户对账明细中所有车架号
			List<String> carVins = detailService.selectCarVin();

			boolean isAnyNotEmpty = false;
			int validCarVinCount = 0;

			List<String> existedDetailCarVins = new ArrayList<>();
			List<String> existedExcelCarVins = new ArrayList<>();
			for (Order order : orders) {
				List<OrderDetail> orderDetails = orderDetailService.queryByOrderId(order.getOrderId());
				List<String> orderDetailCarVins = orderDetails.stream().map(OrderDetail::getCarVin).collect(Collectors.toList());

				List<String> validCarVins = excelCarVins.stream().filter(excelCarVin -> orderDetailCarVins.contains(excelCarVin)).collect(Collectors.toList());

				List<String> invalidCarVins = excelCarVins.stream().filter(excelCarVin -> !orderDetailCarVins.contains(excelCarVin)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(invalidCarVins)) {
					existedExcelCarVins.addAll(invalidCarVins);
				}

				List<String> existedCarVins = validCarVins.stream().filter(validCarVin -> carVins.contains(validCarVin)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(existedCarVins)) {
					existedDetailCarVins.addAll(existedCarVins);
				}

				orderDetails = orderDetails
						.stream()
						.filter(orderDetail ->
								carVins.contains(orderDetail.getCarVin()) == false &&
								excelCarVins.contains(orderDetail.getCarVin()))
						.collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(orderDetails)) {
					isAnyNotEmpty = true;
					validCarVinCount += orderDetails.size();
				}
			}

			existedDetailCarVins = existedDetailCarVins.stream().distinct().collect(Collectors.toList());
			existedExcelCarVins = existedExcelCarVins.stream().distinct().collect(Collectors.toList());

			if (!isAnyNotEmpty) {
				if (CollectionUtils.isNotEmpty(existedDetailCarVins)) {
					messages.append(String.format("订单中车架号：%s已存在于其他对账明细中，请检查后重试。", String.join(",", existedDetailCarVins)));
				}
				if (CollectionUtils.isNotEmpty(existedExcelCarVins)) {
					messages.append(String.format("未在客户订单中找到对账单中以下车架号：%s 请检查后重试。", String.join(",", existedExcelCarVins)));
				}

				throw new RuntimeException(messages.toString());
			}

			// 主表对象填充数据
			CustomerReconciliation model = new CustomerReconciliation();
			model.setReconciliationNumber(String.format("CR%s", DateUtil.dateToStringCode(new Date())));
			model.setCustomerId(customerId);
			model.setReconciliationStartDate(startDate);
			model.setReconciliationEndDate(endDate);
			model.setCarVinCount(validCarVinCount);
			model.setCreator(env.getUser().getUserId());
			model.setCreateDate(new Date());
			model.setCorpId(env.getUser().getCorpId());
			model.setBillStatus(0);
			model.setFlag(0);
			add(model);

			List<CustomerReconciliationDetail> addDetails = new ArrayList<>();
			for (Order order : orders) {

				// 根据订单主表ID查询出所有子表记录
				List<OrderDetail> orderDetails = orderDetailService.queryByOrderId(order.getOrderId());
				if (CollectionUtils.isEmpty(orderDetails)) {
					messages.append(String.format("订单编号：%s下无未对账订单明细", order.getOrderCode()));
				}
				if (CollectionUtils.isNotEmpty(carVins)) {
					orderDetails = orderDetails.stream().filter(orderDetail -> carVins.contains(orderDetail.getCarVin()) == false).collect(Collectors.toList());
				}

				// 筛选出EXCEL里存在的车架号的子表记录
				orderDetails = orderDetails.stream().filter(orderDetail -> excelCarVins.contains(orderDetail.getCarVin())).collect(Collectors.toList());

				if (CollectionUtils.isEmpty(orderDetails)) {
					continue;
				}

				for (OrderDetail orderDetail : orderDetails) {

					// 通过收发货单位ID查询收发货城市ID
					Long sendCityId = unitService.queryById(orderDetail.getSendUnitId()).getCityId();
					Long recvCityId = unitService.queryById(orderDetail.getReceiveUnitId()).getCityId();

					String sendCityName = unitService.queryById(orderDetail.getSendUnitId()).getRefCityName();
					String recvCityName = unitService.queryById(orderDetail.getReceiveUnitId()).getRefCityName();

					QuotationVO quotationVO = new QuotationVO();
					quotationVO.setCustomerId(order.getCorpCustomerId());
					quotationVO.setVendorCorpId(order.getCorpCarrierId());
					quotationVO.setOrderDate(order.getOrderDate());
					quotationVO.setSendCityId(sendCityId);
					quotationVO.setRecvCityId(recvCityId);
					quotationVO.setCarBrandId(orderDetail.getBrandId());
					quotationVO.setCarTypeId(orderDetail.getCarTypeId());
					quotationVO.setCarModelId(orderDetail.getCarId());

					QuotationDetail quotationDetail = quotationDetailService.queryByQuotationVO(quotationVO);
					if (quotationDetail == null || quotationDetail.getTotalPrice() == null) {
						throw new RuntimeException(String.format("客户报价中未查询到从%s到%s的报价，请维护客户报价后重试。", sendCityName, recvCityName));
					}

					params = new HashMap(){{
						put("customerCorpId", order.getCorpCustomerId());
						put("recvSendUnitId", orderDetail.getReceiveUnitId());
					}};
//					List<CustomerInfo> customerInfos = customerInfoService.queryList(params);
//					if (CollectionUtils.isEmpty(customerInfos)) {
//						throw new RuntimeException("客户定制信息中未查询到该经销商，请维护客户定制信息档案后重试。");
//					}
//					CustomerInfo customerInfo = customerInfos.stream().findFirst().get();


					Long transportOrderDetailId = null;
					String transportOrderNumber = null;
					Date actualLoadingDate = null;
					Date designatedLoadingDate = orderDetail.getClaimLoadDate();

					// 通过商品车库存表查询商品车ID，通过商品车运输记录表查询到运单子表ID
					params = new HashMap(){{
						put("orderId", order.getOrderId());
						put("orderDetailId", orderDetail.getOrderDetailId());
						put("carVin", orderDetail.getCarVin());
					}};
					List<StockCar> stockCarList = stockCarService.queryList(params);
					if (CollectionUtils.isNotEmpty(stockCarList)) {

						StockCar stockCar = stockCarList.stream().findFirst().get();
						params = new HashMap(){{
							put("stockCarId", stockCar.getStockCarId());
						}};
						List<StockCarTrans> stockCarTransList = stockCarTransService.queryList(params);

						if (CollectionUtils.isNotEmpty(stockCarTransList)) {
							StockCarTrans stockCarTrans = stockCarTransList.stream().findFirst().get();

							TransportOrder transportOrder = transportOrderService.queryById(stockCarTrans.getTransportOrderId());
							if (transportOrder == null) {
								throw new RuntimeException(String.format("商品车运输记录表对应的运单不存在，请检查后重试。"));
							}
							TransportOrderDetail transportOrderDetail = transportOrderDetailService.queryById(stockCarTrans.getTransportOrderDetailId());
							if (transportOrderDetail == null) {
								throw new RuntimeException(String.format("运单号: %s对应的运单明细不存在，请检查后重试。",transportOrderDetail.getTransportOrderCode()));
							}

							transportOrderDetailId = transportOrderDetail.getTransportOrderDetailId();
							transportOrderNumber = transportOrderDetail.getTransportOrderCode();
							actualLoadingDate = transportOrderDetail.getRealityLoadingDate();
						}
					}


					BigDecimal quotationPrice = quotationDetail.getTotalPrice();
					BigDecimal settlementPrice = quotationPrice;

					CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
					detailModel.setReconciliationId(model.getReconciliationId());
					detailModel.setOrderDetailId(orderDetail.getOrderDetailId());
					detailModel.setOrderNumber(order.getOrderCode());
					detailModel.setTransportOrderDetailId(transportOrderDetailId);
					detailModel.setTransportOrderNumber(transportOrderNumber);
					detailModel.setSendCityId(sendCityId);
					detailModel.setRecvCityId(recvCityId);
					detailModel.setDistance(quotationDetail.getDistance());
					detailModel.setProductionNumber(orderDetail.getProductNumber());
					detailModel.setCarBrandId(orderDetail.getBrandId());
					detailModel.setCarTypeId(orderDetail.getCarTypeId());
					detailModel.setCarModelId(orderDetail.getCarId());
					detailModel.setCarVin(orderDetail.getCarVin());
					// detailModel.setCustomerId(customerInfo.getCustomerId());
					detailModel.setCustomerCorpId(order.getCorpCustomerId());
					detailModel.setDesignatedLoadingDate(designatedLoadingDate);
					detailModel.setActualLoadingDate(actualLoadingDate);
					detailModel.setQuotationId(quotationDetail.getQuotationId());
					detailModel.setQuotationPrice(quotationPrice);
					detailModel.setSettlementPrice(settlementPrice);
					detailModel.setSendUnitId(orderDetail.getSendUnitId());
					detailModel.setRecvUnitId(orderDetail.getReceiveUnitId());
					detailModel.setCorpId(env.getUser().getCorpId());
					detailModel.setFlag(0);
					detailService.add(detailModel);
				}
				detailService.updateAmountForReconciliationDetail(model.getReconciliationId());
				updateAmountForReconciliation(model.getReconciliationId());
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		return messages.toString();
	}

	@Transactional
	@Override
	public void parseExcelOfCustomerFeedBack(CustomerReconciliation customerReconciliation, Workbook workbook) {

		if (customerReconciliation.getBillStatus() != BillStatus.Audited.getIndex()) {
			throw new RuntimeException("当前单据未审核，无法客户反馈。");
		}
		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法客户反馈。");
		}

		Environment env = Environment.getEnv();
		Map<String, Object> params;

		if (workbook.getNumberOfSheets() == 0) {
			throw new RuntimeException("文件中未找到任何工作表，请检查后重试。");
		}

		Sheet sheet = workbook.getSheetAt(0);

		int rowCount = ExcelUtil.getLastRowNumber(workbook);

		DataFormatter formatter = new DataFormatter();

		int colCount = 0;
		Map<String, Long> deductions = new HashMap<>();
		while (true) {
			Cell cell = sheet.getRow(1).getCell(colCount);
			String cellString = formatter.formatCellValue(cell).trim();
			if (StringUtils.isBlank(cellString.trim())) {
				break;
			}

			// 从第7列开始读取扣款项目并解析
			if (colCount >= 7) {
				params = new HashMap(){{
					put("deductionName", cellString);
				}};
				List<Deduction> deductionList = deductionService.queryList(params);
				if (deductionList == null || deductionList.size() == 0) {
					throw new RuntimeException(String.format("扣款项目档案中未找到：%s，请手动维护后重新上传。", cellString));
				}
				Deduction deduction = deductionList.stream().findFirst().get();
				deductions.put(cellString, deduction.getDeductionId());
			}
			colCount++;
		}

		StringBuilder errorMsg = new StringBuilder();
		String[] headerValues = { "VIN", "指定装车日期", "收货单位经销商编码", "发货单位经销商编码", "收货单位", "发货单位", "调整报价" };
		Map<Integer, Boolean> nullable = new HashMap(){{
			put(0, false);
			put(1, false);
			put(2, false);
			put(3, false);
			put(4, false);
			put(5, false);
			put(6, false);
		}};


		for (int i = 2; i <= rowCount; i++) {

			String[] cellValues = new String[7];
			for (int j = 0; j < 7; j++) {

				Cell cell = sheet.getRow(i).getCell(j);
				if (cell == null) {
					errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
					continue;
				}
				cellValues[j] = formatter.formatCellValue(cell);

				if (!nullable.get(j) && StringUtils.isBlank(cellValues[j])) {
					errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
				}
			}
		}

		if (StringUtils.isNotBlank(errorMsg.toString())) {
			throw new RuntimeException(errorMsg.toString());
		}

		List<CustomerReconciliationDetail> invalidDetails;
		List<CustomerReconciliationDetail> validDetails = new ArrayList<>();
		List<CustomerReconciliationDetail> details = detailService.selectByReconciliationId(customerReconciliation.getReconciliationId());

		List<String> excelCarVins = new ArrayList<>();
		for (int rowNumber = 2; rowNumber <= rowCount; rowNumber++) {

			Cell[] cells = new Cell[colCount];
			for (int colNumber = 0; colNumber < colCount; colNumber++) {
				cells[colNumber] = sheet.getRow(rowNumber).getCell(colNumber);
			}

			String carVin                      = formatter.formatCellValue(cells[0]);
			String designatedLoadingDateString = cells[1].getDateCellValue().toString();
			String recvDealerNumber            = formatter.formatCellValue(cells[2]);
			String sendDealerNumber            = formatter.formatCellValue(cells[3]);
			String recvUnitName                = formatter.formatCellValue(cells[4]);
			String sendUnitName                = formatter.formatCellValue(cells[5]);

			excelCarVins.add(carVin);

			DateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
			Date designatedLoadingDate = null;
			try {
				designatedLoadingDate = format.parse(designatedLoadingDateString);
			} catch (ParseException e) {
				log.error("{}", e);
				throw new RuntimeException("EXCEL中日期格式应为2018-01-01");
			}


			Long sendUnitId = null;
			Long recvUnitId = null;
			Unit sendUnit   = null;
			Unit recvUnit   = null;

			try {
				List<Unit> recvUnits = unitService.queryByName(recvUnitName);
				List<Unit> sendUnits = unitService.queryByName(sendUnitName);
				if (CollectionUtils.isNotEmpty(recvUnits)) {
					if (recvUnits.size() == 1) {
						recvUnit = recvUnits.stream().findFirst().get();
						recvUnitId = recvUnit.getUnitId();
					}
					if (recvUnits.size() > 1) {
//						CustomerInfo customerInfo = customerInfoService.queryByDealerNumber(recvDealerNumber);
//						if (customerInfo == null) {
//							throw new RuntimeException("未查询到收货单位经销商编码");
//						}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//						recvUnits = recvUnits.stream().filter(unit -> unit.getSuperorCorpId() == customerInfo.getCustomerCorpId()).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(recvUnits)) {
							if (recvUnits.size() == 1) {
								recvUnit = recvUnits.stream().findFirst().get();
								recvUnitId = recvUnit.getUnitId();
							}
							if (recvUnits.size() > 1) {
								throw new RuntimeException(String.format("当前收货单位经销商编码: %s存在多个同名收货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
							}
						} else {
							throw new RuntimeException(String.format("未查询到与收货单位经销商编码: %s关联的收货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
						}
					}
				}
				if (CollectionUtils.isNotEmpty(sendUnits)) {
					if (sendUnits.size() == 1) {
						sendUnit = sendUnits.stream().findFirst().get();
						sendUnitId = sendUnit.getUnitId();
					}
					if (sendUnits.size() > 1) {
//						CustomerInfo customerInfo = customerInfoService.queryByDealerNumber(sendDealerNumber);
//						if (customerInfo == null) {
//							throw new RuntimeException("未查询到发货单位经销商编码");
//						}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//						sendUnits = sendUnits.stream().filter(unit -> unit.getSuperorCorpId().equals(customerInfo.getCustomerCorpId())).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(sendUnits)) {
							if (sendUnits.size() == 1) {
								sendUnit = sendUnits.stream().findFirst().get();
								sendUnitId = sendUnit.getUnitId();
							}
							if (sendUnits.size() > 1) {
								throw new RuntimeException(String.format("当前发货单位经销商编码: %s存在多个同名发货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
							}
						} else {
							throw new RuntimeException(String.format("未查询到与发货单位经销商编码: %s关联的发货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
						}
					}
				}
			} catch (Exception e) {
				log.error("{}", e);
				throw e;
			}

			if (sendUnitId == null) {
				throw new RuntimeException(String.format("未找到发货单位：%s", sendUnitName));
			}
			if (recvUnitId == null) {
				throw new RuntimeException(String.format("未找到收货单位：%s", recvUnitName));
			}

			for (CustomerReconciliationDetail detail : details) {
				if (detail.getCarVin().equals(carVin) &&
					detail.getDesignatedLoadingDate().equals(designatedLoadingDate) &&
					detail.getRecvCityId().equals(recvUnit.getCityId()) &&
					detail.getSendCityId().equals(sendUnit.getCityId())) {

					validDetails.add(detail);
				}
			}
		}

		invalidDetails = details.stream().filter(detail -> !validDetails.contains(detail)).collect(Collectors.toList());

		List<String> detailCarVins = details.stream().map(CustomerReconciliationDetail::getCarVin).collect(Collectors.toList());
		List<String> notExistedCarVins = excelCarVins.stream().filter(excelCarVin -> !detailCarVins.contains(excelCarVin)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(notExistedCarVins)) {
			// 删除EXCEL中未出现车架号记录
			// invalidDetails.forEach(invalidDetail -> detailService.deleteById(invalidDetail.getReconciliationDetailId()));

			throw new RuntimeException(String.format("未能在当前对账单下找到以下车架号：%s", String.join(",", notExistedCarVins)));
		}

		Integer carVinCount = validDetails.size();


		// 主表，累计扣款
		BigDecimal totalDeductionAmount = new BigDecimal(0);
		// 主表，扣款原因
		String totalDeductionReason = null;

		// 主表，红蓝金额
		BigDecimal redAmount   = new BigDecimal(0);
		BigDecimal blueAmount  = new BigDecimal(0);
		BigDecimal totalAmount = new BigDecimal(0);

		List<CustomerReconciliationDetail> updateDetails = new ArrayList<>();
		List<CustomerReconciliationDeduction> addDeductions = new ArrayList<>();
		// i = 2，从第三行开始读取 & 解析数据
		for (int rowNumber = 2; rowNumber <= rowCount; rowNumber++) {

			Cell[] cells = new Cell[colCount];
			for (int colNumber = 0; colNumber < colCount; colNumber++) {
				cells[colNumber] = sheet.getRow(rowNumber).getCell(colNumber);
			}

			DateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
			Date date = null;
			try {
				date = format.parse(cells[1].getDateCellValue().toString());
			} catch (ParseException e) {
				log.error("{}", e);
				throw new RuntimeException("EXCEL中日期格式应为2018-01-01");
			}

			String recvDealerNumber = formatter.formatCellValue(cells[2]);
			String sendDealerNumber = formatter.formatCellValue(cells[3]);
			String recvUnitName     = formatter.formatCellValue(cells[4]);
			String sendUnitName     = formatter.formatCellValue(cells[5]);

			Long sendUnitId = null;
			Long recvUnitId = null;
			try {
				List<Unit> recvUnits = unitService.queryByName(recvUnitName);
				List<Unit> sendUnits = unitService.queryByName(sendUnitName);
				if (CollectionUtils.isNotEmpty(recvUnits)) {
					if (recvUnits.size() == 1) {
						recvUnitId = recvUnits.stream().findFirst().get().getUnitId();
					}
					if (recvUnits.size() > 1) {
//						CustomerInfo customerInfo = customerInfoService.queryByDealerNumber(recvDealerNumber);
//						if (customerInfo == null) {
//							throw new RuntimeException("未查询到收货单位经销商编码");
//						}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//						recvUnits = recvUnits.stream().filter(unit -> unit.getSuperorCorpId() == customerInfo.getCustomerCorpId()).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(recvUnits)) {
							if (recvUnits.size() == 1) {
								recvUnitId = recvUnits.stream().findFirst().get().getUnitId();
							}
							if (recvUnits.size() > 1) {
								throw new RuntimeException(String.format("当前收货单位经销商编码: %s存在多个同名收货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
							}
						} else {
							throw new RuntimeException(String.format("未查询到与收货单位经销商编码: %s关联的收货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
						}
					}
				}
				if (CollectionUtils.isNotEmpty(sendUnits)) {
					if (sendUnits.size() == 1) {
						sendUnitId = sendUnits.stream().findFirst().get().getUnitId();
					}
					if (sendUnits.size() > 1) {
//						CustomerInfo customerInfo = customerInfoService.queryByDealerNumber(sendDealerNumber);
//						if (customerInfo == null) {
//							throw new RuntimeException("未查询到发货单位经销商编码");
//						}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//						sendUnits = sendUnits.stream().filter(unit -> unit.getSuperorCorpId().equals(customerInfo.getCustomerCorpId())).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(sendUnits)) {
							if (sendUnits.size() == 1) {
								sendUnitId = sendUnits.stream().findFirst().get().getUnitId();
							}
							if (sendUnits.size() > 1) {
								throw new RuntimeException(String.format("当前发货单位经销商编码: %s存在多个同名发货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
							}
						} else {
							throw new RuntimeException(String.format("未查询到与发货单位经销商编码: %s关联的发货单位: %s，请维护客户定制信息后重试。", recvDealerNumber, recvUnitName));
						}
					}
				}
			} catch (Exception e) {
				log.error("{}", e);
				throw e;
			}

			if (sendUnitId == null) {
				throw new RuntimeException(String.format("未找到发货单位：%s", sendUnitName));
			}
			if (recvUnitId == null) {
				throw new RuntimeException(String.format("未找到收货单位：%s", recvUnitName));
			}

			String carVin = formatter.formatCellValue(cells[0]);
			String designatedLoadingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);

			params = new HashMap<>();
			params.put("reconciliationId", customerReconciliation.getReconciliationId());
			params.put("carVin", carVin);
			params.put("designatedLoadingDate", designatedLoadingDate);
			params.put("sendUnitId", sendUnitId);
			params.put("recvUnitId", recvUnitId);

			CustomerReconciliationDetail customerReconciliationDetail = detailService.queryForFeedback(params);
			if (customerReconciliationDetail == null) {
				throw new RuntimeException(String.format("未找到车架号：%s 指定装车日期：%s 收发货单位：%s/%s 的对账记录", carVin, designatedLoadingDate, recvUnitName, sendUnitName));
			}

			List<CustomerReconciliationDeduction> deductionList = reconciliationDeductionService.selectDeductionsByReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			deductionList.forEach(deduction -> reconciliationDeductionService.physicalDelete(deduction.getReconciliationDeductionId()));

			// 子表，扣款总额
			BigDecimal detailDeductionAmount = new BigDecimal(0);
			StringBuilder deductionReason = new StringBuilder();
			int deductionColNumber = 7;
			for (int i = deductionColNumber; i < colCount; i++) {

				BigDecimal deductionAmount = new BigDecimal(formatter.formatCellValue(cells[i]));
				CustomerReconciliationDeduction deductionModel = new CustomerReconciliationDeduction();
				deductionModel.setReconciliationId(customerReconciliation.getReconciliationId());
				deductionModel.setReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
				deductionModel.setDeductionId(deductions.get(formatter.formatCellValue(sheet.getRow(1).getCell(i))));
				deductionModel.setDeductionAmount(deductionAmount);
				deductionModel.setCreator(env.getUser().getUserId());
				deductionModel.setCorpId(env.getUser().getCorpId());
				deductionModel.setCreateDate(new Date());
				deductionModel.setFlag(0);
				// reconciliationDeductionService.add(deductionModel);
				addDeductions.add(deductionModel);

				detailDeductionAmount = detailDeductionAmount.add(deductionAmount);
				// 取EXCEL表头扣款项目
				if (i == colCount - 1) {
					deductionReason.append(String.format("%s", formatter.formatCellValue(sheet.getRow(1).getCell(i))));
				} else {
					deductionReason.append(String.format("%s, ", formatter.formatCellValue(sheet.getRow(1).getCell(i))));
				}
			}

			BigDecimal settlementPrice = new BigDecimal(formatter.formatCellValue(cells[6]));
			settlementPrice = settlementPrice.subtract(detailDeductionAmount);
			BigDecimal hedgeAmount = settlementPrice.subtract(customerReconciliationDetail.getQuotationPrice());
			totalAmount = totalAmount.add(settlementPrice);

			if (hedgeAmount.compareTo(BigDecimal.ZERO) > 0) {
				blueAmount = blueAmount.add(hedgeAmount);
			}
			if (hedgeAmount.compareTo(BigDecimal.ZERO) < 0) {
				redAmount = redAmount.add(hedgeAmount);
			}

			totalDeductionReason = deductionReason.toString();

			CustomerReconciliationDetail detailModel = new CustomerReconciliationDetail();
			detailModel.setReconciliationDetailId(customerReconciliationDetail.getReconciliationDetailId());
			detailModel.setHedgeAmount(hedgeAmount);
			detailModel.setSettlementPrice(settlementPrice);
			detailModel.setDeductionAmount(detailDeductionAmount);
			detailModel.setDeductionReason(deductionReason.toString());
			updateDetails.add(detailModel);
		}

		List<CustomerReconciliationDeduction> reconciliationDeductions = reconciliationDeductionService.selectDeductionsByReconciliationId(customerReconciliation.getReconciliationId());
		if (CollectionUtils.isNotEmpty(reconciliationDeductions)) {
			List<Long> reconciliationDeductionIds = reconciliationDeductions.stream().map(CustomerReconciliationDeduction::getReconciliationDeductionId).collect(Collectors.toList());
			reconciliationDeductionService.physicalDelete(reconciliationDeductionIds);
		}

		updateDetails.forEach(updateDetail -> detailService.updateSelective(updateDetail));
		addDeductions.forEach(updateDeduction -> reconciliationDeductionService.createWithoutUpdate(updateDeduction));

		List<CustomerReconciliationDetail> detailList = detailService.selectByReconciliationId(customerReconciliation.getReconciliationId());
		if (CollectionUtils.isNotEmpty(detailList)) {
			totalAmount = new BigDecimal(0);
			for (CustomerReconciliationDetail detail : detailList) {
				totalAmount = totalAmount.add(detail.getSettlementPrice());
				totalDeductionAmount = totalDeductionAmount.add(detail.getDeductionAmount());
			}
		}

		CustomerReconciliation model = new CustomerReconciliation();
		model.setReconciliationId(customerReconciliation.getReconciliationId());
		model.setRedAmount(redAmount);
		model.setBlueAmount(blueAmount);
		model.setCarVinCount(carVinCount);
		model.setTotalAmount(totalAmount);
		model.setDeductionTotalAmount(totalDeductionAmount);
		model.setDeductionReason(totalDeductionReason);
		model.setCustomerFeedbackDate(new Date());
		updateSelective(model);
	}

	@Transactional
	@Override
	public void logicalDelete(Long reconciliationId) {

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation == null) {
			throw new RuntimeException("未找到对账单，请重新选择后重试。");
		}
		if (customerReconciliation.getBillStatus() != BillStatus.Drafted.getIndex()) {
			throw new RuntimeException("当前单据未处于制单状态，无法删除。");
		}
		if (customerReconciliation.getConfirmor() != null) {
			throw new RuntimeException("当前单据已结算，无法删除。");
		}
		List<CustomerReconciliationDetail> details = detailService.selectByReconciliationId(reconciliationId);
		List<CustomerReconciliationDeduction> deductions = reconciliationDeductionService.selectDeductionsByReconciliationId(reconciliationId);

		try {
			reconciliationMapper.logicalDelete(reconciliationId);

			if (CollectionUtils.isNotEmpty(details)) {
				details.forEach(detail -> detailService.logicalDelete(detail.getReconciliationDetailId()));
			}

			if (CollectionUtils.isNotEmpty(deductions)) {
				deductions.forEach(deduction -> reconciliationDeductionService.logicalDelete(deduction.getReconciliationDeductionId()));
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateDeductionReasonsForReconciliation(Long reconciliationId) {

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation == null) {
			throw new RuntimeException(String.format("ID: %d记录不存在", reconciliationId));
		}

		List<CustomerReconciliationDeduction> deductions = reconciliationDeductionService.selectDeductionsByReconciliationId(reconciliationId);

		String deductionReason = "";
		if (CollectionUtils.isNotEmpty(deductions)) {
			List<String> deductionReasons = deductions.stream().map(CustomerReconciliationDeduction::getRefDeductionName).distinct().collect(Collectors.toList());
			deductionReason = String.join(", ", deductionReasons);
		}

		CustomerReconciliation model = new CustomerReconciliation();
		model.setReconciliationId(customerReconciliation.getReconciliationId());
		model.setDeductionReason(deductionReason);
		updateSelective(model);
	}

	@Transactional
	@Override
	public void updateAmountForReconciliation(Long reconciliationId) {

		CustomerReconciliation customerReconciliation = queryById(reconciliationId);
		if (customerReconciliation == null) {
			throw new RuntimeException(String.format("ID: %d记录不存在", reconciliationId));
		}

		List<CustomerReconciliationDetail> details = detailService.selectByReconciliationId(reconciliationId);
		if (CollectionUtils.isNotEmpty(details)) {

			BigDecimal redAmount = new BigDecimal(0);
			BigDecimal blueAmount = new BigDecimal(0);
			BigDecimal totalAmount = new BigDecimal(0);
			BigDecimal deductionTotalAmount = new BigDecimal(0);

			for (CustomerReconciliationDetail detail : details) {

				BigDecimal hedgeAmount = detail.getHedgeAmount();
				if (hedgeAmount != null) {
					if (hedgeAmount.compareTo(BigDecimal.ZERO) > 0) {
						blueAmount = blueAmount.add(hedgeAmount);
					}
					if (hedgeAmount.compareTo(BigDecimal.ZERO) < 0) {
						redAmount = redAmount.add(hedgeAmount);
					}
				}

				BigDecimal settlementPrice = detail.getSettlementPrice();
				if (settlementPrice != null) {
					totalAmount = totalAmount.add(settlementPrice);
				}
			}

			List<CustomerReconciliationDeduction> deductions = reconciliationDeductionService.selectDeductionsByReconciliationId(reconciliationId);
			if (CollectionUtils.isNotEmpty(deductions)) {
				for (CustomerReconciliationDeduction deduction : deductions) {
					deductionTotalAmount = deductionTotalAmount.add(deduction.getDeductionAmount());
				}
			}

			CustomerReconciliation model = new CustomerReconciliation();
			model.setReconciliationId(customerReconciliation.getReconciliationId());
			model.setRedAmount(redAmount);
			model.setBlueAmount(blueAmount);
			model.setTotalAmount(totalAmount);
			model.setDeductionTotalAmount(deductionTotalAmount);
			updateSelective(model);
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
	{
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
