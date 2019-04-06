package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 卡领用记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class CardReceiveRecord extends SuperVO {

    private Long cardReceiveRecordId;   //领用记录ID
    private Long cardResourceId;   //卡ID
    private BigDecimal receiveBalance;   //领用时余额
    private Long driverId;   //领用人
    private Long transporterId;   //轿运车ID
    private Date receiveDate;   //领用时间
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId; //公司ID
    
    private String refCardResourceCode;//卡号
    private String refDriverName;//领用人名称
    private String refTractirPlateCode;//轿运车车头车牌号
    private String refCreateName;//创建人名称
    private String refCorpName; //公司名称

}

