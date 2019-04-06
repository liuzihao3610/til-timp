package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.DamagePosition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 质损部位档案
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface DamagePositionMapper extends BaseMapper<DamagePosition> {

    List<Map<Long, String>> getReferenceList(String searchContent);
}
