package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.enmus.RowStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.RouteDetailMapper;
import com.tilchina.timp.model.RouteDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.model.Route;
import com.tilchina.timp.service.RouteService;
import com.tilchina.timp.mapper.RouteMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* 虚拟路线
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

	@Autowired
    private RouteMapper routeMapper;

    @Autowired
    private RouteDetailMapper routeDetailMapper;

    @Override
    @Transactional
	public void add(Route route){
        if(route == null){
            throw new BusinessException("9003");
        }
        StringBuilder s = checkNewRecord(route);
        if(s.length() > 0){
            throw new BusinessException(s.toString());
        }
        Environment env = Environment.getEnv();
        route.setCreateDate(new Date());
        route.setCreator(env.getUser().getUserId());
        route.setCorpId(env.getCorp().getCorpId());
        try {
            routeMapper.insertSelective(route);
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("编码或名称已存在！");
            }else{
                throw e;
            }
        }

        List<RouteDetail> details = route.getDetails();
        if(CollectionUtils.isEmpty(details)){
            throw new BusinessException("明细数据为空！");
        }
        StringBuilder sub = new StringBuilder();
        for (int i = 0; i < details.size(); i++) {
            RouteDetail detail = details.get(i);
            if(detail.getCityAreaId() == null){
                sub.append("明细数据第"+(i+1)+"行，虚拟城市为空！");
                continue;
            }
            detail.setCorpId(env.getCorp().getCorpId());
            detail.setRouteId(route.getRouteId());
        }
        if(sub.length() > 0){
            throw new BusinessException(sub.toString());
        }
        try {
            routeDetailMapper.insert(details);
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("明细数据虚拟路线重复！");
            }else{
                throw e;
            }
        }
	}

	@Override
    @Transactional
	public void update(Route route){
        if(route == null){
            throw new BusinessException("9003");
        }
        if(route.getRouteId() == null){
            throw new BusinessException("9003");
        }
        StringBuilder s = checkNewRecord(route);
        if(s.length() > 0){
            throw new BusinessException(s.toString());
        }
        routeMapper.updateByPrimaryKeySelective(route);

        List<RouteDetail> details = route.getDetails();
        if(CollectionUtils.isEmpty(details)){
            throw new BusinessException("明细数据为空！");
        }
        Environment env = Environment.getEnv();
        StringBuilder sub = new StringBuilder();

        List<RouteDetail> adds = new ArrayList<>();
        List<RouteDetail> updates = new ArrayList<>();
        List<Long> dels = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            RouteDetail detail = details.get(i);
            if(detail.getCityAreaId() == null){
                sub.append("明细数据第"+(i+1)+"行，虚拟城市为空！");
                continue;
            }
            switch (RowStatus.get(detail.getRowStatus())){
                case NEW:
                    detail.setCorpId(env.getCorp().getCorpId());
                    detail.setRouteId(route.getRouteId());
                    adds.add(detail);
                    break;
                case EDIT:
                    updates.add(detail);
                    break;
                case DELETE:
                    dels.add(detail.getRouteDetailId());
                    break;
                default:break;
            }
        }
        if(sub.length() > 0){
            throw new BusinessException(sub.toString());
        }
        try{
            if(CollectionUtils.isNotEmpty(adds)){
                routeDetailMapper.insert(adds);
            }
            if(CollectionUtils.isNotEmpty(updates)){
                routeDetailMapper.updateByPrimaryKeySelective(updates);
            }
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("明细数据虚拟路线重复！");
            }else{
                throw e;
            }
        }
        if(CollectionUtils.isNotEmpty(dels)){
            routeDetailMapper.deleteByPrimaryKey(dels);
        }
    }

    @Override
    @Transactional
    public void delete(Long routeId){
        if(routeId == null){
            throw new BusinessException("9003");
        }
        routeMapper.deleteByPrimaryKey(routeId);
        routeDetailMapper.deleteByRouteId(routeId);
    }

    @Override
    public PageInfo<Route> queryList(Map<String, Object> map, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo(queryList(map));
    }

    @Override
    public List<Route> queryList(Map<String, Object> map){
	    Environment env = Environment.getEnv();
	    if(map == null){
	        map = new HashMap<>();
        }
        map.put("corpId",env.getCorp().getCorpId());
	    return routeMapper.selectList(map);
    }

    @Override
    public List<RouteDetail> queryDetails(Long routeId){
        if(routeId == null){
            throw new BusinessException("9003");
        }
        return routeDetailMapper.selectByRouteId(routeId);
    }

	private StringBuilder checkNewRecord(Route route) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "routeCode", "路线编码", route.getRouteCode(), 20));
        s.append(CheckUtils.checkString("NO", "routeName", "路线名称", route.getRouteName(), 20));
        s.append(CheckUtils.checkInteger("NO", "routeType", "路线类型", route.getRouteType(), 10));
        s.append(CheckUtils.checkString("YES", "tag", "标签", route.getTag(), 50));
        s.append(CheckUtils.checkString("YES", "remark", "备注", route.getRemark(), 100));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", route.getFlag(), 10));
		return s;
	}

	@Override
	public List<Route> queryByRouteType(Integer routeType){
		Map<String,Object> map = new HashMap<>();
		map.put("routeType",routeType);
		List<Route> routes = routeMapper.selectList(map);
		List<RouteDetail> details = queryDetailByRouteType(routeType);
		Map<Long,List<Long>> cityAreaMap = new HashMap<>();
		for (RouteDetail detail : details) {
			List<Long> cityAreas = cityAreaMap.getOrDefault(detail.getRouteId(),new ArrayList<>());
			cityAreas.add(detail.getCityAreaId());
		}
		for (Route route : routes) {
			route.setCityAreas(cityAreaMap.get(route.getRouteId()));
		}
		return routes;

	}

    @Override
    public List<RouteDetail> queryDetailByRouteType(Integer routeType){
        return routeDetailMapper.selectByRouteType(routeType);
    }
}
