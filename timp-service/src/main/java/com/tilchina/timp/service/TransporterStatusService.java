package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransporterStatus;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransporterStatusService extends BaseService<TransporterStatus> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);
	
	void updateById(TransporterStatus record);
	
	List<TransporterStatus> getRefer(Map<String, Object> map, int pageNum, int pageSize);
	
	/**
	 * 根据轿运车id查询
	 * @param transporterId
	 * @return
	 */
	TransporterStatus queryByTransporterId(Long transporterId);
	
}
