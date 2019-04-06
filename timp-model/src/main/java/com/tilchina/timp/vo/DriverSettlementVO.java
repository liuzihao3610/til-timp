package com.tilchina.timp.vo;

import com.tilchina.timp.model.Car;
import com.tilchina.timp.model.TransportOrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *运单明细
 *
 * @version 1.0.0 2018/5/8
 * @author xiahong
 */
@Data
public class DriverSettlementVO implements Serializable{

    private Long transportOrderId;   //运单ID
    private String transportOrderCode;   //运单号
    private Integer jobType;   //作业类型:0=长途,1=市内,2=短驳,3=回
    private Date receivingDate;	//单据日期
    private String detail;  //  明细
    private String tractirPlateCode;	//车头车牌号
    private BigDecimal orderPrice;  //  运价
    private Integer refMaxQuantity;//可装载数量

}
