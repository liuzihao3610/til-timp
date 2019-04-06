package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.RouteDetail;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 虚拟路线明细
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface RouteDetailMapper extends BaseMapper<RouteDetail> {

    List<RouteDetail> selectByRouteType(@Param("routeType") Integer routeType);

    void updateByPrimaryKeySelective(List<RouteDetail> details);

    void deleteByPrimaryKey(List<Long> routeIds);

    List<RouteDetail> selectByRouteId(Long routeId);

    void deleteByRouteId(Long routeId);
}
