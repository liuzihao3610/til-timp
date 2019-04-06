package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CorpManagement;

import java.util.List;
import java.util.Map;

/**
* 公司管理表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CorpManagementService extends BaseService<CorpManagement> {

	List<CorpManagement> getManagement(Map<String, Object> params);

	void addManagement(Map<String, Object> params);

	void delManagement(Map<String, Object> params);

    List<CorpManagement> queryByUserId(Long userId);

	List<Map<String, Object>> getCorpListByUserId(Long userId);

	// 查询当前登录用户可管理的公司列表
	List<Map<String, Object>> getSelfCorpList();
}
