package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Quotation;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.vo.QuotationVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 客户报价
 *
 * @author XueYuSong
 * @version 1.0.0
 */
public interface QuotationService extends BaseService<Quotation> {

	Quotation selectByQuotationVO(QuotationVO quotationVO);

	void setDocumentsChecked(Long quotationId);

	void setDocumentsUnchecked(Long quotationId);

	void blocked(Long quotationId);

	void unblocked(Long quotationId);

	void importExcel(Long customerId, Long vendorCorpId, Integer quotationType, Long contractId, String effectiveDate, String expirationDate, Integer templateType, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType, MultipartFile multipartFile) throws Exception;

	void importUniversalExcel(Integer quotationType, Integer quotationPlan, Long contractId, Integer jobType, MultipartFile multipartFile) throws Exception;

	void importExcelV2(Long customerId, Long vendorCorpId, Integer quotationType, Long contractId, String effectiveDate, Integer templateType, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType, MultipartFile multipartFile) throws Exception;

	List<QuotationDetail> parseExcelForPorsche(Workbook workbook, Long carBrandId, Long carTypeId, Long carModelId, Integer quotationPlan, Integer jobType);

	List<QuotationDetail> parseExcelForToyota(Workbook workbook, Integer quotationPlan) throws Exception;

	List<QuotationDetail> parseExcelForUniversal(Workbook workbook, Integer jobType) throws Exception;

	List<QuotationDetail> parseExcelForLongHaul(Workbook workbook) throws Exception;

	List<QuotationDetail> parseExcelForArea(Workbook workbook) throws Exception;

	List<QuotationDetail> parseExcelForShortBarge(Workbook workbook) throws Exception;

	Quotation parseQuotationForLongHaul(Workbook workbook, Integer quotationPlan, Integer jobType) throws Exception;

	Quotation parseQuotationForArea(Workbook workbook, Integer quotationPlan, Integer jobType) throws Exception;

	Quotation parseQuotationForShortBarge(Workbook workbook, Integer quotationPlan, Integer jobType) throws Exception;
}
