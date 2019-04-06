package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.DefaultTransportCorpDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 默认运输公司明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface DefaultTransportCorpDetailMapper extends BaseMapper<DefaultTransportCorpDetail> {

	List<DefaultTransportCorpDetail> selectByDefaultCorpId(Long defaultCorpId);
}
