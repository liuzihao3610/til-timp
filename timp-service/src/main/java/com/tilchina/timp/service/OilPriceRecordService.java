package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.OilPriceRecord;

/**
* 油价变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
public interface OilPriceRecordService extends BaseService<OilPriceRecord> {

	void deleteList(int[] data);

	void updateBillStatus(Long recordId, String path);

	void removeDate(Long recoerId);

	void audit(Long data);

	void unaudit(Long data);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<OilPriceRecord> getList(Map<String, Object> data, int pageNum, int pageSize);

}
