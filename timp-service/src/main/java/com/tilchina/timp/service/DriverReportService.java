package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.DriverReport;
import com.tilchina.timp.model.DriverSettlement;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface DriverReportService {
	
	/**
	 * 分页查询
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<DriverReport> queryList(Map<String, Object> data, int pageNum, int pageSize);
	
	/**
	 * 空闲提报
	 * @param record
	 */
	void dt(DriverReport record);
	
	/**
	 * 司机提报
	 * @param t
	 */
	void report(DriverReport t);
	
	PageInfo<DriverReport> getList(Map<String, Object> data, int pageNum, int pageSize);
	
	/**
	 * app 查询提报历史
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<DriverReport> queryByDriverIdList(Map<String, Object> data, int pageNum, int pageSize);
	
	/**
	 * 根据司机获取出勤天数
	 * @param driverSettlement
	 * @return
	 */
	Integer getParticipationDay(DriverSettlement driverSettlement);

	
	
}
