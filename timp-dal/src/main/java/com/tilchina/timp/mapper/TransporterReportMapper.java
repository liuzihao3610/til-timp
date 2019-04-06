package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.TransporterReport;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 骄运车状态变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransporterReportMapper {
	
    int deleteByPrimaryKey(Long id);

    TransporterReport selectByPrimaryKey(Long id);
    
    void insertSelective(TransporterReport record);
    
    List<TransporterReport> selectList(Map<String, Object> map);
	
}	
