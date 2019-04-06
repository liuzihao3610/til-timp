package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.DriverLogin;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 司机登录信息
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface DriverLoginMapper extends BaseMapper<DriverLogin> {

    DriverLogin selectByDriverId(@Param("driverId") Long driverId);
}
