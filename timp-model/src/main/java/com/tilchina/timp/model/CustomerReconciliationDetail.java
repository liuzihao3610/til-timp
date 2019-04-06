package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 客户对账明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CustomerReconciliationDetail extends SuperVO {

    private Long reconciliationDetailId;   //子表ID
    private Long reconciliationId;   //主表ID
    private Long orderDetailId;   //订单子表ID
    private String orderNumber;   //订单号
    private Long transportOrderDetailId;   //运单子表ID
    private String transportOrderNumber;   //运单号
    private Long sendCityId;   //发货城市ID
    private Long recvCityId;   //收货城市ID
    private Double distance;   //公里数
    private String productionNumber;   //生产号
    private Long carBrandId;   //车辆品牌ID
    private Long carTypeId;   //车辆类型ID
    private Long carModelId;   //车辆型号ID
    private String carVin;   //车架号
    private Long customerId;   //经销商ID
    private Long customerCorpId;   //
    private Date designatedLoadingDate;   //指定装车日期
    private Date actualLoadingDate;   //实际装车日期
    private Long quotationId;   //客户报价ID
    private BigDecimal quotationPrice;   //客户报价
    private BigDecimal hedgeAmount;   //对冲金额
    private BigDecimal settlementPrice;   //结算价格
    private BigDecimal deductionAmount;   //扣款总额
    private String deductionReason;   //扣款原因
    private Long recvUnitId;   //收货单位ID
    private Long sendUnitId;   //发货单位ID
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCorpName;
    private String refCarTypeName;
    private String refSendCityName;
    private String refRecvCityName;
    private String refCarBrandName;
    private String refCarModelName;
    private String refCustomerName;
    private String refDealerNumber;
    private String refSendUnitName;
    private String refRecvUnitName;
    private String refQuotationType;
}

