package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.catalyst.utils.DateUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.*;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.QuotationMapper;
import com.tilchina.timp.model.City;
import com.tilchina.timp.model.Quotation;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.model.QuotationPrice;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.util.ValidateUtil;
import com.tilchina.timp.vo.QuotationVO;
import com.tilchina.timp.vo.UniversalExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* 客户报价
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class QuotationServiceImpl extends BaseServiceImpl<Quotation> implements QuotationService {

	@Autowired
	private QuotationMapper quotationMapper;

	@Autowired
	private QuotationDetailService detailService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private CityService cityService;

	@Autowired
	private UnitService unitService;

	@Autowired
	private ContractService contractService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CarTypeService carTypeService;
	
	@Autowired
	private QuotationDetailService quotationDetailService;

	@Autowired
	private QuotationPriceService quotationPriceService;

	@Override
	protected BaseMapper<Quotation> getMapper() {
		return quotationMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Quotation quotation) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkString("NO", "quotationNumber", "报价编号", quotation.getQuotationNumber(), 20));
		s.append(CheckUtils.checkInteger("NO", "quotationType", "报价类型", quotation.getQuotationType(), 10));
		s.append(CheckUtils.checkString("YES", "quotationName", "报价名称", quotation.getQuotationName(), 50));
		s.append(CheckUtils.checkString("YES", "quotationFile", "报价文件", quotation.getQuotationFile(), 100));
		s.append(CheckUtils.checkInteger("NO", "jobType", "作业类型", quotation.getJobType(), 10));
		s.append(CheckUtils.checkDate("NO", "effectiveDate", "生效日期", quotation.getEffectiveDate()));
		s.append(CheckUtils.checkDate("YES", "expirationDate", "失效日期", quotation.getExpirationDate()));
		s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", quotation.getCreateDate()));
		s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", quotation.getCheckDate()));
		s.append(CheckUtils.checkInteger("NO", "billStatus", "制单状态", quotation.getBillStatus(), 10));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", quotation.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Quotation quotation) {
		StringBuilder s = checkNewRecord(quotation);
		s.append(CheckUtils.checkPrimaryKey(quotation.getQuotationId()));
		return s;
	}

	@Override
	public PageInfo<Quotation> queryList(Map<String, Object> map, int pageNum, int pageSize) {

		try {

			PageHelper.startPage(pageNum, pageSize);
			List<Quotation> quotations = quotationMapper.selectList(map);
			if (CollectionUtils.isNotEmpty(quotations)) {
				quotations.forEach(quotation -> {
					List<QuotationDetail> quotationDetails = detailService.selectDetailsByQuotationId(quotation.getQuotationId());
					quotation.setDetails(quotationDetails);
				});
			}
			return new PageInfo(quotations);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Quotation> queryList(Map<String, Object> map) {

		try {
			List<Quotation> quotations = quotationMapper.selectList(map);
			if (quotations != null && quotations.size() > 0) {
				quotations.forEach(quotation -> {
					List<QuotationDetail> quotationDetails = detailService.selectDetailsByQuotationId(quotation.getQuotationId());
					quotation.setDetails(quotationDetails);
				});
			}
			return quotations;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(Quotation quotation) {

		try {
			StringBuilder sb;
			Environment env = Environment.getEnv();
			quotation.setCreator(env.getUser().getUserId());
			quotation.setCreateDate(new Date());
			quotation.setCorpId(env.getUser().getCorpId());
			quotation.setBillStatus(0);
			sb = checkNewRecord(quotation);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			quotationMapper.insertSelective(quotation);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	// 订单模块查询报价ID以及交期用
	@Override
	public Quotation selectByQuotationVO(QuotationVO quotationVO) {

		try {
			return quotationMapper.selectByQuotationVO(quotationVO);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	// 审核
	@Override
	public void setDocumentsChecked(Long quotationId) {

		try {
			Environment environment = Environment.getEnv();
			Quotation quotation = queryById(quotationId);
			if (quotation.getBillStatus() == null || quotation.getBillStatus() == 0) {
				quotation.setBillStatus(1);
				quotation.setChecker(environment.getUser().getUserId());
				quotation.setCheckDate(new Date());
				quotationMapper.updateCheckStateByPrimaryKey(quotation);
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	// 取消审核
	@Override
	public void setDocumentsUnchecked(Long quotationId) {

		try {
			Quotation quotation = queryById(quotationId);
			if (quotation.getBillStatus() == null || quotation.getBillStatus() == 1) {
				quotation.setBillStatus(0);
				quotation.setChecker(null);
				quotation.setCheckDate(null);
				quotationMapper.updateCheckStateByPrimaryKey(quotation);
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void importExcel(Long customerId,
	                        Long vendorCorpId,
	                        Integer quotationType,
	                        Long contractId,
	                        String effectiveDate,
	                        String expirationDate,
	                        Integer templateType,
	                        Long carBrandId,
	                        Long carTypeId,
	                        Long carModelId,
	                        Integer quotationPlan,
	                        Integer jobType,
	                        MultipartFile multipartFile) throws Exception {

		if (customerId == null || corpService.queryById(customerId) == null) {
			throw new BusinessException("9005", "客户");
		}
		if (vendorCorpId == null || corpService.queryById(vendorCorpId) == null) {
			throw new BusinessException("9005", "承运商");
		}
		if (carBrandId == null || brandService.queryById(carBrandId) == null) {
			throw new BusinessException("9005", "品牌");
		}
		if (multipartFile == null) {
			throw new BusinessException("9005", "导入文件");
		}


		// 检查模板类型是否为预定义类型（ 0:保时捷 1:奔驰 2:丰田 ）
		switch (templateType) {
			case 0: break;
			case 1: break;
			case 2: break;
			default: throw new BusinessException("9008", "模板类型");
		}

		// 检查报价类型是否为预定义类型（ 0:合同价 1:预估价 ）
		switch (quotationType) {
			case 0:
				if (contractId == null || contractService.queryById(contractId) == null) {
					throw new BusinessException("9008", "合同");
				}
				break;

			case 1: break;
			default: throw new BusinessException("9008", "报价类型");
		}

		switch (quotationPlan) {
			case 0: break;
			case 1: break;
			case 2: break;
			default: throw new BusinessException("9008", "报价方案");
		}

		switch (jobType) {
			case 0: break;
			case 1: break;
			case 2: break;
			case 3: break;
			case 4: break;
			case 5: break;
			case 6: break;
			case 7: break;
			default: throw new BusinessException("9008", "作业类型");
		}

		// 将用户上传的文件保存到本地，并取得其本地路径
		String filePath;
		try {
			filePath = FileUtil.uploadFile(multipartFile, "Porsche");
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		Quotation quotation = new Quotation();
		try {
			Environment env = Environment.getEnv();
			quotation.setQuotationNumber(String.format("CQ%s", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
			quotation.setQuotationType(quotationType);
			quotation.setContractId(contractId);
			quotation.setCustomerId(customerId);
			quotation.setVendorCorpId(vendorCorpId);
			quotation.setQuotationName(multipartFile.getOriginalFilename());
			quotation.setQuotationFile(filePath);
			quotation.setEffectiveDate(DateUtils.toDate(effectiveDate));
			quotation.setExpirationDate(DateUtils.toDate(expirationDate));
			quotation.setCreator(env.getUser().getUserId());
			quotation.setCreateDate(new Date());
			quotation.setCorpId(env.getUser().getCorpId());
			quotation.setBillStatus(BillStatus.Drafted.getIndex());
			quotation.setJobType(jobType);
			quotation.setFlag(FlagStatus.No.getIndex());

			StringBuilder sb = checkNewRecord(quotation);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException(String.format("数据检查失败：%s", sb.toString()));
			}

			quotationMapper.insertSelective(quotation);
			List<QuotationDetail> quotationDetails = parseExcel(templateType, filePath, carBrandId, carTypeId, carModelId, quotationPlan, jobType);
			quotationDetails.forEach(quotationDetail -> {
				quotationDetail.setQuotationId(quotation.getQuotationId());
				detailService.add(quotationDetail);

				List<QuotationPrice> prices = quotationDetail.getPrices();
				if (CollectionUtils.isNotEmpty(prices)) {
					prices.forEach(price -> {
						price.setQuotationId(quotation.getQuotationId());
						price.setQuotationDetailId(quotationDetail.getQuotationDetailId());
						quotationPriceService.add(price);
					});
				}
			});
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public Quotation parseQuotationForArea(Workbook workbook, Integer quotationPlan, Integer jobType) {
		Sheet sheet = workbook.getSheetAt(0);
		String customer     = Optional.ofNullable(sheet.getRow(0).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		String vendor       = Optional.ofNullable(sheet.getRow(1).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		Date effectiveDate  = Optional.ofNullable(sheet.getRow(2).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();
		Date expirationDate = Optional.ofNullable(sheet.getRow(3).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();

		Long customerId = null;
		Long vendorCorpId = null;

		List<Long> customerIds = corpService.queryByName(customer);
		if (customerIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下客户: %s", customer));
		}
		if (customerIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下客户: %s", customer));
		}
		if (customerIds.size() == 1) {
			customerId = customerIds.stream().findFirst().get();
		}

		List<Long> vendorCorpIds = corpService.queryByName(vendor);
		if (vendorCorpIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() == 1) {
			vendorCorpId = vendorCorpIds.stream().findFirst().get();
		}

		Environment env = Environment.getEnv();
		Quotation quotation = new Quotation();
		quotation.setQuotationNumber(String.format("CQ%s", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
		quotation.setCustomerId(customerId);
		quotation.setVendorCorpId(vendorCorpId);
		quotation.setJobType(jobType);
		quotation.setEffectiveDate(effectiveDate);
		quotation.setExpirationDate(expirationDate);
		quotation.setCreator(env.getUser().getUserId());
		quotation.setCreateDate(new Date());
		quotation.setCorpId(env.getUser().getCorpId());
		quotation.setFlag(0);

		List<QuotationDetail> quotationDetails = parseExcelForArea(workbook);
		quotationDetails.forEach(quotationDetail -> quotationDetail.setQuotationPlan(quotationPlan));
		quotation.setDetails(quotationDetails);

		return quotation;
	}

	@Transactional
	@Override
	public Quotation parseQuotationForShortBarge(Workbook workbook, Integer quotationPlan, Integer jobType) {
		Sheet sheet = workbook.getSheetAt(0);
		String customer     = Optional.ofNullable(sheet.getRow(0).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		String vendor       = Optional.ofNullable(sheet.getRow(1).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		Date effectiveDate  = Optional.ofNullable(sheet.getRow(2).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();
		Date expirationDate = Optional.ofNullable(sheet.getRow(3).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();

		Long customerId = null;
		Long vendorCorpId = null;

		List<Long> customerIds = corpService.queryByName(customer);
		if (customerIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下客户: %s", customer));
		}
		if (customerIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下客户: %s", customer));
		}
		if (customerIds.size() == 1) {
			customerId = customerIds.stream().findFirst().get();
		}

		List<Long> vendorCorpIds = corpService.queryByName(vendor);
		if (vendorCorpIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() == 1) {
			vendorCorpId = vendorCorpIds.stream().findFirst().get();
		}

		Environment env = Environment.getEnv();
		Quotation quotation = new Quotation();
		quotation.setQuotationNumber(String.format("CQ%s", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
		quotation.setCustomerId(customerId);
		quotation.setVendorCorpId(vendorCorpId);
		quotation.setJobType(jobType);
		quotation.setEffectiveDate(effectiveDate);
		quotation.setExpirationDate(expirationDate);
		quotation.setCreator(env.getUser().getUserId());
		quotation.setCreateDate(new Date());
		quotation.setCorpId(env.getUser().getCorpId());
		quotation.setFlag(0);

		List<QuotationDetail> quotationDetails = parseExcelForShortBarge(workbook);
		quotationDetails.forEach(quotationDetail -> quotationDetail.setQuotationPlan(quotationPlan));
		quotation.setDetails(quotationDetails);

		return quotation;
	}

	@Transactional
	@Override
	public void importUniversalExcel(Integer quotationType, Integer quotationPlan, Long contractId, Integer jobType, MultipartFile multipartFile) throws Exception {

		String filePath = FileUtil.uploadExcel(multipartFile);
		Workbook workbook = ExcelUtil.getWorkbook(filePath);

		// 用于判断模板类型和作业类型是否匹配
		Sheet sheet = workbook.getSheetAt(0);
		String sendCity     = Optional.ofNullable(sheet.getRow(4).getCell(0)).orElseThrow(() -> new RuntimeException("导入文件内容有误，请检查后重试。")).getStringCellValue();
		switch (jobType) {
			case 0: if (!sendCity.equals("出发城市")) throw new RuntimeException(String.format("%s对应的导入模板为市到市模板", JobType.getName(jobType))); break;
			case 1: if (!sendCity.equals("城市")) throw new RuntimeException(String.format("%s对应的导入模板为市到市模板", JobType.getName(jobType))); break;
			case 2: if (!sendCity.equals("发货单位")) throw new RuntimeException(String.format("%s对应的导入模板为市到市模板", JobType.getName(jobType))); break;
		}

		Quotation quotation = new Quotation();
		switch (jobType) {
			case 0: quotation = parseQuotationForLongHaul(workbook, quotationPlan, jobType); break;
			case 1: quotation = parseQuotationForArea(workbook, quotationPlan, jobType); break;
			case 2: quotation = parseQuotationForShortBarge(workbook, quotationPlan, jobType); break;
		}

		if (quotationType == QuotationType.ContractPrice.getIndex()) {
			quotation.setContractId(contractId);
		}
		quotation.setQuotationName(multipartFile.getOriginalFilename());
		quotation.setQuotationFile(filePath);
		quotation.setQuotationType(quotationType);
		add(quotation);

		List<QuotationDetail> quotationDetails = quotation.getDetails();
		for (QuotationDetail quotationDetail : quotationDetails) {
			quotationDetail.setQuotationId(quotation.getQuotationId());
			detailService.add(quotationDetail);

			List<QuotationPrice> quotationPrices = quotationDetail.getPrices();
			for (QuotationPrice quotationPrice : quotationPrices) {
				quotationPrice.setQuotationId(quotation.getQuotationId());
				quotationPrice.setQuotationDetailId(quotationDetail.getQuotationDetailId());
			}
			quotationPriceService.add(quotationPrices);
		}
	}

	@Transactional
	@Override
	public void importExcelV2(Long customerId, Long vendorCorpId, Integer quotationType, Long contractId, String effectiveDate, Integer templateType, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType, MultipartFile multipartFile) throws Exception {

		ValidateUtil.validate(customerId, corpService, "客户");
		ValidateUtil.validate(vendorCorpId, corpService, "承运商");
		ValidateUtil.validate(carBrandId, brandService, "车辆品牌");

		String filePath = FileUtil.uploadExcel(multipartFile);

		Environment env = Environment.getEnv();
		Quotation quotation = new Quotation();
		quotation.setQuotationNumber(String.format("CQ%s", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
		quotation.setQuotationType(quotationType);
		quotation.setContractId(contractId);
		quotation.setCustomerId(customerId);
		quotation.setVendorCorpId(vendorCorpId);
		quotation.setQuotationName(multipartFile.getOriginalFilename());
		quotation.setQuotationFile(filePath);
		quotation.setJobType(jobType);
		quotation.setCreator(env.getUser().getUserId());
		quotation.setCreateDate(new Date());
		quotation.setCorpId(env.getUser().getCorpId());
		quotation.setFlag(FlagStatus.No.getIndex());
		add(quotation);


		List<QuotationDetail> quotationDetails = parseExcelV2(templateType, filePath, carBrandId, carTypeId, carModelId, quotationPlan, jobType);
		quotationDetails.forEach(quotationDetail -> {
			quotationDetail.setQuotationId(quotation.getQuotationId());
			detailService.add(quotationDetail);

			List<QuotationPrice> prices = quotationDetail.getPrices();
			if (CollectionUtils.isNotEmpty(prices)) {
				prices.forEach(price -> {
					price.setQuotationId(quotation.getQuotationId());
					price.setQuotationDetailId(quotationDetail.getQuotationDetailId());
					quotationPriceService.add(price);
				});
			}
		});
	}

	@Transactional
	@Override
	public void deleteById(Long id) {

		try {
			quotationMapper.deleteByPrimaryKey(id);
			List<QuotationDetail> quotationDetails = detailService.selectDetailsByQuotationId(id);
			if (quotationDetails != null && quotationDetails.size() > 0) {
				quotationDetails.forEach(detail -> detailService.deleteById(detail.getQuotationDetailId()));
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	// 根据报价类型以及EXCEL文件版本解析
	private List<QuotationDetail> parseExcel(Integer templateType, String filePath, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType) throws Exception{

		InputStream fileStream;
		Workbook workbook;
		try {
			fileStream = new FileInputStream(filePath);

			if (filePath.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fileStream);
			} else if (filePath.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fileStream);
			} else {
				throw new BusinessException("Unsupported file type.");
			}

		} catch (FileNotFoundException e) {
			log.error("{}", e);
			throw e;
		} catch (IOException e) {
			log.error("{}", e);
			throw e;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		List<QuotationDetail> quotationDetails = null;
		switch (templateType) {
			case 0: quotationDetails = parseExcelForPorsche(workbook, carBrandId, carTypeId, carModelId, quotationPlan, jobType); break;
			case 1: quotationDetails = parseExcelForMercedesBenz(workbook); break;
			case 2: quotationDetails = parseExcelForToyota(workbook, quotationPlan); break;
		}

		return quotationDetails;
	}

	private List<QuotationDetail> parseExcelV2(Integer templateType, String filePath, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType) throws Exception {

		Workbook workbook;
		InputStream fileStream = new FileInputStream(filePath);

		if (filePath.endsWith(".xls")) {
			workbook = new HSSFWorkbook(fileStream);
		} else if (filePath.endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(fileStream);
		} else {
			throw new RuntimeException("请检查上传文件是否为以xls或xlsx结尾的EXCEL文件后重试。");
		}

		List<QuotationDetail> quotationDetails = new ArrayList<>();
		switch (templateType) {
			case 0: quotationDetails = parseExcelForPorsche(workbook, carBrandId, carTypeId, carModelId, quotationPlan, jobType); break;
			case 1: quotationDetails = parseExcelForMercedesBenz(workbook); break;
			case 2: quotationDetails = parseExcelForToyota(workbook, quotationPlan); break;
			case 3: quotationDetails = parseExcelForUniversal(workbook, jobType); break;
		}

		return quotationDetails;
	}

	@Transactional
	@Override
	public List<QuotationDetail> parseExcelForUniversal(Workbook workbook, Integer jobType) {

		List<QuotationDetail> quotationDetails = new ArrayList<>();
		switch (jobType) {
			case 0: quotationDetails = parseExcelForLongHaul(workbook); break;
			case 1: quotationDetails = parseExcelForArea(workbook); break;
			case 2: quotationDetails = parseExcelForShortBarge(workbook); break;
		}
		return quotationDetails;
	}

	@Transactional
	@Override
	public List<QuotationDetail> parseExcelForLongHaul(Workbook workbook) {
		List<QuotationDetail> quotationDetails = new ArrayList<>();
		try {
			Environment env = Environment.getEnv();
			Sheet sheet = workbook.getSheetAt(0);

			int startRow = 5;
			int colCount = 5;
			int rowCount = ExcelUtil.getLastRowNumber(workbook);

			StringBuilder errorMsg = new StringBuilder();
			List<UniversalExcelVO> excelVOList = new ArrayList<>();
			String[] headerValues = { "出发城市", "到达城市", "品牌", "车型类别", "金额" };
			Map<Integer, Boolean> nullable = new HashMap(){{
				put(0, false);
				put(1, false);
				put(2, false);
				put(3, true);
				put(4, false);
				put(5, false);
			}};

			for (int i = startRow; i <= rowCount; i++) {

				String[] cellValues = new String[colCount + 1];
				for (int j = 0; j <= colCount; j++) {

					Cell cell = sheet.getRow(i).getCell(j);
					if (cell == null) {
						continue;
					}
					cell.setCellType(CellType.STRING);
					cellValues[j] = cell.getStringCellValue();

					if (!nullable.get(j) && StringUtils.isBlank(cellValues[j])) {
						errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
					}
				}

				UniversalExcelVO excelVO = new UniversalExcelVO();
				excelVO.setSendCityName(cellValues[0]);
				excelVO.setRecvCityName(cellValues[1]);
				excelVO.setCarBrandName(cellValues[2]);
				excelVO.setCarTypeName(cellValues[3]);
				excelVO.setAmount(new BigDecimal(cellValues[4]));
				excelVO.setLeadTime(NumberUtils.toInt(cellValues[5]));
				excelVOList.add(excelVO);
			}

			if (StringUtils.isNotBlank(errorMsg.toString())) {
				throw new RuntimeException(errorMsg.toString());
			}

			List<String> sendCityNames = excelVOList.stream().map(UniversalExcelVO::getSendCityName).distinct().collect(Collectors.toList());
			List<String> recvCityNames = excelVOList.stream().map(UniversalExcelVO::getRecvCityName).distinct().collect(Collectors.toList());
			List<String> cityNames = Stream.of(sendCityNames, recvCityNames).flatMap(Collection::stream).distinct().collect(Collectors.toList());

			errorMsg = new StringBuilder();
			Map<String, Long> cityMap = new HashMap<>();
			List<String> invalidCityNames  = new ArrayList<>();
			List<String> multipleCityNames = new ArrayList<>();

			for (String cityName : cityNames) {
				List<Long> areaIds = cityService.queryByName(cityName, CityType.City);
				if (areaIds.size() < 1) {
					invalidCityNames.add(cityName);
				}
				if (areaIds.size() > 1) {
					multipleCityNames.add(cityName);
				}
				if (areaIds.size() == 1) {
					Long areaId = areaIds.stream().findFirst().get();
					cityMap.put(cityName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidCityNames)) {
				errorMsg.append(String.format("未能在省市区档案中找到以下城市: %s, 请维护档案后重试。", String.join(",", invalidCityNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleCityNames)) {
				errorMsg.append(String.format("在省市区档案中找到多个相同或城市名包含以下城市: %s, 请填写全称或维护档案后重试。", String.join(",", multipleCityNames)));
			}

			List<String> carBrandNames = excelVOList.stream().map(UniversalExcelVO::getCarBrandName).distinct().collect(Collectors.toList());
			Map<String, Long> carBrandMap = new HashMap<>();
			List<String> invalidBrandNames = new ArrayList<>();
			List<String> multipleBrandNames = new ArrayList<>();

			for (String carBrandName : carBrandNames) {
				List<Long> carBrandIds = brandService.queryByName(carBrandName);
				if (carBrandIds == null) {
					invalidBrandNames.add(carBrandName);
					continue;
				}
				if (carBrandIds.size() < 1) {
					invalidBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() > 1) {
					multipleBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() == 1) {
					Long areaId = carBrandIds.stream().findFirst().get();
					carBrandMap.put(carBrandName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidBrandNames)) {
				errorMsg.append(String.format("未能在品牌档案中找到以下品牌: %s, 请维护档案后重试。", String.join(",", invalidBrandNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleBrandNames)) {
				errorMsg.append(String.format("在品牌档案中找到多个相同或品牌名包含以下品牌: %s, 请填写全称或维护档案后重试。", String.join(",", multipleBrandNames)));
			}

			List<String> carTypeNames = excelVOList.stream()
					.filter(excelVO -> StringUtils.isNotBlank(excelVO.getCarTypeName()))
					.map(UniversalExcelVO::getCarTypeName)
					.distinct()
					.collect(Collectors.toList());
			Map<String, Long> carTypeMap = new HashMap<>();
			List<String> invalidTypeNames = new ArrayList<>();
			List<String> multipleTypeNames = new ArrayList<>();

			for (String carTypeName : carTypeNames) {
				List<Long> carTypeIds = carTypeService.queryByName(carTypeName);
				if (carTypeIds.size() < 1) {
					invalidTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() > 1) {
					multipleTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() == 1) {
					Long areaId = carTypeIds.stream().findFirst().get();
					carTypeMap.put(carTypeName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidTypeNames)) {
				errorMsg.append(String.format("未能在车型类别档案中找到以下车型类别: %s, 请维护档案后重试。", String.join(",", invalidTypeNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleTypeNames)) {
				errorMsg.append(String.format("在车型类别档案中找到多个相同或车型类别名包含以下车型类别: %s, 请填写全称或维护档案后重试。", String.join(",", multipleTypeNames)));
			}

			if (StringUtils.isNotBlank(errorMsg)) {
				throw new RuntimeException(errorMsg.toString());
			}

			for (UniversalExcelVO excelVO : excelVOList) {

				QuotationPrice priceModel = new QuotationPrice();
				priceModel.setQuotationAmount(1);
				priceModel.setQuotationPrice(excelVO.getAmount());
				priceModel.setCorpId(env.getUser().getCorpId());
				priceModel.setFlag(FlagStatus.No.getIndex());

				QuotationDetail detailModel = new QuotationDetail();
				detailModel.setLeadTime(excelVO.getLeadTime());
				detailModel.setSendCityId(cityMap.get(excelVO.getSendCityName()));
				detailModel.setRecvCityId(cityMap.get(excelVO.getRecvCityName()));
				detailModel.setCarBrandId(carBrandMap.get(excelVO.getCarBrandName()));
				if (StringUtils.isNotBlank(excelVO.getCarTypeName())) {
					detailModel.setCarTypeId(carTypeMap.get(excelVO.getCarTypeName()));
				}
				detailModel.setPriceDesc(priceModel.toString());
				detailModel.setCorpId(env.getUser().getCorpId());
				detailModel.setFlag(FlagStatus.No.getIndex());
				detailModel.setPrices(new ArrayList(){{add(priceModel);}});
				quotationDetails.add(detailModel);
			}

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
		return quotationDetails;
	}

	@Transactional
	@Override
	public List<QuotationDetail> parseExcelForArea(Workbook workbook) {
		List<QuotationDetail> quotationDetails = new ArrayList<>();
		try {
			Environment env = Environment.getEnv();
			Sheet sheet = workbook.getSheetAt(0);

			int startRow = 5;
			int colCount = 6;
			int rowCount = ExcelUtil.getLastRowNumber(workbook);

			String customer     = Optional.ofNullable(sheet.getRow(0).getCell(1)).orElseThrow(() -> new RuntimeException("导入文件中客户为空，请检查后重试。")).getStringCellValue();
			String vendor       = Optional.ofNullable(sheet.getRow(1).getCell(1)).orElseThrow(() -> new RuntimeException("导入文件中承运商为空，请检查后重试。")).getStringCellValue();
			Date effectiveDate  = Optional.ofNullable(sheet.getRow(2).getCell(1)).orElseThrow(() -> new RuntimeException("导入文件中生效日期为空，请检查后重试。")).getDateCellValue();
			Date expirationDate = Optional.ofNullable(sheet.getRow(3).getCell(1)).orElseThrow(() -> new RuntimeException("导入文件中失效日期为空，请检查后重试。")).getDateCellValue();

			if (StringUtils.isBlank(customer)) {
				throw new RuntimeException("导入文件中客户为空，请检查后重试。");
			}
			if (StringUtils.isBlank(vendor)) {
				throw new RuntimeException("导入文件中承运商为空，请检查后重试。");
			}
			if (StringUtils.isBlank(effectiveDate.toString())) {
				throw new RuntimeException("导入文件中生效日期为空，请检查后重试。");
			}
			if (StringUtils.isBlank(expirationDate.toString())) {
				throw new RuntimeException("导入文件中失效日期为空，请检查后重试。");
			}

			StringBuilder errorMsg = new StringBuilder();
			List<UniversalExcelVO> excelVOList = new ArrayList<>();
			String[] headerValues = { "城市", "出发区域", "到达区域", "品牌", "车型类别", "金额" };
			Map<Integer, Boolean> nullable = new HashMap(){{
				put(0, false);
				put(1, false);
				put(2, false);
				put(3, false);
				put(4, true);
				put(5, false);
				put(6, false);
			}};

			for (int i = startRow; i <= rowCount; i++) {

				String[] cellValues = new String[colCount + 1];
				for (int j = 0; j <= colCount; j++) {

					Cell cell = sheet.getRow(i).getCell(j);
					if (cell == null) {
						continue;
					}
					cell.setCellType(CellType.STRING);
					cellValues[j] = cell.getStringCellValue();

					if (!nullable.get(j) && StringUtils.isBlank(cellValues[j])) {
						errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
					}
				}

				UniversalExcelVO excelVO = new UniversalExcelVO();
				excelVO.setCityName(cellValues[0]);
				excelVO.setSendAreaName(cellValues[1]);
				excelVO.setRecvAreaName(cellValues[2]);
				excelVO.setCarBrandName(cellValues[3]);
				excelVO.setCarTypeName(cellValues[4]);
				excelVO.setAmount(new BigDecimal(cellValues[5]));
				excelVO.setLeadTime(NumberUtils.toInt(cellValues[6]));
				excelVOList.add(excelVO);
			}

			if (StringUtils.isNotBlank(errorMsg.toString())) {
				throw new RuntimeException(errorMsg.toString());
			}

			List<String> sendAreaNames = excelVOList.stream().map(UniversalExcelVO::getSendAreaName).distinct().collect(Collectors.toList());
			List<String> recvAreaNames = excelVOList.stream().map(UniversalExcelVO::getRecvAreaName).distinct().collect(Collectors.toList());
			List<String> areaNames = Stream.of(sendAreaNames, recvAreaNames).flatMap(Collection::stream).distinct().collect(Collectors.toList());

			errorMsg = new StringBuilder();
			Map<String, Long> areaMap = new HashMap<>();
			List<String> invalidAreaNames  = new ArrayList<>();
			List<String> multipleAreaNames = new ArrayList<>();

			for (String areaName : areaNames) {
				List<Long> areaIds = cityService.queryByName(areaName, CityType.District);
				if (areaIds.size() < 1) {
					invalidAreaNames.add(areaName);
				}
				if (areaIds.size() > 1) {
					multipleAreaNames.add(areaName);
				}
				if (areaIds.size() == 1) {
					Long areaId = areaIds.stream().findFirst().get();
					areaMap.put(areaName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidAreaNames)) {
				errorMsg.append(String.format("未能在省市区档案中找到以下城市: %s, 请维护档案后重试。", String.join(",", invalidAreaNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleAreaNames)) {
				errorMsg.append(String.format("在省市区档案中找到多个相同或城市名包含以下城市: %s, 请填写全称或维护档案后重试。", String.join(",", multipleAreaNames)));
			}

			List<String> carBrandNames = excelVOList.stream().map(UniversalExcelVO::getCarBrandName).distinct().collect(Collectors.toList());
			Map<String, Long> carBrandMap = new HashMap<>();
			List<String> invalidBrandNames = new ArrayList<>();
			List<String> multipleBrandNames = new ArrayList<>();

			for (String carBrandName : carBrandNames) {
				List<Long> carBrandIds = brandService.queryByName(carBrandName);
				if (carBrandIds == null) {
					invalidBrandNames.add(carBrandName);
					continue;
				}
				if (carBrandIds.size() < 1) {
					invalidBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() > 1) {
					multipleBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() == 1) {
					Long areaId = carBrandIds.stream().findFirst().get();
					carBrandMap.put(carBrandName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidBrandNames)) {
				errorMsg.append(String.format("未能在品牌档案中找到以下品牌: %s, 请维护档案后重试。", String.join(",", invalidBrandNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleBrandNames)) {
				errorMsg.append(String.format("在品牌档案中找到多个相同或品牌名包含以下品牌: %s, 请填写全称或维护档案后重试。", String.join(",", multipleBrandNames)));
			}

			List<String> carTypeNames = excelVOList.stream()
					.filter(excelVO -> StringUtils.isNotBlank(excelVO.getCarTypeName()))
					.map(UniversalExcelVO::getCarTypeName)
					.distinct()
					.collect(Collectors.toList());
			Map<String, Long> carTypeMap = new HashMap<>();
			List<String> invalidTypeNames = new ArrayList<>();
			List<String> multipleTypeNames = new ArrayList<>();

			for (String carTypeName : carTypeNames) {
				List<Long> carTypeIds = carTypeService.queryByName(carTypeName);
				if (carTypeIds.size() < 1) {
					invalidTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() > 1) {
					multipleTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() == 1) {
					Long areaId = carTypeIds.stream().findFirst().get();
					carTypeMap.put(carTypeName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidTypeNames)) {
				errorMsg.append(String.format("未能在车型类别档案中找到以下车型类别: %s, 请维护档案后重试。", String.join(",", invalidTypeNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleTypeNames)) {
				errorMsg.append(String.format("在车型类别档案中找到多个相同或车型类别名包含以下车型类别: %s, 请填写全称或维护档案后重试。", String.join(",", multipleTypeNames)));
			}

			if (StringUtils.isNotBlank(errorMsg)) {
				throw new RuntimeException(errorMsg.toString());
			}

			for (UniversalExcelVO excelVO : excelVOList) {

				QuotationPrice priceModel = new QuotationPrice();
				priceModel.setQuotationAmount(1);
				priceModel.setQuotationPrice(excelVO.getAmount());
				priceModel.setCorpId(env.getUser().getCorpId());
				priceModel.setFlag(FlagStatus.No.getIndex());

				QuotationDetail detailModel = new QuotationDetail();
				detailModel.setLeadTime(excelVO.getLeadTime());
				detailModel.setSendAreaId(areaMap.get(excelVO.getSendAreaName()));
				detailModel.setRecvAreaId(areaMap.get(excelVO.getRecvAreaName()));
				detailModel.setCarBrandId(carBrandMap.get(excelVO.getCarBrandName()));
				if (StringUtils.isNotBlank(excelVO.getCarTypeName())) {
					detailModel.setCarTypeId(carTypeMap.get(excelVO.getCarTypeName()));
				}
				detailModel.setPriceDesc(priceModel.toString());
				detailModel.setCorpId(env.getUser().getCorpId());
				detailModel.setFlag(FlagStatus.No.getIndex());
				detailModel.setPrices(new ArrayList(){{add(priceModel);}});
				quotationDetails.add(detailModel);
			}

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
		return quotationDetails;
	}

	@Override
	public List<QuotationDetail> parseExcelForShortBarge(Workbook workbook) {
		List<QuotationDetail> quotationDetails = new ArrayList<>();
		try {
			Environment env = Environment.getEnv();
			Sheet sheet = workbook.getSheetAt(0);

			int startRow = 5;
			int colCount = 5;
			int rowCount = ExcelUtil.getLastRowNumber(workbook);

			StringBuilder errorMsg = new StringBuilder();
			List<UniversalExcelVO> excelVOList = new ArrayList<>();
			String[] headerValues = { "发货单位", "收货单位", "品牌", "车型类别", "金额" };
			Map<Integer, Boolean> nullable = new HashMap(){{
				put(0, false);
				put(1, false);
				put(2, false);
				put(3, true);
				put(4, false);
				put(5, false);
			}};

			for (int i = startRow; i <= rowCount; i++) {

				String[] cellValues = new String[colCount + 1];
				for (int j = 0; j <= colCount; j++) {

					Cell cell = sheet.getRow(i).getCell(j);
					if (cell == null) {
						continue;
					}
					cell.setCellType(CellType.STRING);
					cellValues[j] = cell.getStringCellValue();

					if (!nullable.get(j) && StringUtils.isBlank(cellValues[j])) {
						errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
					}
				}

				UniversalExcelVO excelVO = new UniversalExcelVO();
				excelVO.setSendUnitName(cellValues[0]);
				excelVO.setRecvUnitName(cellValues[1]);
				excelVO.setCarBrandName(cellValues[2]);
				excelVO.setCarTypeName(cellValues[3]);
				excelVO.setAmount(new BigDecimal(cellValues[4]));
				excelVO.setLeadTime(NumberUtils.toInt(cellValues[5]));
				excelVOList.add(excelVO);
			}

			if (StringUtils.isNotBlank(errorMsg.toString())) {
				throw new RuntimeException(errorMsg.toString());
			}

			List<String> sendUnitNames = excelVOList.stream().map(UniversalExcelVO::getSendUnitName).distinct().collect(Collectors.toList());
			List<String> recvUnitNames = excelVOList.stream().map(UniversalExcelVO::getRecvUnitName).distinct().collect(Collectors.toList());
			List<String> unitNames = Stream.of(sendUnitNames, recvUnitNames).flatMap(Collection::stream).distinct().collect(Collectors.toList());

			errorMsg = new StringBuilder();
			Map<String, Long> unitMap = new HashMap<>();
			List<String> invalidUnitNames  = new ArrayList<>();
			List<String> multipleUnitNames = new ArrayList<>();

			for (String unitName : unitNames) {
				List<Long> areaIds =  unitService.queryByUnitName(unitName);
				if (areaIds.size() < 1) {
					invalidUnitNames.add(unitName);
				}
				if (areaIds.size() > 1) {
					multipleUnitNames.add(unitName);
				}
				if (areaIds.size() == 1) {
					Long areaId = areaIds.stream().findFirst().get();
					unitMap.put(unitName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidUnitNames)) {
				errorMsg.append(String.format("未能在收发货单位档案中找到以下单位: %s, 请维护档案后重试。", String.join(",", invalidUnitNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleUnitNames)) {
				errorMsg.append(String.format("在省市区收发货单位中找到多个相同或城市名包含以下单位: %s, 请填写全称或维护档案后重试。", String.join(",", multipleUnitNames)));
			}

			List<String> carBrandNames = excelVOList.stream().map(UniversalExcelVO::getCarBrandName).distinct().collect(Collectors.toList());
			Map<String, Long> carBrandMap = new HashMap<>();
			List<String> invalidBrandNames = new ArrayList<>();
			List<String> multipleBrandNames = new ArrayList<>();

			for (String carBrandName : carBrandNames) {
				List<Long> carBrandIds = brandService.queryByName(carBrandName);
				if (carBrandIds == null) {
					invalidBrandNames.add(carBrandName);
					continue;
				}
				if (carBrandIds.size() < 1) {
					invalidBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() > 1) {
					multipleBrandNames.add(carBrandName);
				}
				if (carBrandIds.size() == 1) {
					Long areaId = carBrandIds.stream().findFirst().get();
					carBrandMap.put(carBrandName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidBrandNames)) {
				errorMsg.append(String.format("未能在品牌档案中找到以下品牌: %s, 请维护档案后重试。", String.join(",", invalidBrandNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleBrandNames)) {
				errorMsg.append(String.format("在品牌档案中找到多个相同或品牌名包含以下品牌: %s, 请填写全称或维护档案后重试。", String.join(",", multipleBrandNames)));
			}

			List<String> carTypeNames = excelVOList.stream()
					.filter(excelVO -> StringUtils.isNotBlank(excelVO.getCarTypeName()))
					.map(UniversalExcelVO::getCarTypeName)
					.distinct()
					.collect(Collectors.toList());
			Map<String, Long> carTypeMap = new HashMap<>();
			List<String> invalidTypeNames = new ArrayList<>();
			List<String> multipleTypeNames = new ArrayList<>();

			for (String carTypeName : carTypeNames) {
				List<Long> carTypeIds = carTypeService.queryByName(carTypeName);
				if (carTypeIds.size() < 1) {
					invalidTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() > 1) {
					multipleTypeNames.add(carTypeName);
				}
				if (carTypeIds.size() == 1) {
					Long areaId = carTypeIds.stream().findFirst().get();
					carTypeMap.put(carTypeName, areaId);
				}
			}
			if (CollectionUtils.isNotEmpty(invalidTypeNames)) {
				errorMsg.append(String.format("未能在车型类别档案中找到以下车型类别: %s, 请维护档案后重试。", String.join(",", invalidTypeNames)));
			}
			if (CollectionUtils.isNotEmpty(multipleTypeNames)) {
				errorMsg.append(String.format("在车型类别档案中找到多个相同或车型类别名包含以下车型类别: %s, 请填写全称或维护档案后重试。", String.join(",", multipleTypeNames)));
			}

			if (StringUtils.isNotBlank(errorMsg)) {
				throw new RuntimeException(errorMsg.toString());
			}

			for (UniversalExcelVO excelVO : excelVOList) {

				QuotationPrice priceModel = new QuotationPrice();
				priceModel.setQuotationAmount(1);
				priceModel.setQuotationPrice(excelVO.getAmount());
				priceModel.setCorpId(env.getUser().getCorpId());
				priceModel.setFlag(FlagStatus.No.getIndex());

				QuotationDetail detailModel = new QuotationDetail();
				detailModel.setLeadTime(excelVO.getLeadTime());
				detailModel.setSendUnitId(unitMap.get(excelVO.getSendUnitName()));
				detailModel.setRecvUnitId(unitMap.get(excelVO.getRecvUnitName()));
				detailModel.setCarBrandId(carBrandMap.get(excelVO.getCarBrandName()));
				if (StringUtils.isNotBlank(excelVO.getCarTypeName())) {
					detailModel.setCarTypeId(carTypeMap.get(excelVO.getCarTypeName()));
				}
				detailModel.setPriceDesc(priceModel.toString());
				detailModel.setCorpId(env.getUser().getCorpId());
				detailModel.setFlag(FlagStatus.No.getIndex());
				detailModel.setPrices(new ArrayList(){{add(priceModel);}});
				quotationDetails.add(detailModel);
			}

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
		return quotationDetails;
	}

	@Override
	public Quotation parseQuotationForLongHaul(Workbook workbook, Integer quotationPlan, Integer jobType) {
		Sheet sheet = workbook.getSheetAt(0);
		String customer     = Optional.ofNullable(sheet.getRow(0).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		String vendor       = Optional.ofNullable(sheet.getRow(1).getCell(1)).orElseThrow(() -> new RuntimeException("")).getStringCellValue();
		Date effectiveDate  = Optional.ofNullable(sheet.getRow(2).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();
		Date expirationDate = Optional.ofNullable(sheet.getRow(3).getCell(1)).orElseThrow(() -> new RuntimeException("")).getDateCellValue();

		Long customerId = null;
		Long vendorCorpId = null;

		List<Long> customerIds = corpService.queryByName(customer);
		if (customerIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下客户: %s", customer));
		}
		if (customerIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下客户: %s", customer));
		}
		if (customerIds.size() == 1) {
			customerId = customerIds.stream().findFirst().get();
		}

		List<Long> vendorCorpIds = corpService.queryByName(vendor);
		if (vendorCorpIds.size() < 1) {
			throw new RuntimeException(String.format("未能在公司档案中查询到以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() > 1) {
			throw new RuntimeException(String.format("在公司档案中查询多个相同或公司名称中包含以下承运商: %s", vendor));
		}
		if (vendorCorpIds.size() == 1) {
			vendorCorpId = vendorCorpIds.stream().findFirst().get();
		}

		Environment env = Environment.getEnv();
		Quotation quotation = new Quotation();
		quotation.setQuotationNumber(String.format("CQ%s", DateUtils.format(new Date(), "yyyyMMddHHmmss")));
		quotation.setCustomerId(customerId);
		quotation.setVendorCorpId(vendorCorpId);
		quotation.setJobType(jobType);
		quotation.setEffectiveDate(effectiveDate);
		quotation.setExpirationDate(expirationDate);
		quotation.setCreator(env.getUser().getUserId());
		quotation.setCreateDate(new Date());
		quotation.setCorpId(env.getUser().getCorpId());
		quotation.setFlag(0);

		List<QuotationDetail> quotationDetails = parseExcelForLongHaul(workbook);
		quotationDetails.forEach(quotationDetail -> quotationDetail.setQuotationPlan(quotationPlan));
		quotation.setDetails(quotationDetails);

		return quotation;
	}

	// 保时捷EXCEL文件解析
	@Transactional
	@Override
	public List<QuotationDetail> parseExcelForPorsche(Workbook workbook, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType) {

		Environment env = Environment.getEnv();
		DataFormatter formatter = new DataFormatter();
		List<QuotationDetail> quotationDetails = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);
		Row row;
		// 手动指定需要读取的EXCEL列数
		int colNumber = 8;
		int rowNumber = sheet.getLastRowNum();

		String sendCityName = null;
		try {
			Cell sendCityCell = sheet.getRow(0).getCell(0);
			sendCityName = sendCityCell.getStringCellValue();
			sendCityName = sendCityName.substring(sendCityName.indexOf("：") + 1, sendCityName.length() - 1);
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("EXCEL格式有误，请检查数据后重试。");
		}

		Long sendCityId = searchCityIdByCityName(sendCityName);

		for (int i = 2; i <= rowNumber; i++) {
			row = sheet.getRow(i);
			Cell[] cells = new Cell[colNumber];

			try {
				for (int j = 0; j < colNumber; j++) {
					cells[j] = row.getCell(j);
				}
			} catch (Exception e) {
				log.error("{}", e);
			}

			Long recvCityId;
			Double distance;
			Integer leadTime;
			BigDecimal unitPrice;
			BigDecimal totalPrice;
			BigDecimal taxRate;
			try {
				recvCityId = searchCityIdByCityName(cells[2].getStringCellValue());
				distance = cells[3].getNumericCellValue();
				leadTime = Integer.valueOf((int) cells[4].getNumericCellValue());
				totalPrice = new BigDecimal(formatter.formatCellValue(cells[5])).setScale(2, RoundingMode.HALF_DOWN);
				unitPrice = totalPrice.divide(new BigDecimal(distance), 2, RoundingMode.HALF_DOWN);
				taxRate = new BigDecimal(formatter.formatCellValue(cells[7]).replaceAll("%", "")).divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);

				QuotationPrice price = new QuotationPrice();
				price.setQuotationAmount(1);
				price.setQuotationPrice(totalPrice);
				price.setCorpId(env.getUser().getCorpId());
				price.setFlag(0);

				QuotationDetail quotationDetail = new QuotationDetail();
				quotationDetail.setSendCityId(sendCityId);
				quotationDetail.setRecvCityId(recvCityId);
				quotationDetail.setDistance(distance == 0 ? null : distance);
				quotationDetail.setLeadTime(leadTime == 0 ? null : leadTime);
				quotationDetail.setUnitPrice(unitPrice.equals(BigDecimal.ZERO) ? null : unitPrice);
				quotationDetail.setTotalPrice(totalPrice.equals(BigDecimal.ZERO) ? null : totalPrice);
				quotationDetail.setTaxRate(taxRate.equals(BigDecimal.ZERO) ? null : taxRate);
				quotationDetail.setCarBrandId(carBrandId);
				quotationDetail.setCarTypeId(carTypeId);
				quotationDetail.setCarModelId(carModelId);
				quotationDetail.setQuotationPlan(quotationPlan);
				quotationDetail.setJobType(jobType);
				quotationDetail.setCorpId(env.getUser().getCorpId());
				quotationDetail.setFlag(FlagStatus.No.getIndex());
				quotationDetail.setPrices(new ArrayList(){{add(price);}});
				quotationDetails.add(quotationDetail);
			} catch (Exception e) {
				log.error("{}", e);
				throw new RuntimeException("EXCEL格式有误，请检查后重试");
			}
		}

		quotationDetails.forEach(quotationDetail -> {

			String priceDesc = quotationDetail.getPrices().stream().map(QuotationPrice::toString).collect(Collectors.joining(";"));
			quotationDetail.setPriceDesc(priceDesc);
		});

		return quotationDetails;
	}

	// 梅赛德斯奔驰EXCEL文件解析
	private List<QuotationDetail> parseExcelForMercedesBenz(Workbook workbook) {
		throw new RuntimeException("奔驰导入模板即将支持");
	}

	// 丰田EXCEL文件解析
	@Transactional
	@Override
	public List<QuotationDetail> parseExcelForToyota(Workbook workbook, Integer quotationPlan) throws Exception {

		Environment env = Environment.getEnv();
		DataFormatter formatter = new DataFormatter();
		List<QuotationDetail> quotationDetails = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);

		int colCount = 11;
		int rowCount = ExcelUtil.getLastRowNumber(workbook);


		String sendCityName = formatter.formatCellValue(sheet.getRow(4).getCell(0)).substring(9, 11);

		String lexusBrand   = formatter.formatCellValue(sheet.getRow(7).getCell(3));
		String cbuBrand     = formatter.formatCellValue(sheet.getRow(7).getCell(6));

		String dColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(3)).substring(2, 3);
		String eColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(4)).substring(0, 1);
		String fColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(5)).substring(0, 1);
		String gColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(6)).substring(2, 3);
		String hColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(7)).substring(0, 1);
		String iColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(8)).substring(0, 1);
		String jColumnNumber = formatter.formatCellValue(sheet.getRow(9).getCell(9)).substring(0, 1);


		for (int rowNumber = 10; rowNumber <= rowCount; rowNumber++) {
			Cell[] cells = new Cell[colCount];
			String[] textCells = new String[colCount];
			for (int colNumber = 0; colNumber < colCount; colNumber++) {
				cells[colNumber] = sheet.getRow(rowNumber).getCell(colNumber);
				if (cells[colNumber] == null) {
					continue;
				}
				String textCell = "";
				if (cells[colNumber].getCellTypeEnum() == CellType.FORMULA) {
					switch (cells[colNumber].getCachedFormulaResultTypeEnum()) {
						case NUMERIC:
							textCell = "";
							textCell = String.valueOf(cells[colNumber].getNumericCellValue());
							textCells[colNumber] = textCell.contains(",") ? textCell.replace(",", ""): textCell;
							break;
						case STRING:
							textCell = "";
							textCell = String.valueOf(cells[colNumber].getRichStringCellValue());
							textCells[colNumber] = textCell.contains(",") ? textCell.replace(",", ""): textCell;
							break;
					}
				} else {
					textCell = "";
					textCell = String.valueOf(formatter.formatCellValue(cells[colNumber]));
					textCells[colNumber] = textCell.contains(",") ? textCell.replace(",", ""): textCell;
				}
			}

			String recvCityName = textCells[1];

			int leadTime    = NumberUtils.toInt(textCells[10]);
			double distance = NumberUtils.toDouble(textCells[2]);

			Long sendCityId   = searchCityIdByCityName(sendCityName);
			Long recvCityId   = searchCityIdByCityName(recvCityName);

			Long lexusBrandId = searchBrandIdByBrandName(lexusBrand);
			Long cbuBrandId   = searchBrandIdByBrandName(cbuBrand);

			BigDecimal unitPrice = new BigDecimal(textCells[3]).setScale(2, RoundingMode.HALF_DOWN);
			List<QuotationPrice> prices = new ArrayList<>();
			for (int i = 1; i <= NumberUtils.toInt(dColumnNumber); i++) {

				QuotationPrice lexusPrice = new QuotationPrice();
				lexusPrice.setQuotationAmount(i);
				lexusPrice.setQuotationPrice(unitPrice);
				lexusPrice.setCorpId(env.getUser().getCorpId());
				lexusPrice.setFlag(FlagStatus.No.getIndex());
				prices.add(lexusPrice);
			}

			unitPrice = new BigDecimal(textCells[4]).setScale(2, RoundingMode.HALF_DOWN);
			QuotationPrice lexusPrice = new QuotationPrice();
			lexusPrice.setQuotationAmount(NumberUtils.toInt(eColumnNumber));
			lexusPrice.setQuotationPrice(unitPrice);
			lexusPrice.setCorpId(env.getUser().getCorpId());
			lexusPrice.setFlag(FlagStatus.No.getIndex());
			prices.add(lexusPrice);

			unitPrice = new BigDecimal(textCells[5]).setScale(2, RoundingMode.HALF_DOWN);
			lexusPrice = new QuotationPrice();
			lexusPrice.setQuotationAmount(NumberUtils.toInt(fColumnNumber));
			lexusPrice.setQuotationPrice(unitPrice);
			lexusPrice.setCorpId(env.getUser().getCorpId());
			lexusPrice.setFlag(FlagStatus.No.getIndex());
			prices.add(lexusPrice);

			QuotationDetail lexusDetail = new QuotationDetail();
			lexusDetail.setLeadTime(leadTime);
			lexusDetail.setSendCityId(sendCityId);
			lexusDetail.setRecvCityId(recvCityId);
			lexusDetail.setDistance(distance);
			lexusDetail.setCarBrandId(lexusBrandId);
			lexusDetail.setQuotationPlan(quotationPlan);
			lexusDetail.setUnitPrice(null);
			lexusDetail.setTotalPrice(null);
			lexusDetail.setTaxRate(null);
			lexusDetail.setPrices(prices);
			lexusDetail.setCorpId(env.getUser().getCorpId());
			lexusDetail.setFlag(FlagStatus.No.getIndex());
			quotationDetails.add(lexusDetail);


			unitPrice = new BigDecimal(textCells[6]).setScale(2, RoundingMode.HALF_DOWN);
			prices = new ArrayList<>();
			for (int i = 1; i <= NumberUtils.toInt(gColumnNumber); i++) {

				QuotationPrice cbuPrice = new QuotationPrice();
				cbuPrice.setQuotationAmount(i);
				cbuPrice.setQuotationPrice(unitPrice);
				cbuPrice.setCorpId(env.getUser().getCorpId());
				cbuPrice.setFlag(FlagStatus.No.getIndex());
				prices.add(cbuPrice);
			}

			unitPrice = new BigDecimal(textCells[7]).setScale(2, RoundingMode.HALF_DOWN);
			QuotationPrice cbuPrice = new QuotationPrice();
			cbuPrice.setQuotationAmount(NumberUtils.toInt(hColumnNumber));
			cbuPrice.setQuotationPrice(unitPrice);
			cbuPrice.setCorpId(env.getUser().getCorpId());
			cbuPrice.setFlag(FlagStatus.No.getIndex());
			prices.add(cbuPrice);

			unitPrice = new BigDecimal(textCells[8]).setScale(2, RoundingMode.HALF_DOWN);
			cbuPrice = new QuotationPrice();
			cbuPrice.setQuotationAmount(NumberUtils.toInt(iColumnNumber));
			cbuPrice.setQuotationPrice(unitPrice);
			cbuPrice.setCorpId(env.getUser().getCorpId());
			cbuPrice.setFlag(FlagStatus.No.getIndex());
			prices.add(cbuPrice);

			unitPrice = new BigDecimal(textCells[9]).setScale(2, RoundingMode.HALF_DOWN);
			cbuPrice = new QuotationPrice();
			cbuPrice.setQuotationAmount(NumberUtils.toInt(jColumnNumber));
			cbuPrice.setQuotationPrice(unitPrice);
			cbuPrice.setCorpId(env.getUser().getCorpId());
			cbuPrice.setFlag(FlagStatus.No.getIndex());
			prices.add(cbuPrice);

			QuotationDetail cbuDetail = new QuotationDetail();
			cbuDetail.setLeadTime(leadTime);
			cbuDetail.setSendCityId(sendCityId);
			cbuDetail.setRecvCityId(recvCityId);
			cbuDetail.setDistance(distance);
			cbuDetail.setCarBrandId(cbuBrandId);
			cbuDetail.setQuotationPlan(quotationPlan);
			cbuDetail.setUnitPrice(null);
			cbuDetail.setTotalPrice(null);
			cbuDetail.setTaxRate(null);
			cbuDetail.setPrices(prices);
			cbuDetail.setCorpId(env.getUser().getCorpId());
			cbuDetail.setFlag(FlagStatus.No.getIndex());
			quotationDetails.add(cbuDetail);
		}

		quotationDetails.sort(Comparator.comparing(QuotationDetail::getCarBrandId));
		quotationDetails.forEach(quotationDetail -> {

			String priceDesc = quotationDetail.getPrices().stream().map(QuotationPrice::toString).collect(Collectors.joining(";"));
			quotationDetail.setPriceDesc(priceDesc);
		});
		return quotationDetails;
	}

	// 通过城市名获取城市ID
	private Long searchCityIdByCityName(String cityName) {
		Map<String, Object> map = new HashMap(0);
		map.put("searchContent", cityName);
		List<City> cities = cityService.queryList(map);
		cities = cities.stream().filter(city -> city.getCityType() == 2 || city.getCityType() == 3).collect(Collectors.toList());

		if (cities.size() > 0) {
			return cities.stream().findFirst().get().getCityId();
		} else {
			throw new BusinessException(String.format("城市: %s, 请检查EXCEL中起运地/目的地中是否为有效城市", cityName));
		}
	}

	private Long searchBrandIdByBrandName(String brandName) throws Exception {

		try {
			Long brandId = brandService.queryIdByName(brandName);
			if (brandId == null) {
				throw new BusinessException(String.format("未查询到当前车辆品牌: %s, 请维护品牌档案后重试。", brandName));
			}
			return brandId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	//封存
	@Override
	@Transactional
	public void blocked(Long quotationId) {
		try {
			Quotation quotation=quotationMapper.selectByPrimaryKey(quotationId);
			if (quotation==null) {
				throw new BusinessException("客户报价不存在，请核实");
			}
			quotation.setFlag(1);
			quotationMapper.updateByPrimaryKeySelective(quotation);
			//封存子表
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("quotationId", quotation.getQuotationId());
			List<QuotationDetail> quotationDetails=quotationDetailService.queryList(map);
			quotationDetails.forEach(quotationDetail->{
				quotationDetail.setFlag(1);
				quotationDetailService.updateSelective(quotationDetail);
			});
		} catch (Exception e) {
			throw e;
			
		}
		
	}
	//取消封存
	@Override
	@Transactional
	public void unblocked(Long quotationId) {
		try {
			Quotation quotation=quotationMapper.selectByPrimaryKey(quotationId);
			if (quotation==null) {
				throw new BusinessException("客户报价不存在，请核实");
			}
			quotation.setFlag(0);
			quotationMapper.updateByPrimaryKeySelective(quotation);
			Map<String, Object> map=new HashMap<String, Object>();
			//取消封存子表
			map.put("quotationId", quotation.getQuotationId());
			List<QuotationDetail> quotationDetails=quotationDetailService.queryList(map);
			quotationDetails.forEach(quotationDetail->{
				quotationDetail.setFlag(0);
				quotationDetailService.updateSelective(quotationDetail);
			});
		} catch (Exception e) {
			throw e;
			
		}
		
	}
}
