package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DeductionMapper;
import com.tilchina.timp.model.Deduction;
import com.tilchina.timp.service.DeductionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 扣款项目
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class DeductionServiceImpl extends BaseServiceImpl<Deduction> implements DeductionService {

	@Autowired
    private DeductionMapper deductionMapper;
	
	@Override
	protected BaseMapper<Deduction> getMapper() {
		return deductionMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Deduction deduction) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "deductionName", "扣款项目名称", deduction.getDeductionName(), 50));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建日期", deduction.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", deduction.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Deduction deduction) {
        StringBuilder s = checkNewRecord(deduction);
        s.append(CheckUtils.checkPrimaryKey(deduction.getDeductionId()));
		return s;
	}

	@Override
	public Deduction queryById(Long id) {
		return super.queryById(id);
	}

	@Override
	public PageInfo<Deduction> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		return super.queryList(map, pageNum, pageSize);
	}

	@Override
	public List<Deduction> queryList(Map<String, Object> map) {
		return super.queryList(map);
	}

	@Override
	public void add(Deduction deduction) {

		try {
			StringBuilder sb;

			Environment env = Environment.getEnv();
			deduction.setCreator(env.getUser().getUserId());
			deduction.setCreateDate(new Date());
			deduction.setCorpId(env.getUser().getCorpId());

			sb = checkNewRecord(deduction);

			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			deductionMapper.insertSelective(deduction);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(List<Deduction> records) {
		super.add(records);
	}

	@Override
	public void updateSelective(Deduction deduction) {

		try {
			if (deductionMapper.selectByPrimaryKey(deduction.getDeductionId()) == null) {
				throw new BusinessException("9008", "扣款项目ID");
			}

			StringBuilder sb = new StringBuilder();
			sb.append(CheckUtils.checkLong("NO", "dedutionId", "扣款项目ID", deduction.getDeductionId(), 20));
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			deductionMapper.updateByPrimaryKeySelective(deduction);
		} catch (RuntimeException e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void update(Deduction record) {
		super.update(record);
	}

	@Override
	public void update(List<Deduction> records) {
		super.update(records);
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	@Override
	public boolean exists(Long deductionId) {
		try {
			Deduction deduction = queryById(deductionId);
			return deduction != null;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public boolean exists(String deductionName) {
		try {
			Long deductionId = queryByName(deductionName);
			return deductionId != null;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Long queryByName(String deductionName) {
		try {
			Long deductionId = deductionMapper.queryByName(deductionName);
			return deductionId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
