package com.tilchina.timp.service;

import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportRecordOuter;

/**
* 运输记录表
*
* @version 1.1.0
* @author Xiahong
*/
public interface TransportRecordOuterService extends BaseService<TransportRecordOuter> {
	
    PageInfo<TransportRecordOuter> queryListByCode(Map<String, Object> map, int pageNum, int pageSize);
    
}
