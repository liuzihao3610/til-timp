package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 油价变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class OilPriceRecord extends SuperVO {

    private Long oilPriceRecordId;   //主键
    private Long oilPriceId;   //油价ID
    private BigDecimal price;   //变更后金额
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refOilName;//油价名称
    private String refCreateName;//创建人名称
    private String refCheckerName;//审核人名称
    private String refCorpName;//公司名称

}

