package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Cargo;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* 货物装载信息表
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface CargoMapper extends BaseMapper<Cargo> {

    List<Cargo> selectByTrailers(@Param("trailerIds") List<Long> trailerIds);

	List<Cargo> getList(Map<String, Object> map);
}
