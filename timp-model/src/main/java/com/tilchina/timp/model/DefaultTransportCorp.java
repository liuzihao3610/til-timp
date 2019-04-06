package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
* 默认运输公司表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class DefaultTransportCorp extends SuperVO {

    private Long defaultCorpId;   //ID
    private Long affiliatedCorpId;   //所属公司ID
    private Long recvCityId;   //收货城市ID
    private Long sendCityId;   //发货城市ID
    private Integer isUniversal;   //是否通用(0:否 1:是)
    private Long creator;   //创建人ID
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)

	private List<DefaultTransportCorpDetail> details;

	private String refAffiliatedCorpName;
	private String refRecvCityName;
	private String refSendCityName;
	private String refCreatorName;
	private String refCorpName;
}

