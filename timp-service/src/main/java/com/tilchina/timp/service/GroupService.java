package com.tilchina.timp.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Group;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface GroupService extends BaseService<Group> {
	
	 void logicDeleteById(Long id);
	
	 void logicDeleteByIdList(int[] ids);
	
	 Map<String, Object> queryDynamicList();

    PageInfo<Group> getRefer(Map<String,Object> data, int pageNum, int pageSize);

}
