package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.CardRechargeRecord;

/**
* 卡充值记录
*
* @version 1.0.0
* @author LiushuQi
*/
public interface CardRechargeRecordService {
	
	public CardRechargeRecord queryById(Long id);
    
    public PageInfo<CardRechargeRecord> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<CardRechargeRecord> queryList(Map<String, Object> map);
    
    public void add(CardRechargeRecord record);
    
    public void add(List<CardRechargeRecord> records);

    void updateSelective(CardRechargeRecord record);

    public void update(CardRechargeRecord record);
    
    public void update(List<CardRechargeRecord> records);
    
    public void deleteById(Long id);
}
