package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.OilPrice;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 油价档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface OilPriceMapper extends BaseMapper<OilPrice> {

	void deleteList(int[] data);

	List<OilPrice> getReferenceList(Map<String, Object> map);

}
