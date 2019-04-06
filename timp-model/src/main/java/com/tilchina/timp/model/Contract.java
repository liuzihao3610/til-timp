package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 合同管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class Contract extends SuperVO {

    private Long contractId;   //合同ID
    private String contractNumber;   //合同编号
    private Integer contractType;   //合同类型(0:正式 1:补充 2:口头)
    private Long partya;   //甲方
    private Long partyb;   //乙方
    private Long partyaManager;   //甲方经办人
    private Long partybManager;   //乙方经办人
    private Date signingDate;   //签订日期
    private Long signingPlace;   //签订地点
    private Date contractStartDate;   //合同有效期起
    private Date contractEndDate;   //合同有效期止
    private String contractAttachment;   //合同附件
    private Integer billStatus;   //单据状态(0:制单 1:审核 2:作废)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)

    private String refCreator;
    private String refChecker;
    private String refPartya;
    private String refPartyb;
    private String refPartyaManager;
    private String refPartybManager;
    private String refSigningPlace;
}

