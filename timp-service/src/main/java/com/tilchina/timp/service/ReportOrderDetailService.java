package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.ReportOrderDetail;

/**
* 在途提报运单子表
*
* @version 1.0.0
* @author Xiahong
*/
public interface ReportOrderDetailService extends BaseService<ReportOrderDetail> {
	
	/**
	 * 根据在途提报id查询
	 * @param reportId 在途提报id
	 * @return
	 */
	List<ReportOrderDetail> queryByReportIdList(Long reportId);
	
	/**
	 * 根据在车架号查询
	 * @param carVin 车架号
	 * @return
	 */
	List<ReportOrderDetail> queryByCarVinList(String carVin);
	
}
