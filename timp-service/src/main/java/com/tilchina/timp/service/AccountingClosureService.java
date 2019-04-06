package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.AccountingClosure;

import java.util.Date;

/**
* 关账
*
* @version 1.0.0
* @author XueYuSong
*/
public interface AccountingClosureService extends BaseService<AccountingClosure> {

	void generateRecord(Date date);
}
