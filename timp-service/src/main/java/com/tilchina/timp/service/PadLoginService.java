package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.PadLogin;

import java.util.Date;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
public interface PadLoginService extends BaseService<PadLogin> {

    String login(String identifier, String ip, String ua, Date date, boolean check);

    PadLogin queryByUserId(Long userId);

    void cleanById(Long padLoginId);

    void updateCaptcha(PadLogin padLogin);
}
