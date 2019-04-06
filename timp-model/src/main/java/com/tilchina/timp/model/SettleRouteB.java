package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* 结算路线子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class SettleRouteB extends SuperVO {

    private Long settleRouteBId;   //主键
    private Long settleRouteId;   //结算路线主表ID
    private Integer sequence;   //序号
    private Long cityId;   //城市ID
    private String cityName;   //城市名称
    private String remark;   //备注
    private Long corpId;   //公司ID

    private String refSettleRouteName;//结算路线名称
    private String refCorpName;//公司名称

}

