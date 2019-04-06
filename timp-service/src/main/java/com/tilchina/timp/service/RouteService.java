package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Route;
import com.tilchina.timp.model.RouteDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* 虚拟路线
*
* @version 1.0.0
* @author WangShengguang
*/
public interface RouteService {

    @Transactional
    void add(Route route);

    @Transactional
    void update(Route route);

    void delete(Long routeId);

    PageInfo<Route> queryList(Map<String, Object> map, int pageNum, int pageSize);

    List<Route> queryList(Map<String, Object> map);

    List<RouteDetail> queryDetails(Long routeId);

    List<Route> queryByRouteType(Integer routeType);

    List<RouteDetail> queryDetailByRouteType(Integer routeType);
}
