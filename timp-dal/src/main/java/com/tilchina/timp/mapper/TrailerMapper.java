package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Trailer;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 挂车型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TrailerMapper extends BaseMapper<Trailer> {

	void deleteList(int[] data);

	void updateBillStatus(Trailer trailer);

	List<Trailer> getReferenceList(Map<String, Object> map);

	void removeDate(Long trailerId);
	
	List<Trailer> selectByTrailerIds(@Param("trailerIds") List<Long> trailerIds);

}
