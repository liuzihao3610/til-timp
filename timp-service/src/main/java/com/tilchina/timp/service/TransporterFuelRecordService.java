package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.TransporterFuelRecord;

/**
* 车辆加油记录
*
* @version 1.0.0
* @author LiushuQi
*/
public interface TransporterFuelRecordService {

	public TransporterFuelRecord queryById(Long id);
    
    public PageInfo<TransporterFuelRecord> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<TransporterFuelRecord> queryList(Map<String, Object> map);
    
    public void add(TransporterFuelRecord record);
    
    public void add(List<TransporterFuelRecord> records);

    void updateSelective(TransporterFuelRecord record);

    public void update(TransporterFuelRecord record);
    
    public void update(List<TransporterFuelRecord> records);
    
    public void deleteById(Long id);
}
