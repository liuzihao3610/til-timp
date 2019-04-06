package com.tilchina.timp.model;

import lombok.*;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 用户角色关系表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class UserGroup extends SuperVO {

    private Long userGroupId;   //用户关系ID
    private Long userId;   //用户ID
    private Long groupId;   //角色ID
    private Long corpId;   //公司ID
    
    private String refGroupName;   //角色名称
    private String refUserName;   //用户名称
    private String refCorpName;   //公司名称

}

