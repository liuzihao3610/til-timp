package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 公司管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CorpManagement extends SuperVO {

    private Long managementId;   //主键
    private Long userId;   //用户ID
    private Long managementCorpId;   //可管理公司ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refUserName;
    private String refCorpCode;
    private String refCorpName;
    private String refCorpAddress;
    private String refContactName;
    private String refContactPhone;
    private Integer refCorpFlag;
}

