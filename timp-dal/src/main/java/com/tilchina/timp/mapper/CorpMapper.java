package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Corp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 公司档案
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface CorpMapper extends BaseMapper<Corp> {

	void updateById(Long data);

	void deleteList(int[] data);
	
	List<Corp> getReferenceList(Map<String, Object> map);

	List<Corp> getTransCorp();

	Corp getByCorpName(String corpName);

	void updateSelective(Corp record);

	List<Corp> getList(Map<String, Object> map);

	// 通过公司ID查询当前公司的上级公司
	List<Long> queryHigherCorpByCorpId(Long corpId);

	List<Corp> selectByCorpName(@Param("corpNames") List<String> corpNames);

	List<Long> queryByName(String corpName);

	List<Corp> getRelation(Map<String, Object> params);

	List<Corp> getManagement(@Param("userId") Long userId);

	List<Corp> selectByNames(@Param("customerCorpNames")List<String> customerCorpNames);

    List<Corp> selectByCorpIds(@Param("corpIds") List<Long> corpIds);

	Long queryIdByName(@Param("corpName") String corpName);
}
