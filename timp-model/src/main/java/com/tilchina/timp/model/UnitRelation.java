package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 公司收发货单位档案
*
* @version 1.0.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class UnitRelation extends SuperVO {

    private Long unitRelationId;  //   公司收发货单位档案ID
    private Long unitId;   //收发货单位ID
    private Long adsCorpId;   //归属公司ID
    private Integer level;   //级别

    private String  refUnitContacts;    // 联系人信息：姓名：手机号码；
    private String refUnitAddress;   // 收发货单位地址
    private String refUnitCode;   // 收发货单位编码
    private String refAdsCorpName;   // 归属公司名称
    private String refUnitName;   // 收发货单位名称
    private String refDealerCode;   // 经销商代码

}

