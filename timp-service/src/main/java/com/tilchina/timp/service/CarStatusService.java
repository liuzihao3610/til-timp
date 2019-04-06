package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CarStatus;

import java.util.List;
import java.util.Map;

/**
* 商品车明细表
*
* @version 1.0.0
* @author LiShuqi
*/
public interface CarStatusService extends BaseService<CarStatus> {

	void deleteList(int[] data);

	CarStatus queryByVin(Map<String, Object> map);

	List<Map<String, Object>> selectVinList(Map<String, Object> m);

	List<CarStatus> getCarList(Map<String, Object> map);

	List<CarStatus> queryByUnitId(Long unitId);

	void updateBillStatus(String data, String path);

	CarStatus selectByVin(String carVin);

	void updateStatus(String carVin);


	



}
