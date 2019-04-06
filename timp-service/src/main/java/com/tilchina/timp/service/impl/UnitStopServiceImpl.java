package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.UnitStopMapper;
import com.tilchina.timp.model.UnitStop;
import com.tilchina.timp.service.UnitStopService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 接车点管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class UnitStopServiceImpl extends BaseServiceImpl<UnitStop> implements UnitStopService {

	@Autowired
    private UnitStopMapper unitstopmapper;

	@Autowired
	private UnitStopService unitStopService;
	
	@Override
	protected BaseMapper<UnitStop> getMapper() {
		return unitstopmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(UnitStop unitstop) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "unitStopAdress", "停车点地址", unitstop.getUnitStopAdress(), 200));
        s.append(CheckUtils.checkInteger("YES", "continueTransport", "是否需要继续运输(0=", unitstop.getContinueTransport(), 10));
        s.append(CheckUtils.checkInteger("YES", "smallCartPay", "小板费支付方式(0=司机自付", unitstop.getSmallCartPay(), 10));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", unitstop.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志(0=", unitstop.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(UnitStop unitstop) {
        StringBuilder s = checkNewRecord(unitstop);
        s.append(CheckUtils.checkPrimaryKey(unitstop.getUnitStopId()));
		return s;
	}

	/**
	 * 通过ID 查询
	 * @param id
	 * @return
	 */
	@Override
	public UnitStop queryById(Long id) {
		log.debug("query: {}", id);
		return this.getMapper().selectByPrimaryKey(id);
	}

	/**
	 * 分页查询
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageInfo<UnitStop> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
		PageHelper.startPage(pageNum, pageSize);
		if (map!=null){
			DateUtil.addTime(map);
		}
		return new PageInfo(this.getMapper().selectList(map));
	}

	/**
	 * 查询所有
	 * @param map
	 * @return
	 */
	@Override
	public List<UnitStop> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return this.getMapper().selectList(map);
	}

	/**
	 * 新增接车点
	 * @param record
	 */
	@Override
	public void add(UnitStop record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			Environment environment=Environment.getEnv();
			UnitStop unitStop=unitStopService.getByUnitAdress(record.getUnitId(),record.getUnitStopAdress());
			if (unitStop != null){
				unitStop.setContinueTransport(record.getContinueTransport());
				unitStop.setSmallCartPay(record.getSmallCartPay());
				unitStop.setLat(record.getLat());
				unitStop.setLng(record.getLng());
				unitStop.setCreator(environment.getUser().getUserId());
				unitStop.setCreateDate(new Date());
				unitStop.setCorpId(environment.getCorp().getCorpId());
				unitstopmapper.updateByPrimaryKeySelective(unitStop);
			}else{
				record.setCreator(environment.getUser().getUserId());
				record.setCreateDate(new Date());
				record.setCorpId(environment.getCorp().getCorpId());
				unitstopmapper.insertSelective(record);
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
	 * 修改停车点信息
	 * @param record
	 */
	@Override
	public void updateSelective(UnitStop record) {
		log.debug("updateSelective: {}", record);

		try {
			UnitStop unitStop=unitstopmapper.selectByPrimaryKey(record.getUnitStopId());
			if (unitStop==null){
				throw new BusinessException("停车点不存在");
			}
			this.getMapper().updateByPrimaryKeySelective(record);
		} catch (Exception var3) {
			if (var3.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", var3);
			} else {
				throw var3;
			}
		}
	}

	/**
	 * 删除
	 * @param id
	 */
	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		UnitStop unitStop=unitstopmapper.selectByPrimaryKey(id);
		if (unitStop==null){
			throw new BusinessException("停车点不存在");
		}
		this.getMapper().deleteByPrimaryKey(id);
	}

	/**
	 * 封存
	 * @param data
	 */
	@Override
	public void sequestration(Long data) {
		UnitStop unitStop=unitstopmapper.selectByPrimaryKey(data);
		if (unitStop==null){
			throw new BusinessException("停车点不存在");
		}
		unitstopmapper.sequestration(data);
	}

	/**
	 * 取消封存
	 * @param data
	 */
	@Override
	public void unsequestration(Long data) {
		UnitStop unitStop=unitstopmapper.selectByPrimaryKey(data);
		if (unitStop==null){
			throw new BusinessException("停车点不存在");
		}
		unitstopmapper.unsequestration(data);
	}

	/**
	 * 通过收发货单位和接车点地址查询
	 * @param unitId
	 * @param unitStopAdress
	 * @return
	 */
	@Override
	@Transactional
	public UnitStop getByUnitAdress(Long unitId, String unitStopAdress) {

		return unitstopmapper.getByUnitAdress(unitId,unitStopAdress);
	}
}
