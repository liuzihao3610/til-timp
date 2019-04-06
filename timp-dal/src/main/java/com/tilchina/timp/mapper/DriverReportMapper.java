package com.tilchina.timp.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.DriverReport;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 司机提报记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface DriverReportMapper extends BaseMapper<DriverReport> {
	
	DriverReport selectByDriverId(Long driverId);

	List<DriverReport> getList(Map<String, Object> map);
	
	List<DriverReport> getParticipationDay(@Param("driverId") Long driverId, @Param("startDate") Date startDate);
	
}
