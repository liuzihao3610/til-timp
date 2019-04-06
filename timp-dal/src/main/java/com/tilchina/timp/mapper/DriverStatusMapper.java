package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.DriverStatus;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface DriverStatusMapper extends BaseMapper<DriverStatus> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	DriverStatus selectByDriverId(@Param("driverId") Long driverId);
	
	int updateByDriverId (DriverStatus driverStatus);
	
	List<DriverStatus> selectRefer(Map<String, Object> map);

	DriverStatus selectByDriverIdAndTransportId(@Param("driverId")Long driverId, @Param("transportId")Long transportId);
	
}
