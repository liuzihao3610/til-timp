package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.MD5;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.enums.ErrorTimes;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.Login;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.UnitLogin;
import com.tilchina.timp.service.UnitLoginService;
import com.tilchina.timp.mapper.UnitLoginMapper;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
* 收发货单位登陆信息档案
*
* @version 1.0.0
* @author XiaHong
*/
@Service
@Slf4j
public class UnitLoginServiceImpl extends BaseServiceImpl<UnitLogin> implements UnitLoginService {

	@Autowired
    private UnitLoginMapper unitloginmapper;
	
	@Override
	protected BaseMapper<UnitLogin> getMapper() {
		return unitloginmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(UnitLogin unitlogin) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "password", "密码", unitlogin.getPassword(), 60));
        s.append(CheckUtils.checkString("YES", "token", "TOKEN", unitlogin.getToken(), 36));
        s.append(CheckUtils.checkString("YES", "special", "SPECIAL", unitlogin.getSpecial(), 36));
        s.append(CheckUtils.checkString("YES", "captcha", "验证码", unitlogin.getCaptcha(), 10));
        s.append(CheckUtils.checkString("YES", "identifier", "识别码", unitlogin.getIdentifier(), 40));
        s.append(CheckUtils.checkString("YES", "userAgent", "UserAgent", unitlogin.getUserAgent(), 32));
        s.append(CheckUtils.checkInteger("NO", "clientType", "客户端类型", unitlogin.getClientType(), 10));
        s.append(CheckUtils.checkInteger("NO", "errorTimes", "密码输入错误次数", unitlogin.getErrorTimes(), 10));
        s.append(CheckUtils.checkInteger("NO", "block", "锁定状态:0=未锁定,1=锁定", unitlogin.getBlock(), 10));
        s.append(CheckUtils.checkString("YES", "ip", "IP", unitlogin.getIp(), 15));
        s.append(CheckUtils.checkDate("YES", "recentlyLogintime", "最近一次登陆时间", unitlogin.getRecentlyLogintime()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", unitlogin.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(UnitLogin unitlogin) {
        StringBuilder s = checkNewRecord(unitlogin);
        s.append(CheckUtils.checkPrimaryKey(unitlogin.getUnitLoginId()));
		return s;
	}

    @Override
    public UnitLogin queryByUnitCodeOrDealerCode(Map<String,Object>  map) {
        log.debug("queryByUnitCodeOrDealerCode: {}",map);
        return unitloginmapper.selectByUnitCodeOrDealerCode(map);
    }

    @Override
    public String login(String ip, String ua, Date date) {
        Environment env = Environment.getEnv();
        UnitLogin login = unitloginmapper.selectByUnitId(env.getUnit().getUnitId());
        login.setIp(ip);
        login.setRecentlyLogintime(date);
        login.setUserAgent(MD5.MD5(ua));
        login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
        login.setClientType(ClientType.WEB.getIndex());
        unitloginmapper.updateByPrimaryKeySelective(login);

        return null;
    }

    @Override
    public UnitLogin queryByUnitId(Long unitId) {
        log.debug("queryByUnitId: {}",unitId);
        try {
            Optional.ofNullable(unitId)
                    .orElseThrow(() -> new BusinessException("请输入收发货单位id！"));
            return unitloginmapper.selectByUnitId(unitId);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }

}
