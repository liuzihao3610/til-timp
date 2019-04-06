package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.catalyst.utils.MD5;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.LoginErrorException;
import com.tilchina.timp.mapper.AppLoginMapper;
import com.tilchina.timp.model.AppLogin;
import com.tilchina.timp.service.AppLoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class AppLoginServiceImpl extends BaseServiceImpl<AppLogin> implements AppLoginService {

	@Autowired
    private AppLoginMapper apploginmapper;

	@Override
	protected BaseMapper<AppLogin> getMapper() {
		return apploginmapper;
	}

	@Override
    @Transactional
	public String login(String identifier, String ip, String ua, Date date,boolean check) {
		Environment env = Environment.getEnv();
		AppLogin appLogin = apploginmapper.selectByUserId(env.getUser().getUserId());

		if(appLogin == null){
            appLogin = new AppLogin();
            appLogin.setUserId(env.getUser().getUserId());
            appLogin.setCorpId(env.getCorp().getCorpId());
            apploginmapper.insertSelective(appLogin);
        }

		String uuid = com.tilchina.catalyst.utils.StringUtils.getUuid();
		if(check && StringUtils.isBlank(identifier)){
			throw new LoginErrorException("9999");
		}

		//检查是否更换终端
		if(check && !identifier.equals(appLogin.getIdentifier())){
            appLogin.setCheckPhone(0);
		}else{
            appLogin.setCheckPhone(1);
        }

		appLogin.setToken(uuid);
        appLogin.setIp(ip);
        appLogin.setRecentlyLogintime(date);
        appLogin.setIdentifier(identifier);
        appLogin.setUserAgent(MD5.MD5(ua));
        appLogin.setClientType(ClientType.APP.getIndex());
        apploginmapper.updateByPrimaryKeySelective(appLogin);

		return uuid;
	}

    @Override
    public AppLogin queryByUserId(Long userId) {
        return apploginmapper.selectByUserId(userId);
    }

    @Override
	protected StringBuilder checkNewRecord(AppLogin applogin) {
		StringBuilder s = new StringBuilder();

		return s;
	}

	@Override
	protected StringBuilder checkUpdate(AppLogin applogin) {
        StringBuilder s = checkNewRecord(applogin);
        s.append(CheckUtils.checkPrimaryKey(applogin.getAppLoginId()));
		return s;
	}

    @Override
    public void cleanById(Long appLoginId) {
        apploginmapper.updateForClean(appLoginId);
    }

	@Override
	public void updateCaptcha(AppLogin appLogin) {

		apploginmapper.updateCaptcha(appLogin);
		
	
	}


}
