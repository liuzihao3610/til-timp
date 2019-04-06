package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Login;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface LoginMapper extends BaseMapper<Login> {

    Login selectByUserCode(@Param("userCode") String userCode, @Param("userType") Integer userType);

    Login selectByUserId(@Param("userId") Long userId);
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
    int updateByPrimaryKeyLogin(Login login); 
	 
}
