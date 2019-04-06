package com.tilchina.timp.service;


import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Dept;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface DeptService extends BaseService<Dept> {
	
	void logicDeleteById(Long id) throws Exception;
	
	void logicDeleteByIdList(int[]  ids);
	
	Map<String, Object> queryDynamicList();

	PageInfo<Dept> getRefer(Map<String, Object> data,int pageNum, int pageSize);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<Dept> getList(Map<String, Object> data, int pageNum, int pageSize);

	List<Dept> queryByDeptNames(List<String> deptCollect);
}
