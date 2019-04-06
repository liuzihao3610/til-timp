package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverLogin;

/**
* 司机登录信息
*
* @version 1.0.0
* @author WangShengguang
*/
public interface DriverLoginService extends BaseService<DriverLogin> {

    DriverLogin queryByDriverId(Long driverId);
}
