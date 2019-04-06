package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.UnitStop;
import org.springframework.stereotype.Repository;

/**
* 接车点管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface UnitStopMapper extends BaseMapper<UnitStop> {
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
