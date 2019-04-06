package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单主表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransportOrder extends SuperVO {

    private Long transportOrderId;   //运单ID
    private String transportOrderCode;   //运单号
    private Integer jobType;   //作业类型:0=长途,1=市内,2=短驳,3=回程
    private Integer examination;   //车检标志
    private Integer urgent;		//加急标志：0=否 1=是
    private Double predictKm;   //预计公里数
    private Double practicalKm;   //实际公里数
    private Long driverId;   //司机ID
    private Long transporterId;   //轿运车ID
    private Date loadingDate;   //装车日期
    private Long carriageCorpId;   //承运公司ID
    private Long salesmanId;//业务员
    private Date transmitDate;//下达时间
    private Integer billStatus;   //运单状态:0=制单 1=审核 2=已发送 3=已接单 4=在途 5=取消计划 6=关闭
    private Long creator;   //制单人
    private Date createDate;   //制单日期
    private Long checker;   //审核人
    private Date checkDate;   //审核日期
    private Long corpId;   //公司ID
    
    private Date receivingDate;	//单据日期
    private Integer settleType;	//结算模式
    private Integer settleStatus;	//结算状态(0=未结算,1=已结算) 
    private String remark;   //备注
    
    private String refDriverName;   //司机名称
    private String refTransporterCode;   //轿运车名称
    private String refCarriageCorpName;   //承运公司名称
    private String refCorpName;   //公司名称
    private String refCreateName;	//制单人姓名
    private String refCheckerName;	//审核人姓名
    private List<TransportOrderDetail> details;	//运单子表
    private Long refDetailTotal;	//运单子表总条数
    private String refTractirPlateCode;	//车头车牌号
    private String refTrailerPlateCode;   //挂车车牌号
    private String refAssistantDriverName;   //副驾驶员名称(司机id)轿运车司机关系表
    private String refTractorName;   //车头型号名称(轿运车型号)
    private String refSalesmanName;//业务员
    private Integer refMaxQuantity;//可装载数量
    
    private String refDriverCode;   // 驾驶员名编号
    private String refAssistantDriverCode;   //副驾驶员名称(司机id)轿运车司机关系表
    
}

