package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.DriverLogin;
import com.tilchina.timp.service.DriverLoginService;
import com.tilchina.timp.mapper.DriverLoginMapper;

/**
* 司机登录信息
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class DriverLoginServiceImpl extends BaseServiceImpl<DriverLogin> implements DriverLoginService {

	@Autowired
    private DriverLoginMapper driverloginmapper;
	
	@Override
	protected BaseMapper<DriverLogin> getMapper() {
		return driverloginmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DriverLogin driverlogin) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "password", "密码", driverlogin.getPassword(), 60));
        s.append(CheckUtils.checkString("YES", "token", "TOKEN", driverlogin.getToken(), 36));
        s.append(CheckUtils.checkString("YES", "ldentifier", "识别码", driverlogin.getLdentifier(), 40));
        s.append(CheckUtils.checkString("YES", "userAgent", "UserAgent", driverlogin.getUserAgent(), 32));
        s.append(CheckUtils.checkInteger("NO", "clientType", "客户端类型", driverlogin.getClientType(), 10));
        s.append(CheckUtils.checkInteger("NO", "errorTimes", "密码输入错误次数", driverlogin.getErrorTimes(), 10));
        s.append(CheckUtils.checkInteger("NO", "block", "锁定状态:0=锁定,1=未锁定", driverlogin.getBlock(), 10));
        s.append(CheckUtils.checkString("YES", "ip", "IP", driverlogin.getIp(), 15));
        s.append(CheckUtils.checkDate("YES", "recentlyLogintime", "最近一次登陆时间", driverlogin.getRecentlyLogintime()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", driverlogin.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DriverLogin driverlogin) {
        StringBuilder s = checkNewRecord(driverlogin);
        s.append(CheckUtils.checkPrimaryKey(driverlogin.getDriverLoginId()));
		return s;
	}

	@Override
	public DriverLogin queryByDriverId(Long driverId) {
		return driverloginmapper.selectByDriverId(driverId);
	}
}
