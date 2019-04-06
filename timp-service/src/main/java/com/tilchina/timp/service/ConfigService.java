package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Config;

/**
* 系统参数设置
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface ConfigService extends BaseService<Config> {

	void updateById(Long data);

	void deleteList(int[] data);

}
