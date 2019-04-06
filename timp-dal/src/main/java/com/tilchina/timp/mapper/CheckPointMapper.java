package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.CheckPoint;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 检查站
*
* @version 1.0.0
* @author LiShuqi
*/
@Repository
public interface CheckPointMapper extends BaseMapper<CheckPoint> {

	void deleteList(int[] data);

	List<CheckPoint> getReferenceList(Map<String, Object> map);

	List<CheckPoint> getList(Map<String, Object> map);

}
