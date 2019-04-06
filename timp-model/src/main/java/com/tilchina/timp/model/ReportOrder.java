package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 在途提报运单
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class ReportOrder extends SuperVO {

    private Long reportOrderId;   //主键
    private Long transportOrderId;   //运单主表ID
    private Long orderId;   //订单主表ID
    private Long reportId;   //在途提报ID
    private Long corpId;   //公司ID
    
    private String refTransportOrderCode;   //运单号
    private String refOrderCode;   //订单号
    private String refDriverName;   //司机名称
    private String refDriverCode; //司机编号
    private String refTransporterCode;   //轿运车名称
    private String refTractirPlateCode;	//车头车牌号
    private String refCorpName;   //公司名称
    private String carVinMessage;	// //车辆信息
    private List<ReportPhoto> reportPhoto;//照片信息
    private WayReport wayReport;	//在途提报信息
    
}

