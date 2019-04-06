package com.tilchina.timp.model;

import com.tilchina.timp.annotation.IsNeeded;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 导入结算价格
 *
 * @version 1.0.0
 * @author LiuShuqi
 */
@Getter
@Setter
@ToString
public class ImportFreight {
    private Integer freightId;   //序号
    private String freightCode;   //报价编号
    @IsNeeded(isNeeded = true)
    private String freightType;   //报价类型(0=标准价 1=结算价)
    @IsNeeded
    private String startPlaceName;   //起运地(市)
    @IsNeeded
    private String arrivalPlaceName;   //目的地(市)
    @IsNeeded
    private String brandName;   //品牌
    @IsNeeded
    private String carTypeName;   //车型类型
    @IsNeeded
    private String denominatedMode;   //计价方式(0=统一价格 1=按里程计价)
    @IsNeeded
    private BigDecimal unitPrice;   //单价
    @IsNeeded
    private BigDecimal freightRate;   //起运价
    @IsNeeded
    private BigDecimal perKilometerPrice;   //每公里单价
    @IsNeeded
    private BigDecimal kilometer;   //公里数(KM)
    @IsNeeded
    private BigDecimal handleCharge;   //装卸费
    @IsNeeded
    private String sendUnitName;//发货单位ID
    @IsNeeded
    private String receiveUnitName;//收货单位ID
    @IsNeeded
    private String settlementCorpName;//结算单位ID
    @IsNeeded
    private Date effectiveDate;   //生效日期
    @IsNeeded
    private Date failureDate;   //失效日期
    @IsNeeded
    private String remark;   //描述
}

