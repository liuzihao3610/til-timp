package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.UserGroup;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface UserGroupMapper extends BaseMapper<UserGroup> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	UserGroup selectByUserId(@Param("userId") Long userId);

    List<UserGroup> selectByGroupId(@Param("groupId")Long groupId);

    void deleteByUserId(@Param("userId")Long userId);
}
