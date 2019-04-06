package com.tilchina.timp.job;/*
 * @author XueYuSong
 * @date 2018-07-17 15:00
 */

import com.tilchina.timp.service.VehicleLicenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutoNotifierForVehicleLicense {

	@Autowired
	private VehicleLicenseService vehicleLicenseService;

//	@Scheduled(cron = "0 0 1 * * *")
	@Scheduled(cron = "0 0/5 * * * *")
	public void update() {
		log.info("证件管理邮件自动提醒任务启动");
		vehicleLicenseService.executeScheduledTask();
	}
}
