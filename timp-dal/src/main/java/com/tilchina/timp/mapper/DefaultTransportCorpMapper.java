package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.DefaultTransportCorp;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 默认运输公司表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface DefaultTransportCorpMapper extends BaseMapper<DefaultTransportCorp> {

	List<Map<Long, String>> getReferenceList(Long defaultCorpId);
}
