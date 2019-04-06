package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.mapper.CorpRelationMapper;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.CorpRelation;
import com.tilchina.timp.service.CorpRelationService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* 公司关系表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CorpRelationServiceImpl extends BaseServiceImpl<CorpRelation> implements CorpRelationService {

	@Autowired
    private CorpRelationMapper corprelationmapper;

	@Autowired
	private CorpService corpService;
	
	@Override
	protected BaseMapper<CorpRelation> getMapper() {
		return corprelationmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CorpRelation corprelation) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("YES", "level", "级别(1~5)", corprelation.getLevel(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CorpRelation corprelation) {
        StringBuilder s = checkNewRecord(corprelation);
        s.append(CheckUtils.checkPrimaryKey(corprelation.getRelationId()));
		return s;
	}

	@Override
	public List<Corp> getRelation(Map<String, Object> params) {
		try {
			List<Corp> corps = corpService.getRelation(params);
			return corps;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void addRelation(Map<String, Object> params) {

		try {
			Long adsCorpId = NumberUtil.parseLong(params.get("adsCorpId").toString());
			List<Long> corpIds = JSON.parseArray(params.get("corpIds").toString()).toJavaList(Long.class);

			if (CollectionUtils.isEmpty(corpIds)) {
				return;
			}

			for (Long corpId : corpIds) {

				// 避免自己和自己建立关系
				if (corpId.equals(adsCorpId)) {
					continue;
				}

				// 双向维护关联关系
				CorpRelation corpRelationA = new CorpRelation();
				corpRelationA.setAdsCorpId(adsCorpId);
				corpRelationA.setCorpId(corpId);

				CorpRelation corpRelationB = new CorpRelation();
				corpRelationB.setAdsCorpId(corpId);
				corpRelationB.setCorpId(adsCorpId);

				try {
					add(corpRelationA);
					add(corpRelationB);
				} catch (Exception e) {
					// Ignore
				}
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void delRelation(Map<String, Object> params) {
		try {
			Long adsCorpId = NumberUtil.parseLong(params.get("adsCorpId").toString());
			List<Long> corpIds = JSON.parseArray(params.get("corpIds").toString()).toJavaList(Long.class);

			if (CollectionUtils.isEmpty(corpIds)) {
				return;
			}

			corpIds.forEach(corpId -> corprelationmapper.physicalDelete(corpId, adsCorpId));
			corpIds.forEach(corpId -> corprelationmapper.physicalDelete(adsCorpId, corpId));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
