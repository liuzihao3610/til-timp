package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.SettleRouteB;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 结算路线子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface SettleRouteBMapper extends BaseMapper<SettleRouteB> {
    /**
     * 通过结算路线主表ID查询子表信息
     * @param settleRouteId
     * @return
     */
    List<SettleRouteB> getBySettleRouteId(Long settleRouteId);
}
