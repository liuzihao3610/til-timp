package com.tilchina.timp.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.TransporterReport;

/**
* 骄运车状态变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransporterReportService  {
	
	/**
	 * 提报
	 * @param t
	 */
	void report(TransporterReport t);
	
	/**
	 * 分页查询
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<TransporterReport> queryList(Map<String, Object> data, int pageNum, int pageSize);
	
}
