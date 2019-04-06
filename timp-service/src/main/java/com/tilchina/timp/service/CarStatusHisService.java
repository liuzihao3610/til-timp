package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CarStatusHis;

/**
* 商品车历史明细表
*
* @version 1.0.0
* @author LiShuqi
*/
public interface CarStatusHisService extends BaseService<CarStatusHis> {

	void deleteList(int[] data);

	void deleteByVin(String carVin);

}
