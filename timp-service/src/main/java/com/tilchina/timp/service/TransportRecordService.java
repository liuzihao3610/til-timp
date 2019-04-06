package com.tilchina.timp.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportRecord;

/**
* 运输记录表
*
* @version 1.1.0
* @author Xiahong
*/
public interface TransportRecordService extends BaseService<TransportRecord> {
	
    PageInfo<TransportRecord> queryListByCode(Map<String, Object> map, int pageNum, int pageSize);

	void upLoad(Map<String, Object> data);
    
}
