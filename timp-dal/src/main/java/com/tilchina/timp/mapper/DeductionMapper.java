package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Deduction;
import org.springframework.stereotype.Repository;

/**
* 扣款项目
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface DeductionMapper extends BaseMapper<Deduction> {

	Long queryByName(String deductionName);
}
