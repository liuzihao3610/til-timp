package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.TransportLogin;
import com.tilchina.timp.service.TransportLoginService;
import com.tilchina.timp.mapper.TransportLoginMapper;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class TransportLoginServiceImpl extends BaseServiceImpl<TransportLogin> implements TransportLoginService {

	@Autowired
    private TransportLoginMapper transportloginmapper;
	
	@Override
	protected BaseMapper<TransportLogin> getMapper() {
		return transportloginmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransportLogin transportlogin) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "token", "TOKEN", transportlogin.getToken(), 36));
        s.append(CheckUtils.checkString("YES", "special", "SPECIAL", transportlogin.getSpecial(), 36));
        s.append(CheckUtils.checkInteger("NO", "checkPhone", "手机验证", transportlogin.getCheckPhone(), 10));
        s.append(CheckUtils.checkString("YES", "captcha", "验证码", transportlogin.getCaptcha(), 10));
        s.append(CheckUtils.checkString("YES", "identifier", "识别码", transportlogin.getIdentifier(), 40));
        s.append(CheckUtils.checkString("YES", "userAgent", "UserAgent", transportlogin.getUserAgent(), 32));
        s.append(CheckUtils.checkInteger("NO", "clientType", "客户端类型", transportlogin.getClientType(), 10));
        s.append(CheckUtils.checkString("YES", "ip", "IP", transportlogin.getIp(), 15));
        s.append(CheckUtils.checkDate("YES", "recentlyLogintime", "最近一次登陆时间", transportlogin.getRecentlyLogintime()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transportlogin.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportLogin transportlogin) {
        StringBuilder s = checkNewRecord(transportlogin);
        s.append(CheckUtils.checkPrimaryKey(transportlogin.getTransportLoginId()));
		return s;
	}
}
