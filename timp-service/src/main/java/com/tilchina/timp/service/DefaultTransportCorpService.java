package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DefaultTransportCorp;

import java.util.List;
import java.util.Map;

/**
* 默认运输公司表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface DefaultTransportCorpService extends BaseService<DefaultTransportCorp> {

	PageInfo<List<Map<Long, String>>> getReferenceList(Long defaultCorpId, int pageNum, int pageSize);
}
