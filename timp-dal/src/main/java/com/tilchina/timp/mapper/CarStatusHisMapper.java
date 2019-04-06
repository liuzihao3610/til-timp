package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.CarStatusHis;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 商品车历史明细表
*
* @version 1.0.0
* @author LiShuqi
*/
@Repository
public interface CarStatusHisMapper extends BaseMapper<CarStatusHis> {

	void deleteList(int[] data);

	void deleteByVin(String carVin);

}
