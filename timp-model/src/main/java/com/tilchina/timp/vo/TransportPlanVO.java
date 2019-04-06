package com.tilchina.timp.vo;

import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.model.TransportPlan;
import com.tilchina.timp.model.Transporter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* app:运输计划表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransportPlanVO  extends SuperVO {
	
	/*private Long transportPlanId;   //运输计划ID*/
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private StringBuilder description;	//文字描述路线信息
    private Integer cityCount;   //城市数量
    private Integer unitCount;   //经销店数量
    private Double predictKm;	//预计公里数
    private String startUnitName;	//起始地
    private String endUnitName;	//目的地

    private List<TransportPlan> arrivalPositions;	//停靠点/路线
    private Transporter truck;	//货物信息
}
