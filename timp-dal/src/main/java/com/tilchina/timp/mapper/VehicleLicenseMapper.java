package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.VehicleLicense;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 证件管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface VehicleLicenseMapper extends BaseMapper<VehicleLicense> {

	void setDocumentsCheckStatus(Map<String, Object> params);

	List<VehicleLicense> queryForScheduledTask(@Param("min") Integer min, @Param("max") Integer max);

	void updateExpiredLicense(Object object);

	List<Long> getLicenseIdByVehicleId(@Param("vehicleId") Long vehicleId);

	Long getVehicleIdByLicenseId(@Param("licenseId") Long licenseId);

	VehicleLicense getLicenseByVehicleId(@Param("vehicleId") Long vehicleId, @Param("licenseType") Integer licenseTypes);
}
