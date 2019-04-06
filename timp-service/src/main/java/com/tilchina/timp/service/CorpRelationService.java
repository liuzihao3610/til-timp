package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.CorpRelation;

import java.util.List;
import java.util.Map;

/**
* 公司关系表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CorpRelationService extends BaseService<CorpRelation> {

	List<Corp> getRelation(Map<String, Object> params);

	void addRelation(Map<String, Object> params);

	void delRelation(Map<String, Object> params);
}
