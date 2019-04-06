package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* 客户报价明细
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class QuotationDetail extends SuperVO {

    private Long quotationDetailId; // 报价明细ID
    private Long quotationId;       // 报价ID
    private Integer quotationPlan;  // 报价方案(0:按台 1:按板按台 2:按板按板)
    private Integer jobType;        // 作业类型
    private Long sendCityId;        // 发货城市ID（市）
    private Long recvCityId;        // 收货城市ID（市）
    private Long sendAreaId;        // 发货区域ID（区）
    private Long recvAreaId;        // 收货区域ID（区）
    private Long sendUnitId;        // 发货单位ID（店）
    private Long recvUnitId;        // 收货单位ID（店）
    private Double distance;        // 公里数(KM)
    private Long carBrandId;        // 车辆品牌ID
    private Long carTypeId;         // 车辆类别ID
    private Long carModelId;        // 车辆型号ID
    private Integer leadTime;       // 交期
    private BigDecimal unitPrice;   // 每公里单价
    private BigDecimal totalPrice;  // 总价
    private BigDecimal taxRate;     // 税率
    private String priceDesc;       // 价格描述ID
    private Long corpId;            // 公司ID
    private Integer flag;           // 封存标志(0:否 1:是)

    private String refSendCityName;
    private String refRecvCityName;
    private String refSendAreaName;
    private String refRecvAreaName;
    private String refSendUnitName;
    private String refRecvUnitName;
    private String refCarBrandName;
    private String refCarTypeName;
    private String refCarModelName;
    private String refCarName;
    private String refCorpName;

    private String refQuotationNumber;
    private Date refEffectiveDate;

    private List<QuotationPrice> prices;

    public void addPrice(QuotationPrice price){
        if(CollectionUtils.isEmpty(prices)){
            prices = new ArrayList<>();
        }
        prices.add(price);
    }

}

