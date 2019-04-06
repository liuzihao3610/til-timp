package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Position extends SuperVO {

    private Long positionId;   //职务ID
    private String positionCode;   //职务编码
    private String positionName;   //职务名称
    private Long deptId;   //部门ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCorpName; //公司名称
    private String refDeptName; //部门名称
    private String refCreateName;//创建人姓名
    
}

