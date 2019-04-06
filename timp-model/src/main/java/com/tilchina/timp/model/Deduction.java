package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 扣款项目
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class Deduction extends SuperVO {

    private Long deductionId;   //扣款项目ID
    private String deductionName;   //扣款项目名称
    private Long creator;   //创建人
    private Date createDate;   //创建日期
    private Long corpId;
    private Integer flag;   //封存标志

    private String refCreatorName;
    private String refCorpName;
}

