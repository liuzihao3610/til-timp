package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 订单主表
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Order extends SuperVO {

    private Long orderId;   //订单ID
    private String orderCode;   //订单号
    private Date orderDate;   //订单日期
    private Long corpCustomerId;   //客户ID
    private Long corpCarrierId;   //承运商ID
    private Integer orderType;   //订单类型 0=客户订单 1=转包订单 2=分段订单 
    private Integer workType;   //作业类型0=长途,1=市内,2=短驳,3=回程,4=铁运,5=空运,6=海运,7=展会
    private Integer billStatus;   //订单状态(0=制单 1=审核 2=已下达,3=取消计划,4=关闭)
    private String batchNumber;//批次号
    private Long salesManId;//业务员
    private Date transmitDate;//下达时间
    private String remark;//备注
    private Long creator;   //制单人
    private Date createTime;   //制单时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    
    private List<OrderDetail> ods;//子订单信息
    private List<OrderPieceVO> pieceList;//分段信息
    private String refCorpCustomerName;//客户名称
    private String refCorpCarrierName;//承运商名称
    private String refSalesManName;//业务员名称
    private String refCreateName;//创建人名称
    private String refCheckerName;//审核人名称
    private String refCorpName;//公司名称

}

