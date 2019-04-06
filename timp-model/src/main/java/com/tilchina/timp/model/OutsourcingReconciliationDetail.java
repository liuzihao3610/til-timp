package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
* 外协对账子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class OutsourcingReconciliationDetail extends SuperVO {

    private Long reconciliationDetailId;   //
    private Long reconciliationId;   //主表ID
    private Integer checkStatus;   //数据检查结果（0:未检查 1:未通过 2:已通过）
    private String checkResult;   //未通过原因
    private Long originalOrderDetailId;   //原始订单子表ID
    private String originalOrderNumber;   //原始订单号
    private Long outsourcingOrderDetailId;   //外协订单子表ID
    private String outsourcingOrderNumber;   //外协订单号
    private String carVin;   //车架号
    private Long carBrandId;   //车辆品牌
    private Long carTypeId;   //车辆类型
    private Long carModelId;   //车辆型号
    private Long quotationId;   //外协报价
    private BigDecimal freightRate;   //运价
    private BigDecimal actualFreightRate;   //实际运价
    private BigDecimal hedgeAmount;   //对冲金额
    private BigDecimal deductionAmount;   //扣款金额
    private String deductionReason;   //扣款原因
    private Long sendCityId;   //发货城市
    private Long recvCityId;   //收货城市
    private Long sendUnitId;   //发货单位
    private Long recvUnitId;   //收货单位
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCarBrandName;
    private String refCarTypeName;
    private String refCarModelName;
    private String refSendCityName;
    private String refRecvCityName;
    private String refSendUnitName;
    private String refRecvUnitName;
    private String refCorpName;
}

