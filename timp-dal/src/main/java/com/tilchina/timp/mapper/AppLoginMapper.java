package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.AppLogin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface AppLoginMapper extends BaseMapper<AppLogin> {

    AppLogin selectByUserId(@Param("userId") Long userId);

    void updateForClean(Long appLoginId);

	void updateCaptcha(AppLogin appLogin);

}
