package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Power;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.vo.RegistTreeVO;

/**
* 权限管理
*
* @version 1.1.0
* @author XiaHong
*/
public interface PowerService extends BaseService<Power> {
	
	PageInfo<Power> queryByGroupId(Map<String, Object> map, int pageNum, int pageSize);

	void logicDeleteById(Long id);
	
	List<Power> queryByGroupId(Long groupId);

	void savePower(List<RegistTreeVO> data, Long groupId);

    void test();

}
