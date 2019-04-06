package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.OilPriceRecord;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 油价变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface OilPriceRecordMapper extends BaseMapper<OilPriceRecord> {

	void deleteList(int[] data);

	void removeDate(Long oilPriceRecordId);

	List<OilPriceRecord> getList(Map<String, Object> map);

}
