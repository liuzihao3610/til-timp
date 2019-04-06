package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.Power;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface PowerMapper extends BaseMapper<Power> {
	
	int logicDeleteByPrimaryKey(Long id);
	
	List<Power> selectByGroupId(Map<String, Object> map);

    List<Power> selectByGroupId(@Param("groupId") Long groupId);

}
