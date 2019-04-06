package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.Group;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface GroupMapper extends BaseMapper<Group> {
	
	int logicDeleteByPrimaryKey(Long id);
	
	int logicDeleteByPrimaryKeyList(int[] ids);
	 
	 List<Group> selectDynamicList();

    List<Group> selectRefer(Map<String,Object> map);

}
