package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
* 结算路线主表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class SettleRoute extends SuperVO {

    private Long settleRouteId;   //主键
    private String settleRouteCode;   //路线编号
    private String settleRouteName;   //路线名称
    private String description;   //描述
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0=否 1=是)

    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称
    private List<SettleRouteB> settleRouteBS;//结算路线子表

}

