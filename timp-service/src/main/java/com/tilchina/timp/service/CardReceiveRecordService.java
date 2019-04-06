package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.CardReceiveRecord;

/**
* 卡领用记录
*
* @version 1.0.0
* @author LiushuQi
*/
public interface CardReceiveRecordService {

	public CardReceiveRecord queryById(Long id);
    
    public PageInfo<CardReceiveRecord> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<CardReceiveRecord> queryList(Map<String, Object> map);
    
    public void add(CardReceiveRecord record);
    
    public void add(List<CardReceiveRecord> records);

    void updateSelective(CardReceiveRecord record);

    public void update(CardReceiveRecord record);
    
    public void update(List<CardReceiveRecord> records);
    
    public void deleteById(Long id);

	public CardReceiveRecord queryByCardId(Long cardResourceId);

}
