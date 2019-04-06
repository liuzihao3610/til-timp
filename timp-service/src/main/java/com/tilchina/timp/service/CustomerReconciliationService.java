
package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CustomerReconciliation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 客户对账表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CustomerReconciliationService extends BaseService<CustomerReconciliation> {

	void checking(Map<String, String> params);

	void confirmSettlement(Map<String, Object> params);

	String importFile(Integer fileType, String reconciliationNumber, MultipartFile file) throws Exception;

	String importReconciliation(Long customerId, String startDateString, String endDateString, MultipartFile file) throws Exception;

	Workbook exportExcel(Long reconciliationId);

	void setDocumentsChecked(Long reconciliationId);

	void setDocumentsUnchecked(Long reconciliationId);

	void parseExcelOfRedBlueHedge(CustomerReconciliation customerReconciliation, Workbook workbook);

	String parseExcelOfReconciliation(Long customerId, Date startDate, Date endDate, Workbook workbook);

	void parseExcelOfCustomerFeedBack(CustomerReconciliation customerReconciliation, Workbook workbook);

	void logicalDelete(Long reconciliationId);

	void updateDeductionReasonsForReconciliation(Long reconciliationId);

	void updateAmountForReconciliation(Long reconciliationId);

	List<CustomerReconciliation> selectByReconciliationNumber(String reconciliationNumber);

	void refreshQuotation(Long reconciliationId);
}
