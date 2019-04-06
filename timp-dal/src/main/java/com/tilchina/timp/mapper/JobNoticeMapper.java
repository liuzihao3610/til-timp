package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.JobNotice;
import org.springframework.stereotype.Repository;

/**
* 定时任务到期提醒
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface JobNoticeMapper extends BaseMapper<JobNotice> {
    /**
     * 通过任务编号查询发送列表
     * @param jobNoticeCode
     * @return
     */
    JobNotice queryByCode(String jobNoticeCode);
}
