package com.tilchina.timp.mapper;

import com.tilchina.timp.model.Login;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.UnitLogin;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.Map;

/**
* 收发货单位登陆信息档案
*
* @version 1.0.0
* @author XiaHong
*/
@Repository
public interface UnitLoginMapper extends BaseMapper<UnitLogin> {

    UnitLogin selectByUnitCodeOrDealerCode(Map<String,Object>  map);

    UnitLogin selectByUnitId(Long unitId);

}
