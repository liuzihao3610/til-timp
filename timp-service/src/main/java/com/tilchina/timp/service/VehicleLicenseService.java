package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.VehicleLicense;

import java.util.List;

/**
* 证件管理表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface VehicleLicenseService extends BaseService<VehicleLicense> {

	void setDocumentsCheckStatus(Long licenseId, Boolean checked);

	void executeScheduledTask();

	List<Long> getLicenseIdByVehicleId(Long vehicleId);

	Long getVehicleIdByLicenseId(Long licenseId);

	Boolean checkLicenseStatusByVehicleId(Long vehicleId);
}
