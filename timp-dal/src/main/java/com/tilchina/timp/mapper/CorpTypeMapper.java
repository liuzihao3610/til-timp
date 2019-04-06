package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CorpType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 公司类型
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface CorpTypeMapper extends BaseMapper<CorpType> {

	List<CorpType> getCorpTypeNameList(Map<String, Object> map);
	void deleteList(int[] data);
	List<CorpType> getReferenceList(Map<String, Object> map);
	CorpType queryByCorpId(Long corpCarrierId);

}
