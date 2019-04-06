package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CorpType;

import java.util.Map;

/**
* 公司类型
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface CorpTypeService extends BaseService<CorpType> {

	PageInfo<CorpType> getCorpTypeNameList(Map<String, Object> map, int pageNum,int pageSize);
	void deleteList(int[] data);
	PageInfo<CorpType> getReferenceList(Map<String, Object> data, int pageNum, int pageSize);
	CorpType queryByCorpId(Long corpCarrierId);

}
