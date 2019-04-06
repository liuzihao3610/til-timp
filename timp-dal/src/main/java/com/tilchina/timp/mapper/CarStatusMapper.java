package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CarStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 商品车明细表
*
* @version 1.0.0
* @author LiShuqi
*/
@Repository
public interface CarStatusMapper extends BaseMapper<CarStatus> {

	void deleteList(int[] data);

	CarStatus queryByVin(Map<String, Object> map);

	List<Map<String, Object>> selectVinList(Map<String, Object> m);

	List<CarStatus> queryByUnitId(Long unitId);

	CarStatus selectByVin(String carVin);

	void updateBillStatus(String carVin);

	void updateStatus(String carVin);

}
