package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运输记录表（外）
*
* @version 1.1.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class TransportRecordOuter extends SuperVO {

    private Long transportRecordOuterId;   //运输记录ID
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private Long transporterId;   //轿运车ID
    private Long driverId;   //司机ID
    private String sequence;   //顺序号
    private Date reportDate;   //上报时间
    private Integer carNumber;   //商品车数量
    private Double currentLoadWeight;   //当前载重(KG)
    private Double lng;   //经度
    private Double lat;   //纬度
    private Integer transportStatus;   //状态:0=行驶,1=停靠
    private Double cumulativeKm;   //累计行驶公里数
    private Double speed;   //车速(公里/小时)
    private Double temperature;   //温度(摄氏度)
    private Double humidity;   //湿度(摄氏度)
    private Double stress;   //压力(单位Pa)
    private Long corpId;   //公司ID
    
    private String refTransportOrderName;   //运单主表名称
    private String refTransporterCode;   //轿运车名称
    private String refDriverName;   //司机名称
    private String refCorpName;   //公司名称
    private String refTractirVin;	//轿运车车架号
    private String refDriverCode;   // 驾驶员名编号
    private String refTractirPlateCode;   //车头车牌号

}

