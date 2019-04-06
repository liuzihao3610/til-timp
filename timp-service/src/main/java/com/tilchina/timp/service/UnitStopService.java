package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.UnitStop;

/**
* 接车点管理
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface UnitStopService extends BaseService<UnitStop> {
    /**
     * 封存
     * @param data
     */
    void sequestration(Long data);

    /**
     * 取消封存
     * @param data
     */
    void unsequestration(Long data);

    /**
     * 通过收发货单位和接车点地址查询
     * @param unitId
     * @param unitStopAdress
     * @return
     */
    UnitStop getByUnitAdress(Long unitId, String unitStopAdress);
}
