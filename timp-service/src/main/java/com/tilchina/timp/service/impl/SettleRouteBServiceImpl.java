package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.SettleRouteBMapper;
import com.tilchina.timp.model.SettleRouteB;
import com.tilchina.timp.service.SettleRouteBService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* 结算路线子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class SettleRouteBServiceImpl extends BaseServiceImpl<SettleRouteB> implements SettleRouteBService {

	@Autowired
    private SettleRouteBMapper settleroutebmapper;
	
	@Override
	protected BaseMapper<SettleRouteB> getMapper() {
		return settleroutebmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(SettleRouteB settlerouteb) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "sequence", "序号", settlerouteb.getSequence(), 10));
        s.append(CheckUtils.checkString("NO", "cityName", "城市名称", settlerouteb.getCityName(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "备注", settlerouteb.getRemark(), 200));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(SettleRouteB settlerouteb) {
        StringBuilder s = checkNewRecord(settlerouteb);
        s.append(CheckUtils.checkPrimaryKey(settlerouteb.getSettleRouteBId()));
		return s;
	}

	/**
	 * 通过结算路线子表ID查询信息
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public SettleRouteB queryById(Long id) {
		log.debug("query: {}", id);
		return settleroutebmapper.selectByPrimaryKey(id);
	}

	/**
	 * 分页查询所有结算路线子表
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	@Transactional
	public PageInfo<SettleRouteB> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
		PageHelper.startPage(pageNum, pageSize);
		if (map!=null){
			DateUtil.addTime(map);
		}
		return new PageInfo(settleroutebmapper.selectList(map));
	}

	/**
	 * 查询所有结算路线子表
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public List<SettleRouteB> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return settleroutebmapper.selectList(map);
	}

	/**
	 * 新增结算路线子表
	 * @param record
	 */
	@Override
	@Transactional
	public void add(SettleRouteB record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			} else {
				Environment environment=Environment.getEnv();
				record.setCorpId(environment.getCorp().getCorpId());
				settleroutebmapper.insertSelective(record);
			}
		} catch (Exception var4) {
			if (var4.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", var4);
			} else {
				throw var4;
			}
		}
	}

	/**
	 * 修改结算路线子表
	 * @param settleRouteB
	 */
	public void updateSelective(SettleRouteB settleRouteB) {
		log.debug("updateSelective: {}", settleRouteB);

		try {
			settleroutebmapper.updateByPrimaryKeySelective(settleRouteB);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 删除结算路线子表
	 * @param id
	 */
	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		settleroutebmapper.deleteByPrimaryKey(id);
	}

	/**
	 * 通过结算路线主表ID查询子表信息
	 * @param settleRouteId
	 * @return
	 */
	@Override
	@Transactional
	public List<SettleRouteB> getBySettleRouteId(Long settleRouteId) {
		return settleroutebmapper.getBySettleRouteId(settleRouteId);
	}
}
