package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.SettleRouteB;

import java.util.List;

/**
* 结算路线子表
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface SettleRouteBService extends BaseService<SettleRouteB> {
    /**
     * 通过结算路线主表ID查询子表信息
     * @param settleRouteId
     * @return
     */
    List<SettleRouteB> getBySettleRouteId(Long settleRouteId);
}
