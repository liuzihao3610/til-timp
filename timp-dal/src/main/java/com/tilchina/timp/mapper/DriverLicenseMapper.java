package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.DriverLicense;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 驾驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface DriverLicenseMapper extends BaseMapper<DriverLicense> {

	void deleteList(int[] data);

	void deleteByDriverIdList(int[] data);

	void deleteByDriverId(Long driverId);

	DriverLicense selectByDriverId(long l);

	void unaudit(Long driverLicenseId);

	List<DriverLicense> getList(Map<String, Object> map);

}
