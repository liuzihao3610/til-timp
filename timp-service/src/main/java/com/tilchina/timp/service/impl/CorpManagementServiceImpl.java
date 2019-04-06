package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.mapper.CorpManagementMapper;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.CorpManagement;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.CorpManagementService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.UserService;
import com.tilchina.timp.util.IntegerUtil;
import com.tilchina.timp.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* 公司管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CorpManagementServiceImpl extends BaseServiceImpl<CorpManagement> implements CorpManagementService {

	@Autowired
    private CorpManagementMapper corpManagementMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private CorpService corpService;
	
	@Override
	protected BaseMapper<CorpManagement> getMapper() {
		return corpManagementMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CorpManagement corpmanagement) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("N", "userId", "用户", corpmanagement.getUserId(), 20));
		s.append(CheckUtils.checkLong("N", "corpId", "公司", corpmanagement.getCorpId(), 20));
		s.append(CheckUtils.checkLong("N", "creator", "创建人", corpmanagement.getCreator(), 20));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CorpManagement corpmanagement) {
        StringBuilder s = checkNewRecord(corpmanagement);
        s.append(CheckUtils.checkPrimaryKey(corpmanagement.getManagementId()));
		return s;
	}

	@Override
	public List<CorpManagement> getManagement(Map<String, Object> params) {

		try {
			Environment environment = Environment.getEnv();
			User user = userService.queryById(environment.getUser().getUserId());
			if (!IntegerUtil.getBoolean(user.getSuperManager())) {
				throw new RuntimeException("当前用户无操作权限，请使用超级管理员账号登录后重试。");
			}

			Long userId = NumberUtil.parseLong(params.get("userId").toString());
			// List<Corp> corps = corpService.getManagement(userId);
			return corpManagementMapper.getManagement(userId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void addManagement(Map<String, Object> params) {
		try {
			Environment environment = Environment.getEnv();
			User user = userService.queryById(environment.getUser().getUserId());
			if (!IntegerUtil.getBoolean(user.getSuperManager())) {
				throw new RuntimeException("当前用户无操作权限，请使用超级管理员账号登录后重试。");
			}

			Long userId = NumberUtil.parseLong(params.get("userId").toString());
			List<Long> corpIds = JSON.parseArray(params.get("corpIds").toString()).toJavaList(Long.class);

			if (CollectionUtils.isEmpty(corpIds)) {
				throw new RuntimeException("请选择公司后重试。");
			}

			for (Long corpId : corpIds) {
				CorpManagement corpManagement = new CorpManagement();
				corpManagement.setUserId(userId);
				corpManagement.setManagementCorpId(corpId);
				corpManagement.setCreator(environment.getUser().getUserId());
				corpManagement.setCorpId(environment.getUser().getCorpId());

				// 用于跳过已添加的公司
				try {
					add(corpManagement);
				} catch (Exception e) {
					// Ignore
				}
			}
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				throw new RuntimeException("数据重复，请检查后重试。");
			}
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void delManagement(Map<String, Object> params) {
		try {
			Environment environment = Environment.getEnv();
			User user = userService.queryById(environment.getUser().getUserId());
			if (!IntegerUtil.getBoolean(user.getSuperManager())) {
				throw new RuntimeException("当前用户无操作权限，请使用超级管理员账号登录后重试。");
			}

			Long userId = NumberUtil.parseLong(params.get("userId").toString());
			List<Long> corpIds = JSON.parseArray(params.get("corpIds").toString()).toJavaList(Long.class);

			if (CollectionUtils.isEmpty(corpIds)) {
				throw new RuntimeException("请选择公司后重试。");
			}

			params = new HashMap<>();
			params.put("managementCorpId", environment.getUser().getCorpId());
			params.put("userId", environment.getUser().getUserId());
			// 当前登录公司对应的公司管理表中的ID
			Long managementCorpId = corpManagementMapper.queryIdByManagementCorpId(params);
			// 去除当前登录公司ID
			corpIds.remove(managementCorpId);
			corpIds.forEach(corpId -> corpManagementMapper.physicalDelete(corpId));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<CorpManagement> queryByUserId(Long userId) {
		log.debug("queryList: {}", userId);
		return corpManagementMapper.selectByUserId(userId);
	}

	public List<Map<String, Object>> getCorpListByUserId(Long userId) {
		try {
			List<Map<String, Object>> corpList = new ArrayList<>();
			List<Long> corpIds = corpManagementMapper.getManagementCorpListByUserId(userId);
			for (Long corpId : corpIds) {
				Corp corp = corpService.queryById(corpId);
				if (Objects.nonNull(corp)) {
					Map<String, Object> corpMap = new HashMap<>();
					corpMap.put("corpName", corp.getCorpName());
					corpMap.put("corpId", corp.getCorpId());
					corpList.add(corpMap);
				}
			}
			return corpList;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> getSelfCorpList() {
		try {
			Environment environment = Environment.getEnv();
			return getCorpListByUserId(environment.getUser().getUserId());
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
