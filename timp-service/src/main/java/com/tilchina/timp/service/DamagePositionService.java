package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DamagePosition;

import java.util.List;
import java.util.Map;

/**
* 质损部位档案
*
* @version 1.0.0
* @author XueYuSong
*/
public interface DamagePositionService extends BaseService<DamagePosition> {

    List<Map<Long, String>> getReferenceList(Map<String, String> param);
}
