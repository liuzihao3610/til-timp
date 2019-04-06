package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.JobNotice;

/**
* 定时任务到期提醒
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface JobNoticeService extends BaseService<JobNotice> {
    /**
     * 通过定时任务编号查询任务
     * @param jobNoticeCode
     * @return
     */
    JobNotice queryByCode(String jobNoticeCode);
}
