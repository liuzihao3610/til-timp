package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Tractor;

/**
* 轿运车车头型号档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface TractorService extends BaseService<Tractor> {


	void deleteList(int[] data);

	void updateBillStatus(Long tractorId, String path);

	PageInfo<Tractor> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	void removeDate(Long record);

	void unaudit(Long data);

	void audit(Long data);

}
