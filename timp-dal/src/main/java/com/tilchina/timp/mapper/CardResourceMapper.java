package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.CardResource;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 卡资源管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Repository
public interface CardResourceMapper extends BaseMapper<CardResource> {

	List<CardResource> getList(Map<String, Object> map);

    void updateDriverId(CardResource card);
}
