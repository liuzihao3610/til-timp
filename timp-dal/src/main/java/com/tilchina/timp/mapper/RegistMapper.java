package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.Regist;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface RegistMapper extends BaseMapper<Regist> {

	 int logicDeleteByPrimaryKey(Long id);

	 int logicDeleteByPrimaryKeyList(int[] ids);

	 List<Regist> selectDynamicList();

	 List<Regist> selectByUserId(@Param("userId") Long userId);

    List<Regist> selectRefer(Map<String,Object> map);

    List<Regist> selectByRegistId(@Param("registIds")Set<Long> registIds);

    List<Regist> selectByDownList(@Param("upRegistId")Long upRegistId);

	Regist selectByRegistCode(@Param("registCode")String registCode);
}
