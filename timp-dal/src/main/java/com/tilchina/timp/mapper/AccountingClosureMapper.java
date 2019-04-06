package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.AccountingClosure;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
* 关账
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface AccountingClosureMapper extends BaseMapper<AccountingClosure> {

	List<String> selectCarVinByDate(Date accountPeriod);
}
