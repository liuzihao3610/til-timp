package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.enums.Level;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.Contacts;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.ContactsService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.UnitRelation;
import com.tilchina.timp.service.UnitRelationService;
import com.tilchina.timp.mapper.UnitRelationMapper;
import org.springframework.util.CollectionUtils;

import java.nio.file.OpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
* 公司收发货单位档案
*
* @version 1.0.0
* @author XiaHong
*/
@Service
@Slf4j
public class UnitRelationServiceImpl  implements UnitRelationService {

	@Autowired
    private UnitRelationMapper unitrelationmapper;

	@Autowired
	private UnitService unitService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private ContactsService contactsService;

	protected BaseMapper<UnitRelation> getMapper() {
		return unitrelationmapper;
	}

	protected StringBuilder checkNewRecord(UnitRelation unitrelation) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("YES", "level", "级别", unitrelation.getLevel(), 10));
		s.append(CheckUtils.checkInteger("YES", "level", "级别", unitrelation.getLevel(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(UnitRelation unitrelation) {
        StringBuilder s = checkNewRecord(unitrelation);
        s.append(CheckUtils.checkPrimaryKey(unitrelation.getUnitRelationId()));
		return s;
	}

	@Override
	public UnitRelation queryById(Long id) {
		log.debug("query: {}",id);
		return getMapper().selectByPrimaryKey(id);
	}

	@Override
	public PageInfo<UnitRelation> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<UnitRelation>(getMapper().selectList(map));
	}

	@Override
	public List<UnitRelation> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return getMapper().selectList(map);
	}

	@Override
	public void add(UnitRelation unitRelation) {
		log.debug("add: {}",unitRelation);
		StringBuilder s;
		try {
			s = checkNewRecord(unitRelation);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().insertSelective(unitRelation);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void add(List<UnitRelation> records) {
		log.debug("addBatch: {}",records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < records.size(); i++) {
				UnitRelation record = records.get(i);
				StringBuilder check = checkNewRecord(record);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().insert(records);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void updateSelective(UnitRelation record) {
		log.debug("updateSelective: {}",record);
		try {
			getMapper().updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void update(UnitRelation record) {
		log.debug("update: {}",record);
		StringBuilder s;
		try {
			s = checkUpdate(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().updateByPrimaryKey(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void update(List<UnitRelation> records) {
		log.debug("updateBatch: {}",records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < records.size(); i++) {
				UnitRelation record = records.get(i);
				StringBuilder check = checkUpdate(record);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().update(records);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}",id);
		getMapper().deleteByPrimaryKey(id);
	}

	@Override
	public List<UnitRelation> queryByUnitId(Long unitId) {
		log.debug("queryByUnitId: {}",unitId);
		return  unitrelationmapper.selectByUnitId(unitId);
	}

	@Override
	public List<UnitRelation> queryByCorpId(Long adsCorpId) {
		log.debug("queryByCorp: {}",adsCorpId);
		try {
			List<UnitRelation> unitRelations = unitrelationmapper.selectByCorpId(adsCorpId);
			// 添加收发货联系人信息
			List<Long> unitIds = new ArrayList<>();
			unitRelations.forEach(unit -> unitIds.add(unit.getUnitId()));
			List<Contacts> contactsList = contactsService.queryByUnitIds(unitIds);
			StringBuilder refUnitContacts = new StringBuilder() ;
			for (UnitRelation unitRelation : unitRelations) {
				for (Contacts contacts : contactsList) {
					if (unitRelation.getUnitId().longValue() == contacts.getUnitId().longValue()){
						refUnitContacts.append(contacts.getContactsName()+"："+contacts.getPhone()+"；");
					}
				}
				unitRelation.setRefUnitContacts(refUnitContacts.toString());
				refUnitContacts = new StringBuilder() ;
			}
			return unitRelations;
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	public void adds(Map<String, Object> map) {
		log.debug("adds: {}",map);
		StringBuilder errorMsg;
		try {
			errorMsg = new StringBuilder();
			Map<String, Object> data = Optional.ofNullable(map)
					.orElseThrow(() -> new BusinessException("9003"));
			if (data.get("unitIds") == null || data.get("adsCorpIds") == null){
				throw new BusinessException("9003");
			}
			List<Long> unitIds = JSON.parseArray(map.get("unitIds").toString()).toJavaList(Long.class);
			List<Long> adsCorpIds = JSON.parseArray(map.get("adsCorpIds").toString()).toJavaList(Long.class);
			if (CollectionUtils.isEmpty(unitIds)){
				throw new BusinessException("请选择【关联单位】的公司。");
			}
			if (CollectionUtils.isEmpty(adsCorpIds)){
				throw new BusinessException("请选择【关联单位】的单位。");
			}
			// 查询公司的关联单位信息:新增单位中存在已存在的单位
			List<UnitRelation> queryUnitRelations = queryByadsCorpIds(adsCorpIds);
			Map<Long,UnitRelation> unitIdMap = new HashMap<Long,UnitRelation>();
			queryUnitRelations.forEach(unitRelation -> unitIdMap.put(unitRelation.getUnitId(),unitRelation));
			Map<Long,UnitRelation> adsCorpIdMap = new HashMap<Long,UnitRelation>();
			queryUnitRelations.forEach(unitRelation -> adsCorpIdMap.put(unitRelation.getAdsCorpId(),unitRelation));
			List<Unit> units = unitService.queryByIds(unitIds);
			List<Corp> corps = corpService.queryByIds(adsCorpIds);
			Map<Long,Unit> unitMap = new HashMap<Long,Unit>();
			Map<Long,Corp> corpMap = new HashMap<Long,Corp>();
			units.forEach(unit -> unitMap.put(unit.getUnitId(),unit));
			corps.forEach(corp -> corpMap.put(corp.getCorpId(),corp));
			List<UnitRelation> unitRelations = new ArrayList<UnitRelation>();
			unitIds.forEach(unitId -> {
				if (unitMap.get(unitId) == null){
					errorMsg.append("收发货单位档案中此收发货单位ID："+unitId+"被删除或封存，不支持添加【关联单位】有效信息。");
				}
			});
			adsCorpIds.forEach(corpId -> {
				if (corpMap.get(corpId) == null){
					errorMsg.append("公司档案中此公司ID："+corpId+"被删除或封存，不支持添加【关联单位】信息。");
				}
			});
			if (StringUtils.isNotBlank(errorMsg.toString())) {
				throw new BusinessException(errorMsg.toString());
			}
			adsCorpIds.forEach(adsCorpId ->{
				unitIds.forEach(unitId ->{
					Corp corp = corpMap.get(adsCorpId);
					Unit unit = unitMap.get(unitId);
					UnitRelation unitRelation = new UnitRelation();
					unitRelation.setUnitId(unit.getUnitId());
					unitRelation.setAdsCorpId(corp.getCorpId());
					unitRelation.setLevel(Level.COMMON.getIndex());
					unitRelations.add(unitRelation);
				});
			});
			// 新增单位中存在已存在的单位，跳过已存在的数据，将不存在的数据添加
			List<UnitRelation> collect = unitRelations.stream().filter(unitRelation -> unitIdMap.get(unitRelation.getUnitId()) == null || adsCorpIdMap.get(unitRelation.getAdsCorpId()) == null).collect(Collectors.toList());
			add(collect);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public List<UnitRelation> queryByCorpId(Map<String, Object> data) {
		log.debug("queryByCorp: {}",data);
		try {
			if (data.get("adsCorpId") == null){
				throw new BusinessException("9003");
			}
            List<UnitRelation> unitRelations = unitrelationmapper.selectByCorpIdAndKey(data);
			List<Long> unitIds = new ArrayList<Long>();
            unitRelations.forEach(unitRelation -> unitIds.add(unitRelation.getUnitId()));
            // 添加收发货联系人信息
			if (!CollectionUtils.isEmpty(unitIds)){
				List<Unit> unitList = unitService.queryByIds(unitIds);
				List<Long> contactsIds = new ArrayList<Long>();
				unitList.forEach(unit -> contactsIds.add(unit.getUnitId()));
				List<Contacts> contactsList = contactsService.queryByUnitIds(unitIds);
				StringBuilder refUnitContacts = new StringBuilder() ;
				for (UnitRelation unitRelation : unitRelations) {
					for (Contacts contacts : contactsList) {
						if (unitRelation.getUnitId().longValue() == contacts.getUnitId().longValue()){
							refUnitContacts.append(contacts.getContactsName()+"："+contacts.getPhone()+"；");
						}
					}
					unitRelation.setRefUnitContacts(refUnitContacts.toString());
					refUnitContacts = new StringBuilder() ;
				}
			}
            return  unitRelations;
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public List<UnitRelation> queryByadsCorpIds(List<Long> adsCorpIds) {
		log.debug("queryByCorp: {}",adsCorpIds);
		try {
			if (CollectionUtils.isEmpty(adsCorpIds)){
				throw new BusinessException("9003");
			}
			return  unitrelationmapper.selectByadsCorpIds(adsCorpIds);
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

}
