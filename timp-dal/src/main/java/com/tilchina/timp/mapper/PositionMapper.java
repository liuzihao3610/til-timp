package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Position;

/**
* 职务档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface PositionMapper extends BaseMapper<Position> {
	
	 int logicDeleteByPrimaryKey(Long id);
	 
	 int logicDeleteByPrimaryKeyList(int[] ids);
	 
	 List<Position> selectDynamicList();
	 
	 List<Position> selectRefer(Map<String, Object> map);

    List<Position> selectByPositionNames(@Param("positionNames") List<String> positionNames);

}
