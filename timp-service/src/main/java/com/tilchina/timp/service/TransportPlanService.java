package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportPlan;

/**
* 运输计划
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransportPlanService extends BaseService<TransportPlan> {

	List<TransportPlan> queryByOrderId(Long id);
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(Integer[] array);

	List<TransportPlan> querySequenceList(Map<String, Object> map);
	
	/**
	 * 在途查询
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	/*PageInfo<routeQuery> routeQuery(Map<String, Object> data, int pageNum, int pageSize);*/
	
}
