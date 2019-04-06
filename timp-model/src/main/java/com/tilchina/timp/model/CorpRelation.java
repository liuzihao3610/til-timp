package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* 公司关系表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CorpRelation extends SuperVO {

    private Long relationId;   //主键
    private Long corpId;   //公司ID
    private Long adsCorpId;   //归属公司ID
    private Integer level;   //级别(1~5)

}

