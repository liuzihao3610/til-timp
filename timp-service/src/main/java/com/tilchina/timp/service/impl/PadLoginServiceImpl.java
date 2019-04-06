package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.MD5;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.LoginErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.PadLogin;
import com.tilchina.timp.service.PadLoginService;
import com.tilchina.timp.mapper.PadLoginMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class PadLoginServiceImpl extends BaseServiceImpl<PadLogin> implements PadLoginService {

	@Autowired
	private PadLoginMapper padLoginMapper;

	@Override
	protected BaseMapper<PadLogin> getMapper() {
		return padLoginMapper;
	}

	@Override
	@Transactional
	public String login(String identifier, String ip, String ua, Date date, boolean check) {
		Environment env = Environment.getEnv();
		PadLogin padLogin = padLoginMapper.selectByUserId(env.getUser().getUserId());

		if(padLogin == null){
			padLogin = new PadLogin();
			padLogin.setUserId(env.getUser().getUserId());
			padLogin.setCorpId(env.getCorp().getCorpId());
			padLoginMapper.insertSelective(padLogin);
		}

		String uuid = com.tilchina.catalyst.utils.StringUtils.getUuid();
		if(check && StringUtils.isBlank(identifier)){
			throw new LoginErrorException("9999");
		}

		//检查是否更换终端
		if(check && !identifier.equals(padLogin.getIdentifier())){
			padLogin.setCheckPhone(0);
		}else{
			padLogin.setCheckPhone(1);
		}

		padLogin.setToken(uuid);
		padLogin.setIp(ip);
		padLogin.setRecentlyLogintime(date);
		padLogin.setIdentifier(identifier);
		padLogin.setUserAgent(MD5.MD5(ua));
		padLogin.setClientType(ClientType.APP.getIndex());
		padLoginMapper.updateByPrimaryKeySelective(padLogin);

		return uuid;
	}

	@Override
	public PadLogin queryByUserId(Long userId) {
		return padLoginMapper.selectByUserId(userId);
	}

	@Override
	protected StringBuilder checkNewRecord(PadLogin padLogin) {
		StringBuilder s = new StringBuilder();

		return s;
	}

	@Override
	protected StringBuilder checkUpdate(PadLogin padLogin) {
		StringBuilder s = checkNewRecord(padLogin);
		s.append(CheckUtils.checkPrimaryKey(padLogin.getPdaLoginId()));
		return s;
	}

	@Override
	public void cleanById(Long padLoginId) {
		padLoginMapper.updateForClean(padLoginId);
	}

	@Override
	public void updateCaptcha(PadLogin padLogin) {

		padLoginMapper.updateCaptcha(padLogin);


	}
}
