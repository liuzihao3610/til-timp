package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.OilDepot;
import com.tilchina.timp.model.TransporterFuelRecord;
import com.tilchina.timp.service.OilDepotService;
import com.tilchina.timp.service.TransporterFuelRecordService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterFuelRecordMapper;

/**
* 车辆加油记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Service
@Slf4j
public class TransporterFuelRecordServiceImpl implements TransporterFuelRecordService {

	@Autowired
    private TransporterFuelRecordMapper transporterfuelrecordmapper;
	
	@Autowired
	private OilDepotService oilDepotService;
	
	protected BaseMapper<TransporterFuelRecord> getMapper() {
		return transporterfuelrecordmapper;
	}

	protected StringBuilder checkNewRecord(TransporterFuelRecord transporterfuelrecord) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkDate("NO", "fuelDate", "加油时间", transporterfuelrecord.getFuelDate()));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", transporterfuelrecord.getCreateDate()));
		return s;
	}

	protected StringBuilder checkUpdate(TransporterFuelRecord transporterfuelrecord) {
        StringBuilder s = checkNewRecord(transporterfuelrecord);
        s.append(CheckUtils.checkPrimaryKey(transporterfuelrecord.getTransporterFuelRecordId()));
		return s;
	}
	
	/**
	 * 通过ID查询
	 */
	@Override
	@Transactional
    public TransporterFuelRecord queryById(Long id) {
        log.debug("query: {}",id);
        return getMapper().selectByPrimaryKey(id);
    }
	/**
	 * 分页查询
	 */
    @Override
    @Transactional
    public PageInfo<TransporterFuelRecord> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo<TransporterFuelRecord>(getMapper().selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }
    /**
     * 查询所有
     */
    @Override
    @Transactional
    public List<TransporterFuelRecord> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }
    /**
     * 新增
     */
    @Override
    @Transactional
    public void add(TransporterFuelRecord record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment environment=Environment.getEnv();
            record.setCreator(environment.getUser().getUserId());
            record.setCorpId(environment.getCorp().getCorpId());
            getMapper().insertSelective(record);
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else if (e instanceof BusinessException) {
				throw e;
			}else{
                throw new BusinessException("保存失败！", e);
            }
        }
    }
    /**
     * 批量新增
     */
    @Override
    @Transactional
    public void add(List<TransporterFuelRecord> records){
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                TransporterFuelRecord record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().insert(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("批量保存失败！", e);
            }
        }
    }
    /**
     * 部分更新
     */
    @Override
    @Transactional
    public void updateSelective(TransporterFuelRecord record) {
        log.debug("updateSelective: {}",record);
        try {
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }
    /**
     * 全部更新
     */
    @Override
    @Transactional
    public void update(TransporterFuelRecord record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = checkUpdate(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().updateByPrimaryKey(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }
    /**
     * 批量更新
     */
    @Override
    @Transactional
    public void update(List<TransporterFuelRecord> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                TransporterFuelRecord record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().update(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("批量更新失败！", e);
            }
        }

    }
    /**
     * 通过ID删除
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        getMapper().deleteByPrimaryKey(id);
    }
}
