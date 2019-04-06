package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Group extends SuperVO {

    private Long groupId;   //角色ID
    private String groupCode;   //角色编码
    private String groupName;   //角色名称
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCorpName;	//公司名称
    private String refCreateName;//创建人姓名

}

