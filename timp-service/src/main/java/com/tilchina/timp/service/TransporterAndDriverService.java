package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransporterAndDriver;

import java.util.List;
import java.util.Map;

/**
*  轿运车司机关系
*
* @version 1.1.0
* @author Xiahogn
*/
public interface TransporterAndDriverService extends BaseService<TransporterAndDriver> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);
	
	TransporterAndDriver queryByKeyId(Map<String,Long> map);

    List<Map<String, Object>> queryIdleTransporter();
    
    /**
     * 参照
     * @param data
     * @param pageNum
     * @param pageSize
     * @return
     */
	PageInfo<TransporterAndDriver> getRefer(Map<String, Object> data, int pageNum, int pageSize);

	/**
	 * 新增记录时，获取该轿运车有效的承包记录，如果存在，置为失效状态，同时更新承包时间止为当前时间
	 * @author XueYuSong
	 * @param transporterId 轿运车ID
	 */
	void setInvalidStatusByTransporterId(Long transporterId);

	/**
	 * 通过ID将记录置为失效状态
	 * @author XueYuSong
	 * @param transporterAndDriverId
	 */
	void setInvalidStatusById(Long transporterAndDriverId);
}
