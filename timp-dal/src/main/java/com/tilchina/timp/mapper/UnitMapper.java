package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Unit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 收发货单位
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface UnitMapper extends BaseMapper<Unit> {

	void updateById(Long data);

	void deleteList(int[] data);

	List<Unit> getReferenceList(Map<String, Object> map);

	Unit getSingleUnit(Map<String, Object> map);

	Unit getContactInfoById(Long unitId);

	Unit getByName(String unitName);

	Unit queryListByNames(String unitName);

	List<Unit> queryByNames(@Param("names") Set<String> names);

	List<Unit> queryByName(String unitName);

	List<Unit> selectByUnitIds(@Param("unitIds") List<Long> unitIds);

	List<Long> queryByUnitName(@Param("unitName") String unitName);
}
