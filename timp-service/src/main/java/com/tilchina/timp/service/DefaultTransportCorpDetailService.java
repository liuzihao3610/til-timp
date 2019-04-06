package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DefaultTransportCorpDetail;

import java.util.List;

/**
* 默认运输公司明细表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface DefaultTransportCorpDetailService extends BaseService<DefaultTransportCorpDetail> {

	List<DefaultTransportCorpDetail> selectByDefaultCorpId(Long defaultCorpId);
}
