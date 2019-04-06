package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Config;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 系统参数设置
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface ConfigMapper extends BaseMapper<Config> {

	void updateById(Long data);

	void deleteList(int[] data);

}
