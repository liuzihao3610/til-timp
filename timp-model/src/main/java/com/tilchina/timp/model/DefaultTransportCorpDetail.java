package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* 默认运输公司明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class DefaultTransportCorpDetail extends SuperVO {

    private Long defaultCorpDetailId;   //子表ID
    private Long defaultCorpId;   //主表ID
    private Integer sequenceNumber;   //序号
    private Long transportCorpId;   //运输公司ID
    private Long transferUnitId;   //中转单位ID
    private Integer jobType;   //作业类型
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)

    private String refTransportCorpName;
    private String refTransferUnitName;
    private String refCorpName;
}

