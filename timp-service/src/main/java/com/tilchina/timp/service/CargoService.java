package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Cargo;

import java.util.List;
import java.util.Map;

/**
* 货物装载信息表
*
* @version 1.0.0
* @author WangShengguang
*/
public interface CargoService extends BaseService<Cargo> {

    List<Cargo> queryByTrailers(List<Long> trailerIds);

	PageInfo<Cargo> getList(Map<String, Object> data, int pageNum, int pageSize);
}
