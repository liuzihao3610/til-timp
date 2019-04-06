package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Transporter;

import java.util.Map;

/**
* 轿运车档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransporterService extends BaseService<Transporter> {

	void logicDeleteById(Long id);

	void logicDeleteByIdList(int[]  ids);

	Transporter queryByDriverId(Long driverId);
	
	void updateCheck(Transporter transporter);

	void  deleteByIdList(int[]  ids);

	PageInfo<Transporter> getRefer(Map<String, Object> data, int pageNum, int pageSize);

	Transporter queryByContractorId(Long driverId);

	void updateContractorById(Map<String, Object> params);
}
