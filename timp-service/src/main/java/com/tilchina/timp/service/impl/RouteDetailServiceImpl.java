package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.RouteDetail;
import com.tilchina.timp.service.RouteDetailService;
import com.tilchina.timp.mapper.RouteDetailMapper;

import java.util.List;

/**
* 虚拟路线明细
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class RouteDetailServiceImpl extends BaseServiceImpl<RouteDetail> implements RouteDetailService {

	@Autowired
    private RouteDetailMapper routedetailmapper;
	
	@Override
	protected BaseMapper<RouteDetail> getMapper() {
		return routedetailmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(RouteDetail routedetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkLong("NO", "cityAreaId", "虚拟城市区域ID", routedetail.getCityAreaId(), 12));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(RouteDetail routedetail) {
        StringBuilder s = checkNewRecord(routedetail);
        s.append(CheckUtils.checkPrimaryKey(routedetail.getRouteDetailId()));
		return s;
	}

}
