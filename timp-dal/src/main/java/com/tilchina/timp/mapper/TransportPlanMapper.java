package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.TransportPlan;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 运输计划
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransportPlanMapper extends BaseMapper<TransportPlan> {

	List<TransportPlan> selectByOrderKey(Long id);
	
	 int logicDeleteByPrimaryKey(Long id);
	 
	 int logicDeleteByPrimaryKeyList(int[]  dels);
	 
	/**
	 * 在途查询
	 * @param data
	 * @return
	 */
/*	List<TransportPlan> selectRouteQuery(Map<String, Object> data);*/

	List<TransportPlan> selectSequenceList(Map<String, Object> map);
	
}
