package com.tilchina.timp.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DealerReceipt;

/**
* 电子签收单
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface DealerReceiptService extends BaseService<DealerReceipt> {

	void deleteList(int[] data);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<DealerReceipt> getList(Map<String, Object> data, int pageNum, int pageSize);

}
