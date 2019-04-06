package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.JobNoticeB;
import com.tilchina.timp.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 定时到期任务提醒子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface JobNoticeBMapper extends BaseMapper<JobNoticeB> {
    /**
     * 通过定时任务主表ID查询子表
     * @param id
     * @return
     */
    List<JobNoticeB> queryByJobNoticeId(Long id);

    /**
     * 通过任务编号查询发送列表
     * @param jobNoticeCode
     * @return
     */
    List<User> queryByJobNoticeCode(String jobNoticeCode);
}
