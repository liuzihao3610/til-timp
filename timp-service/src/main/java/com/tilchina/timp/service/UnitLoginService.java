package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.UnitLogin;

import java.util.Date;
import java.util.Map;

/**
* 收发货单位登陆信息档案
*
* @version 1.0.0
* @author XiaHong
*/
public interface UnitLoginService extends BaseService<UnitLogin> {

    /**
     *
     * @param map
     * @return
     */
    UnitLogin queryByUnitCodeOrDealerCode(Map<String,Object> map);

    String login(String ip, String userAgent, Date loginTime);

    UnitLogin queryByUnitId(Long unitId);

}
