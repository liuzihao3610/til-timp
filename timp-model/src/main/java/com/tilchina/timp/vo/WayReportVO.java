package com.tilchina.timp.vo;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tilchina.timp.model.ReportPhoto;
import com.tilchina.timp.model.WayReport;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 在途查询
 * @author Xiahong
 *
 */
@Getter
@Setter
@ToString
public class WayReportVO {

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

    private Integer reportStatus;   //提报状态
    private Date reportDate;   //提报时间
    private String location;   //位置
    private Double originLng;   //经度
    private Double originLat;   //纬度
    private Double lng;   //经度
    private Double lat;   //纬度

}
