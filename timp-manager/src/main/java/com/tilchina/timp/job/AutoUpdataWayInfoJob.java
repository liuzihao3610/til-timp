package com.tilchina.timp.job;

import com.tilchina.auth.manager.RailtransOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @version 1.0.0 2018/4/22
 * @author WangShengguang
 */
@Component
@Slf4j
public class AutoUpdataWayInfoJob {

    @Autowired
    private RailtransOrderManager manager;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void update(){
        log.info("AutoUpdataWayInfoJob start...");
        try {
            manager.getLatestCabinStatus();
            log.info("AutoUpdataWayInfoJob finished.");
        }catch(Exception e){
            log.error("AutoUpdataWayInfoJob run error! ", e);
        }
    }
}
