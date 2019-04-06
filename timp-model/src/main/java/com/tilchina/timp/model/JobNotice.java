package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 定时任务到期提醒
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class JobNotice extends SuperVO {

    private Long jobNoticeId;   //主键
    private String jobNoticeCode;   //任务编号
    private String jobNoticeName;   //任务名称
    private String remark;   //描述
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID

    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称
    private List<JobNoticeB> jobNoticeBS;//定时任务子表


}

