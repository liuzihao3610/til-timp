package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Position;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface PositionService extends BaseService<Position> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);
	
	Map<String, Object> queryDynamicList();

	PageInfo<Position> getRefer(Map<String, Object> map, int pageNum, int pageSize);

    List<Position> queryByPositionNames(List<String> positionCollect);
}
