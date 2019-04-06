package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.OutsourcingReconciliation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.Map;

/**
* 外协对账主表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface OutsourcingReconciliationService extends BaseService<OutsourcingReconciliation> {

	void importReconciliation(Long vendorCorpId, MultipartFile file) throws Exception;

	void checking(Long reconciliationId) throws Exception;

	void logicalDelete(Long reconciliationId);

	void setDocumentsChecked(Long reconciliationId);

	void setDocumentsUnchecked(Long reconciliationId);

	void confirmSettlement(Map<String, Object> params) throws ParseException;

	Workbook exportFeedback(Long reconciliationId);

	void updateDocumentsCheckState(OutsourcingReconciliation reconciliation);
}
