package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverStatus;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
public interface DriverStatusService extends BaseService<DriverStatus> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);
	
	DriverStatus queryByDirverId(Long dirverId);
	
	void updateDriverStatus (DriverStatus record);

	PageInfo<DriverStatus> getRefer(Map<String, Object> map, int pageNum, int pageSize);

	DriverStatus queryByDirverIdAndTransportId(Long driverId, Long transportId);
	
}
