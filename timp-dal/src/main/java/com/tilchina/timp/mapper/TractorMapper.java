package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Tractor;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 轿运车车头型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TractorMapper extends BaseMapper<Tractor> {

	void deleteList(int[] data);

	void updateBillStatus(Tractor tractor);

	List<Tractor> getReferenceList(Map<String, Object> map);

	void removeDate(Long tractorId);

}
