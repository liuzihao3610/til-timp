package com.tilchina.timp.service.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.AssemblyResultDetailMapper;
import com.tilchina.timp.model.AssemblyResultDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.AssemblyResult;
import com.tilchina.timp.service.AssemblyResultService;
import com.tilchina.timp.mapper.AssemblyResultMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 配板结果主表
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class AssemblyResultServiceImpl extends BaseServiceImpl<AssemblyResult> implements AssemblyResultService {

	@Autowired
    private AssemblyResultMapper assemblyResultMapper;

	@Autowired
    private AssemblyResultDetailMapper assemblyResultDetailMapper;
	
	@Override
	protected BaseMapper<AssemblyResult> getMapper() {
		return assemblyResultMapper;
	}

    @Override
    @Transactional
    public void add(List<AssemblyResult> records) {
        log.debug("addBatch: {}",records);
        try{
            if(CollectionUtils.isEmpty(records)){
                throw new BusinessException("9003");
            }
            assemblyResultMapper.insert(records);
            for (AssemblyResult record : records) {
                List<AssemblyResultDetail> details = record.getDetails();
                for (AssemblyResultDetail detail : details) {
                    detail.setAssemblyResultId(record.getAssemblyResultId());
                }
                assemblyResultDetailMapper.insert(details);
            }
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public List<AssemblyResultDetail> queryDetails(Long assemblyResultId){
	    if(assemblyResultId == null){
	        throw new BusinessException("9003");
        }
        return assemblyResultDetailMapper.selectByResultId(assemblyResultId);
    }

	@Override
	protected StringBuilder checkNewRecord(AssemblyResult assemblyresult) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "resultCode", "结果编号", assemblyresult.getResultCode(), 30));
        s.append(CheckUtils.checkBigDecimal("YES", "allTransPrice", "总运价", assemblyresult.getAllTransPrice(), 10, 0));
        s.append(CheckUtils.checkBigDecimal("YES", "rate", "毛利率", assemblyresult.getRate(), 10, 0));
        s.append(CheckUtils.checkBigDecimal("YES", "baseTransPrice", "基础运价", assemblyresult.getBaseTransPrice(), 10, 0));
        s.append(CheckUtils.checkBigDecimal("YES", "allSubsidy", "总补贴", assemblyresult.getAllSubsidy(), 10, 0));
        s.append(CheckUtils.checkInteger("NO", "maxQuantity", "板位数", assemblyresult.getMaxQuantity(), 10));
        s.append(CheckUtils.checkInteger("NO", "carCount", "商品车数量", assemblyresult.getCarCount(), 10));
        s.append(CheckUtils.checkInteger("NO", "cityCount", "城市数量", assemblyresult.getCityCount(), 10));
        s.append(CheckUtils.checkInteger("NO", "unitCount", "经销店数量", assemblyresult.getUnitCount(), 10));
        s.append(CheckUtils.checkString("YES", "transPlan", "运输计划", assemblyresult.getTransPlan(), 200));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "单据状态", assemblyresult.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", assemblyresult.getCreateDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(AssemblyResult assemblyresult) {
        StringBuilder s = checkNewRecord(assemblyresult);
        s.append(CheckUtils.checkPrimaryKey(assemblyresult.getAssemblyResultId()));
		return s;
	}
}
