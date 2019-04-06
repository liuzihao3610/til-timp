package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 部门档案
*	
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Dept extends SuperVO {

    private Long deptId;   //部门ID
    private String deptCode;   //部门编码
    private String deptName;   //部门名称
    private Long upDeptId;   //上级部门
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCorpName; //公司名称
    private String refUpDeptName; //上级部门名称
    private String refCreateName;//创建人姓名
    
}

