package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
*	权限管理 
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Power extends SuperVO {

    private Long powerId;   //权限ID
    private Long groupId;   //角色ID
    private Long registId;   //功能ID
    private Long corpId;   //公司ID
    
    private String refGroupNAME;   //角色名称
    private String refRegistNAME;   //功能名称
    private String refCorpNAME;   //公司名称
    
}

