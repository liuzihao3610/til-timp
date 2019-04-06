package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.AppLogin;

import java.util.Date;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
public interface AppLoginService extends BaseService<AppLogin> {

    String login(String identifier,String ip, String ua, Date date, boolean check);

    AppLogin queryByUserId(Long userId);

    void cleanById(Long appLoginId);

	void updateCaptcha(AppLogin appLogin);
}
