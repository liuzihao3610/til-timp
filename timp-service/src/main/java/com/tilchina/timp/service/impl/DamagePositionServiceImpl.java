package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DamagePositionMapper;
import com.tilchina.timp.model.DamagePosition;
import com.tilchina.timp.service.DamagePositionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 质损部位档案
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class DamagePositionServiceImpl extends BaseServiceImpl<DamagePosition> implements DamagePositionService {

	@Autowired
    private DamagePositionMapper damagePositionMapper;
	
	@Override
	protected BaseMapper<DamagePosition> getMapper() {
		return damagePositionMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DamagePosition damageposition) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "positionCode", "质损部位代号", damageposition.getPositionCode(), 10));
        s.append(CheckUtils.checkString("NO", "positionNameCn", "质损部位中文名称", damageposition.getPositionNameCn(), 50));
        s.append(CheckUtils.checkString("YES", "positionNameEn", "质损部位英文名称", damageposition.getPositionNameEn(), 50));
        s.append(CheckUtils.checkString("YES", "positionDescCn", "质损部位中文描述", damageposition.getPositionDescCn(), 200));
        s.append(CheckUtils.checkString("YES", "positionDescEn", "质损部位英文描述", damageposition.getPositionDescEn(), 200));
        s.append(CheckUtils.checkString("YES", "positionImage", "质损部位参考图片路径", damageposition.getPositionImage(), 100));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", damageposition.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", damageposition.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DamagePosition damageposition) {
        StringBuilder s = checkNewRecord(damageposition);
        s.append(CheckUtils.checkPrimaryKey(damageposition.getPositionId()));
		return s;
	}

	@Override
	public DamagePosition queryById(Long id) {
		return super.queryById(id);
	}

	@Override
	public PageInfo<DamagePosition> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		return super.queryList(map, pageNum, pageSize);
	}

	@Override
	public List<DamagePosition> queryList(Map<String, Object> map) {
		return super.queryList(map);
	}

	@Override
	public void add(DamagePosition damagePosition) {

		StringBuilder sb;
		try {
			Environment env = Environment.getEnv();
			damagePosition.setCreator(env.getUser().getUserId());
			damagePosition.setCreateDate(new Date());
			damagePosition.setCorpId(env.getUser().getCorpId());
			sb = checkNewRecord(damagePosition);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			damagePositionMapper.insertSelective(damagePosition);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(List<DamagePosition> records) {
		super.add(records);
	}

	@Override
	public void updateSelective(DamagePosition damagePosition) {

		StringBuilder sb = new StringBuilder();
		try {

			sb.append(CheckUtils.checkLong("NO", "positionId", "部位ID", damagePosition.getPositionId(), 20));
			if (damagePositionMapper.selectByPrimaryKey(damagePosition.getPositionId()) == null) {
				throw new BusinessException("9008", "质损ID");
			}
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			damagePositionMapper.updateByPrimaryKeySelective(damagePosition);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void update(DamagePosition record) {
		super.update(record);
	}

	@Override
	public void update(List<DamagePosition> records) {
		super.update(records);
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	@Override
	public List<Map<Long, String>> getReferenceList(Map<String, String> param) {

		return damagePositionMapper.getReferenceList(param.get("searchContent"));
	}
}
