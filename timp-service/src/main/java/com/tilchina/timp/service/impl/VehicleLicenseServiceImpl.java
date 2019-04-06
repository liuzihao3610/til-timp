package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.mapper.VehicleLicenseMapper;
import com.tilchina.timp.model.User;
import com.tilchina.timp.model.VehicleLicense;
import com.tilchina.timp.service.JobNoticeBService;
import com.tilchina.timp.service.VehicleLicenseService;
import com.tilchina.timp.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* 证件管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class VehicleLicenseServiceImpl extends BaseServiceImpl<VehicleLicense> implements VehicleLicenseService {

	@Autowired
    private VehicleLicenseMapper vehiclelicensemapper;

	@Autowired
	private JobNoticeBService jobNoticeService;
	
	@Override
	protected BaseMapper<VehicleLicense> getMapper() {
		return vehiclelicensemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(VehicleLicense vehiclelicense) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "licenseNumber", "证件号码", vehiclelicense.getLicenseNumber(), 20));
        s.append(CheckUtils.checkInteger("NO", "licenseType", "证件类型", vehiclelicense.getLicenseType(), 10));
        s.append(CheckUtils.checkDate("NO", "startValidDate", "有效期起", vehiclelicense.getStartValidDate()));
        s.append(CheckUtils.checkDate("NO", "endValidDate", "有效期止", vehiclelicense.getEndValidDate()));
        s.append(CheckUtils.checkInteger("NO", "licenseStatus", "证件状态", vehiclelicense.getLicenseStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建日期", vehiclelicense.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", vehiclelicense.getCheckDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", vehiclelicense.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(VehicleLicense vehiclelicense) {
        StringBuilder s = checkNewRecord(vehiclelicense);
        s.append(CheckUtils.checkPrimaryKey(vehiclelicense.getLicenseId()));
		return s;
	}

	@Override
	public void add(VehicleLicense record) {
		try {
			if (StringUtils.isBlank(record.getLicenseNumber())) {
				throw new RuntimeException("证件号码为空，请检查后重试。");
			}
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}
			this.getMapper().insertSelective(record);
		} catch (Exception e) {
			log.error("{}", e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("当前轿运车已有一张该类型证件，请勿重复添加。", e);
			}
			throw e;
		}
	}

	@Override
	public void updateSelective(VehicleLicense record) {
		try {
			if (StringUtils.isBlank(record.getLicenseNumber())) {
				throw new RuntimeException("证件号码为空，请检查后重试。");
			}
			this.getMapper().updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			log.error("{}", e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("当前轿运车已有一张该类型证件，请勿重复添加。", e);
			}
			throw e;
		}
	}

	@Override
	public void setDocumentsCheckStatus(Long licenseId, Boolean checked) {
		try {
			VehicleLicense vehicleLicense = Optional.ofNullable(queryById(licenseId)).orElseThrow(() -> new RuntimeException("ID不存在，请检查后重试。"));

			Environment environment = Environment.getEnv();

			Map<String, Object> params = new HashMap<>();
			params.put("licenseId", licenseId);

			if (checked) {
				if (vehicleLicense.getLicenseStatus() != BillStatus.Drafted.getIndex()) {
					throw new RuntimeException("当前单据非制单状态，请检查后重试。");
				}
				params.put("checker", environment.getUser().getUserId());
				params.put("checkDate", new Date());
				params.put("licenseStatus", BillStatus.Audited.getIndex());
			} else {
				if (vehicleLicense.getLicenseStatus() != BillStatus.Audited.getIndex()) {
					throw new RuntimeException("当前单据非审核状态，请检查后重试。");
				}
				params.put("checker", null);
				params.put("checkDate", null);
				params.put("licenseStatus", BillStatus.Drafted.getIndex());
			}

			vehiclelicensemapper.setDocumentsCheckStatus(params);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void executeScheduledTask() {

		try {
			vehiclelicensemapper.updateExpiredLicense(new Object());

			List<User> users = jobNoticeService.queryByJobNoticeCode("3456");
			List<String> addresses = users.stream().filter(user -> StringUtils.isNotBlank(user.getEmail())).map(User::getEmail).collect(Collectors.toList());

			List<VehicleLicense> thirtyDaysLeft = vehiclelicensemapper.queryForScheduledTask(15, 30);
			List<VehicleLicense> fiftyDaysLeft  = vehiclelicensemapper.queryForScheduledTask(7, 15);
			List<VehicleLicense> sevenDaysleft  = vehiclelicensemapper.queryForScheduledTask(0, 7);

			if (CollectionUtils.isNotEmpty(thirtyDaysLeft)) {
				String subject = String.format("%d证件将于30天内到期", thirtyDaysLeft.size());
				StringBuilder message = new StringBuilder();
				for (VehicleLicense vehicleLicense : thirtyDaysLeft) {
					message.append(String.format("证件号: %s于30天内即将到期, 轿运车编号为%s, 请及时更新。%n", vehicleLicense.getLicenseNumber(), vehicleLicense.getRefVehicleCode()));
				}
				EmailUtil.send(subject, message.toString(), addresses);
			}

			if (CollectionUtils.isNotEmpty(fiftyDaysLeft)) {
				String subject = String.format("%d证件将于15天内到期", fiftyDaysLeft.size());
				StringBuilder message = new StringBuilder();
				for (VehicleLicense vehicleLicense : fiftyDaysLeft) {
					message.append(String.format("证件号: %s于15天内即将到期, 轿运车编号为%s, 请及时更新。%n", vehicleLicense.getLicenseNumber(), vehicleLicense.getRefVehicleCode()));
				}
				EmailUtil.send(subject, message.toString(), addresses);
			}

			if (CollectionUtils.isNotEmpty(sevenDaysleft)) {
				String subject = String.format("%d证件将于7天内到期", sevenDaysleft.size());
				StringBuilder message = new StringBuilder();
				for (VehicleLicense vehicleLicense : sevenDaysleft) {
					message.append(String.format("证件号: %s于7天内即将到期, 轿运车编号为%s, 请及时更新。%n", vehicleLicense.getLicenseNumber(), vehicleLicense.getRefVehicleCode()));
				}
				EmailUtil.send(subject, message.toString(), addresses);
			}
		} catch (Exception e) {
			log.error("{}", e);
		}
	}

	@Override
	public List<Long> getLicenseIdByVehicleId(Long vehicleId) {
		try {
			List<Long> licenseIds = vehiclelicensemapper.getLicenseIdByVehicleId(vehicleId);
			return licenseIds;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Long getVehicleIdByLicenseId(Long licenseId) {
		try {
			Long vehicleId = vehiclelicensemapper.getVehicleIdByLicenseId(licenseId);
			return vehicleId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Boolean checkLicenseStatusByVehicleId(Long vehicleId) {
		VehicleLicense drivingLicense   = vehiclelicensemapper.getLicenseByVehicleId(vehicleId, 0);
		VehicleLicense operationLicense = vehiclelicensemapper.getLicenseByVehicleId(vehicleId, 1);
		VehicleLicense insuranceLicense = vehiclelicensemapper.getLicenseByVehicleId(vehicleId, 2);

		return ObjectUtils.allNotNull(drivingLicense, operationLicense, insuranceLicense);
	}
}
