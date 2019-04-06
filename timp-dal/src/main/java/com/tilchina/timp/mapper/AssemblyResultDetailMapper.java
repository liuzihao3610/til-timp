package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.AssemblyResultDetail;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 配板结果子表
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface AssemblyResultDetailMapper extends BaseMapper<AssemblyResultDetail> {

    List<AssemblyResultDetail> selectByResultId(Long assemblyResultId);
}
