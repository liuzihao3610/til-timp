package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.OilDepotSupplyRecord;

/**
* 入库记录
*
* @version 1.0.0
* @author LiushuQi
*/
public interface OilDepotSupplyRecordService {

	public OilDepotSupplyRecord queryById(Long id);
    
    public PageInfo<OilDepotSupplyRecord> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<OilDepotSupplyRecord> queryList(Map<String, Object> map);
    
    public void add(OilDepotSupplyRecord record);
    
    public void add(List<OilDepotSupplyRecord> records);

    void updateSelective(OilDepotSupplyRecord record);

    public void update(OilDepotSupplyRecord record);
    
    public void update(List<OilDepotSupplyRecord> records);
    
    public void deleteById(Long id);
}
