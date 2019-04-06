package com.tilchina.timp.service;


import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Login;

import java.util.Date;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
public interface LoginService extends BaseService<Login> {

    String login(String ip, String ua, Date date);

    Login queryByUserId(Long userId);

	void logicDeleteById(Long id);

	void logicDeleteByIdList(int[]  ids);
	
	void updateLogin(Login login);

    Login queryByUserCode(String userCode, Integer userType);
}
