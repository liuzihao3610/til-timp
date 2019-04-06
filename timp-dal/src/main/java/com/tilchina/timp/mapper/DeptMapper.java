package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Dept;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface DeptMapper extends BaseMapper<Dept> {
	
	 int logicDeleteByPrimaryKey(Long id);
	 
	 int logicDeleteByPrimaryKeyList(int[] ids);
	 
	 List<Dept> selectDynamicList();
	 
	 List<Dept> selectRefer(Map<String, Object> map);

	List<Dept> getList(Map<String, Object> map);

    List<Dept> selectByDeptNames(@Param("deptNames") List<String> deptNames);
}
