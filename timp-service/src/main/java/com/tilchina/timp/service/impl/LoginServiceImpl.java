package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.catalyst.utils.MD5;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.LoginMapper;
import com.tilchina.timp.model.Login;
import com.tilchina.timp.service.LoginService;
import com.tilchina.timp.utils.GenPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* 认证信息档案
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class LoginServiceImpl extends BaseServiceImpl<Login> implements LoginService {

	@Autowired
    private LoginMapper loginmapper;
	
	@Override
	protected BaseMapper<Login> getMapper() {
		return loginmapper;
	}

	@Override
    public String login(String ip, String ua, Date date){
        Environment env = Environment.getEnv();
        Login login = loginmapper.selectByUserId(env.getUser().getUserId());
        login.setIp(ip);
        login.setRecentlyLogintime(date);
        login.setUserAgent(MD5.MD5(ua));
        login.setErrorTimes(0);
        login.setClientType(ClientType.WEB.getIndex());
        loginmapper.updateByPrimaryKeySelective(login);

        return null;
    }

	@Override
	public Login queryByUserId(Long userId){
		log.debug("queryByUserId: {}",userId);
		Login login = null;
		try {
			login = loginmapper.selectByUserId(userId);
			if(login == null) {
        		throw new BusinessException("9008","用户ID");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return login;
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		try {
			if( id != null) {
				loginmapper.logicDeleteByPrimaryKey(id);
			}else {
				throw new BusinessException("9001","认证信息ID");
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		try {
			if(ids.length > 0) {
				loginmapper.logicDeleteByPrimaryKeyList(ids);
			}else {
				throw new BusinessException("9001","认证信息ID");
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void updateLogin(Login login) {
		log.debug("updateLogin: {}",login);
		try {
			if(login.getBlock() != null || login.getPassword() != null) {
				if(login.getBlock() != null) {
					login.setBlock(0);
					loginmapper.updateByPrimaryKeyLogin(login);
				}else if("reset".equals(login.getPassword())){
					login.setPassword(GenPassword.get());
					loginmapper.updateByPrimaryKeyLogin(login);
				}
			}else {
				throw new BusinessException("9001","认证信息档案");
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }

	}

	@Override
	protected StringBuilder checkNewRecord(Login login) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "password", "密码", login.getPassword(), 60));
        s.append(CheckUtils.checkString("YES", "token", "TOKEN", login.getToken(), 36));
        s.append(CheckUtils.checkString("YES", "special", "SPECIAL", login.getSpecial(), 36));
        s.append(CheckUtils.checkString("YES", "captcha", "验证码", login.getCaptcha(), 10));
        s.append(CheckUtils.checkString("YES", "ldentifier", "识别码", login.getIdentifier(), 40));
        s.append(CheckUtils.checkString("YES", "userAgent", "UserAgent", login.getUserAgent(), 32));
        s.append(CheckUtils.checkInteger("NO", "clientType", "客户端类型", login.getClientType(), 10));
        s.append(CheckUtils.checkInteger("NO", "errorTimes", "密码输入错误次数", login.getErrorTimes(), 10));
        s.append(CheckUtils.checkInteger("NO", "block", "锁定状态:0=未锁定,1=锁定", login.getBlock(), 10));
        s.append(CheckUtils.checkString("YES", "ip", "IP", login.getIp(), 15));
        s.append(CheckUtils.checkDate("YES", "recentlyLogintime", "最近一次登陆时间", login.getRecentlyLogintime()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", login.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Login login) {
        StringBuilder s = checkNewRecord(login);
        s.append(CheckUtils.checkPrimaryKey(login.getLoginId()));
		return s;
	}

	@Override
	public Login queryByUserCode(String userCode, Integer userType) {
		try {
			log.debug("queryByUserCode: {}",userCode);
			return loginmapper.selectByUserCode(userCode,userType);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

}
