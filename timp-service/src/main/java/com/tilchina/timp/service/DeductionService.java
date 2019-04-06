package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Deduction;

/**
* 扣款项目
*
* @version 1.0.0
* @author XueYuSong
*/
public interface DeductionService extends BaseService<Deduction> {

	boolean exists(Long deductionId);

	boolean exists(String deductionName);

	Long queryByName(String deductionName);
}
