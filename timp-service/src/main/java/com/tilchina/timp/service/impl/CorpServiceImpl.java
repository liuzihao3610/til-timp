package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CorpMapper;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.CorpRelation;
import com.tilchina.timp.service.CorpRelationService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 公司档案
 *
 * @author LiuShuqi
 * @version 1.1.0
 */
@Service
@Slf4j
public class CorpServiceImpl extends BaseServiceImpl<Corp> implements CorpService {

	@Autowired
	private CorpMapper corpMapper;

	@Autowired
	private CorpRelationService corpRelationService;

	@Override
	protected BaseMapper<Corp> getMapper() {
		return corpMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Corp corp) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkString("NO", "corpCode", "公司编码", corp.getCorpCode(), 20));
		s.append(CheckUtils.checkString("NO", "corpName", "公司名称", corp.getCorpName(), 40));
		s.append(CheckUtils.checkString("YES", "enName", "英文名称", corp.getEnName(), 40));
		s.append(CheckUtils.checkString("YES", "remark", "备注", corp.getRemark(), 200));
		s.append(CheckUtils.checkLong("NO", "creator", "创建人", corp.getCreator(), 20));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", corp.getFlag(), 10));
		s.append(CheckUtils.checkString("YES", "socialCreditCode", "统一社会信用代码", corp.getSocialCreditCode(), 20));
		s.append(CheckUtils.checkString("YES", "billingAddress", "开票信息", corp.getBillingAddress(), 100));
		s.append(CheckUtils.checkString("YES", "bankAccountName", "开票信息", corp.getBankAccountName(), 50));
		s.append(CheckUtils.checkString("YES", "bankAccountNumber", "开票信息", corp.getBankAccountNumber(), 20));
		s.append(CheckUtils.checkInteger("NO", "accountPeriodType", "账期类型", corp.getAccountPeriodType(), 10));
		s.append(CheckUtils.checkInteger("NO", "accountPeriodDays", "账期", corp.getAccountPeriodDays(), 10));

		if (StringUtils.isNotBlank(corp.getSocialCreditCode())) {
			s.append(RegexUtil.validate("统一社会信用代码", 18, "regex.string.uscc", corp.getSocialCreditCode()));
		}
		if (StringUtils.isNotBlank(corp.getBankAccountNumber())) {
			s.append(RegexUtil.validate("银行账户", 19, "regex.string.bankaccount", corp.getBankAccountNumber()));
		}

		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Corp corp) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "corpId", "公司ID", corp.getCorpId(), 20));
		s.append(CheckUtils.checkString("NO", "corpName", "公司名称", corp.getCorpName(), 40));
		s.append(CheckUtils.checkString("YES", "enName", "英文名称", corp.getEnName(), 40));
		s.append(CheckUtils.checkString("YES", "remark", "备注", corp.getRemark(), 200));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", corp.getFlag(), 10));
		s.append(CheckUtils.checkString("YES", "socialCreditCode", "统一社会信用代码", corp.getSocialCreditCode(), 20));
		s.append(CheckUtils.checkString("YES", "billingAddress", "开票信息", corp.getBillingAddress(), 100));
		s.append(CheckUtils.checkString("YES", "bankAccountName", "开票信息", corp.getBankAccountName(), 50));
		s.append(CheckUtils.checkString("YES", "bankAccountNumber", "开票信息", corp.getBankAccountNumber(), 20));
		s.append(CheckUtils.checkInteger("NO", "accountPeriodType", "账期类型", corp.getAccountPeriodType(), 10));
		s.append(CheckUtils.checkInteger("NO", "accountPeriodDays", "账期", corp.getAccountPeriodDays(), 10));

		if (StringUtils.isNotBlank(corp.getSocialCreditCode())) {
			s.append(RegexUtil.validate("统一社会信用代码", 18, "regex.string.uscc", corp.getSocialCreditCode()));
		}
		if (StringUtils.isNotBlank(corp.getBankAccountNumber())) {
			s.append(RegexUtil.validate("银行账户", 19, "regex.string.bankaccount", corp.getBankAccountNumber()));
		}
		return s;
	}


	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null == data || 0 == data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		corpMapper.deleteList(data);
	}


	@Override
	@Transactional
	public void update(List<Corp> records) {
		log.debug("updateBatch: {}", records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			if (null == records || 0 == records.size()) {
				throw new BusinessException(LanguageUtil.getMessage("9999"));
			}
			for (int i = 0; i < records.size(); i++) {
				Corp record = records.get(i);
				StringBuilder check = checkUpdate(record);
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r/n");
				}
				corpMapper.updateByPrimaryKeySelective(records.get(i));
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	@Transactional
	public PageInfo<Corp> getReferenceList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);

		try {
			if (map != null && map.get("corpTypeId") != null) {
				String corpTypeIds = map.get("corpTypeId").toString();
				int flag = corpTypeIds.indexOf(",");
				if (flag != -1) {
					String[] string = map.get("corpTypeId").toString().split(",");
					map.put("corpTypeId", string);
				} else {
					String[] string = new String[1];
					string[0] = corpTypeIds;
					map.put("corpTypeId", string);
				}
			}
			List<Corp> list = corpMapper.getReferenceList(map);
			return new PageInfo<Corp>(list);
		} catch (Exception e) {
			throw e;
		}
	}

	//新增
	@Override
	@Transactional
	public void add(Corp record) {
		try {
			Environment env = Environment.getEnv();
			record.setCorpCode(DateUtil.dateToStringCode("CP", new Date()));
			record.setCreator(env.getUser().getUserId());

			StringBuilder s = checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			corpMapper.insertSelective(record);

			// 双向维护公司关系
			CorpRelation corpRelationA = new CorpRelation();
			corpRelationA.setCorpId(env.getCorp().getCorpId());
			corpRelationA.setAdsCorpId(record.getCorpId());
			corpRelationService.add(corpRelationA);

			CorpRelation corpRelationB = new CorpRelation();
			corpRelationB.setCorpId(record.getCorpId());
			corpRelationB.setAdsCorpId(env.getCorp().getCorpId());
			corpRelationService.add(corpRelationB);
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				throw new RuntimeException("公司名称重复，请检查后重试。");
			}
			log.error("{}", e);
			throw e;
		}
	}


	//部分更新
	@Override
	@Transactional
	public void updateSelective(Corp record) {
		try {
			Corp model = new Corp();
			model.setCorpId(record.getCorpId());
			model.setCorpName(record.getCorpName());
			model.setEnName(record.getEnName());
			model.setCorpTypeId(record.getCorpTypeId());
			model.setRemark(record.getRemark());
			model.setUpCorpId(record.getUpCorpId());
			model.setFlag(record.getFlag());
			model.setSocialCreditCode(record.getSocialCreditCode());
			model.setBillingAddress(record.getBillingAddress());
			model.setBankAccountName(record.getBankAccountName());
			model.setBankAccountNumber(record.getBankAccountNumber());
			model.setAccountPeriodType(record.getAccountPeriodType());
			model.setAccountPeriodDays(record.getAccountPeriodDays());
			model.setContactName(record.getContactName());
			model.setContactPhone(record.getContactPhone());
			model.setAccountOpeningAddress(record.getAccountOpeningAddress());
			model.setAccountOpeningPhone(record.getAccountOpeningPhone());
			model.setCorpAddress(record.getCorpAddress());
			model.setCorpPhone(record.getCorpPhone());

			StringBuilder checkResult = checkUpdate(model);
			if (!StringUtils.isBlank(checkResult)) {
				throw new BusinessException("数据检查失败：" + checkResult.toString());
			}
			corpMapper.updateSelective(model);
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				throw new RuntimeException("公司名称重复，请重新输入。");
			}
			log.error("{}", e);
			throw e;
		}
	}

	//通过ID查询
	@Override
	@Transactional
	public Corp queryById(Long id) {
		log.debug("query: {}", id);
		StringBuilder s = new StringBuilder();
		try {
			s = s.append(CheckUtils.checkLong("NO", "corpId", "公司ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			return corpMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	//通过ID删除
	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		StringBuilder s = new StringBuilder();
		try {
			s = s.append(CheckUtils.checkLong("NO", "corpId", "公司ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			corpMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

	}

	@Override
	@Transactional
	public List<Corp> getTransCorp() {
		try {
			Environment environment = Environment.getEnv();
			long corpId = environment.getCorp().getCorpId();
			return corpMapper.getTransCorp();
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public Corp getByCorpName(String shipToPartyName) {
		try {
			return corpMapper.getByCorpName(shipToPartyName);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 查询结果按创建时间倒序排序
	 */
	@Override
	@Transactional
	public PageInfo<Corp> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);

		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Corp>(corpMapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Long> queryHigherCorpByCorpId(Long corpId) {

		try {
			List<Long> corpIds = corpMapper.queryHigherCorpByCorpId(corpId);
			return corpIds;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Long> queryByName(String corpName) {

		List<Long> corpIds = corpMapper.queryByName(corpName);
		return corpIds;
	}

	@Override
	public List<Corp> getRelation(Map<String, Object> params) {
		try {
			List<Corp> corps = corpMapper.getRelation(params);
			return corps;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Corp> getManagement(Long userId) {
		try {
			List<Corp> corps = corpMapper.getManagement(userId);
			return corps;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Corp> queryByNames(List<String> customerCorpNames) {
		List<Corp> corpIds = corpMapper.selectByNames(customerCorpNames);
		return corpIds;
	}

	@Override
	public List<Corp> queryByIds(List<Long> corpIds) {
		if (CollectionUtils.isEmpty(corpIds)) {
			throw new BusinessException("查询【收发货单位】请求参数错误！");
		}
		return corpMapper.selectByCorpIds(corpIds);
	}

	@Override
	@Transactional
	public PageInfo<Corp> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		try {
			Environment environment = Environment.getEnv();
			if (Objects.isNull(map)) {
				map = new HashMap<>();
			}
			map.put("currentCorpId", environment.getUser().getCorpId());

			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo(corpMapper.selectList(map));
		} catch (Exception e) {
			log.error("{}");
			throw e;
		}
	}

	@Override
	public Long queryIdByName(String corpName) {
		try {
			return corpMapper.queryIdByName(corpName);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
