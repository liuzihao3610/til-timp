package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.PadLogin;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface PadLoginMapper extends BaseMapper<PadLogin> {

    PadLogin selectByUserId(@Param("userId") Long userId);

    void updateForClean(Long padLoginId);

    void updateCaptcha(PadLogin padLogin);
}
