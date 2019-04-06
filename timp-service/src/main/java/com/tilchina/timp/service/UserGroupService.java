package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.UserGroup;

import java.util.List;
import java.util.Map;

/**
* 
*
* @version 1.0.0
* @author Xiahong
*/
public interface UserGroupService extends BaseService<UserGroup> {
	
	void logicDeleteById(Long id);
	
	UserGroup queryByUserId(Long userId);

	void allotUser(Map<String, Object> map);

	List<UserGroup> queryByGroupId(Long groupId);

	void deleteByUserId(Long ids);

}
