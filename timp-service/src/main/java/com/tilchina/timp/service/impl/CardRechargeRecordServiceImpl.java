package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
import com.tilchina.timp.model.CardRechargeRecord;
import com.tilchina.timp.service.CardRechargeRecordService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CardRechargeRecordMapper;

/**
* 卡充值记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Service
@Slf4j
public class CardRechargeRecordServiceImpl implements CardRechargeRecordService {

	@Autowired
    private CardRechargeRecordMapper cardrechargerecordmapper;
	
	protected BaseMapper<CardRechargeRecord> getMapper() {
		return cardrechargerecordmapper;
	}
	
	private static boolean isNullAble(String nullable) {
        if ("YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase())) {
            return true;
        }
        return false;
    }
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment+"["+attName+"]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value "+decimal+ " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value "+decimal+ " scale out of range [" + scale + "].";
        }
        return "";
    }
	protected StringBuilder checkNewRecord(CardRechargeRecord cardrechargerecord) {
		StringBuilder s = new StringBuilder();
        s.append(checkBigDecimal("NO", "balanceBeforeRecharge", "充值前余额", cardrechargerecord.getBalanceBeforeRecharge(), 10, 2));
        s.append(checkBigDecimal("NO", "rechargeMoney", "充值金额", cardrechargerecord.getRechargeMoney(), 10, 2));
        s.append(CheckUtils.checkInteger("YES", "deduction", "是否可抵扣(0=否", cardrechargerecord.getDeduction(), 10));
        s.append(CheckUtils.checkLong("NO", "userId", "充值人", cardrechargerecord.getUserId(), 20));
        s.append(CheckUtils.checkDate("NO", "rechargeDate", "充值时间", cardrechargerecord.getRechargeDate()));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", cardrechargerecord.getCreateDate()));
		return s;
	}

	protected StringBuilder checkUpdate(CardRechargeRecord cardrechargerecord) {
        StringBuilder s = checkNewRecord(cardrechargerecord);
        s.append(CheckUtils.checkPrimaryKey(cardrechargerecord.getCardRechargeRecordId()));
		return s;
	}
	
	/**
	 * 通过ID查询
	 */
	@Override
	@Transactional
    public CardRechargeRecord queryById(Long id) {
        log.debug("query: {}",id);
        return getMapper().selectByPrimaryKey(id);
    }
	/**
	 * 分页查询
	 */
    @Override
    @Transactional
    public PageInfo<CardRechargeRecord> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return new PageInfo<CardRechargeRecord>(getMapper().selectList(map));
    }
    /**
     * 查询所有
     */
    @Override
    @Transactional
    public List<CardRechargeRecord> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }
    /**
     * 新增
     */
    @Override
    @Transactional
    public void add(CardRechargeRecord record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
//            if (record.getRechargeMoney().doubleValue()<0) {
//            	throw new BusinessException("充值金额有误,请核实");
//			}
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
    public void add(List<CardRechargeRecord> records){
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                CardRechargeRecord record = records.get(i);
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
    public void updateSelective(CardRechargeRecord record) {
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
    public void update(CardRechargeRecord record) {
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
    public void update(List<CardRechargeRecord> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                CardRechargeRecord record = records.get(i);
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
