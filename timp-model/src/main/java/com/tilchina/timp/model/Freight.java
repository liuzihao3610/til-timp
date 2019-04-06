package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.annotation.ImportField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 结算价格管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Freight extends SuperVO {

    private Long freightId;   //主键
    @ImportField(title = "报价编号")
    private String freightCode;   //报价编号
    @ImportField(title = "报价类型")
    private Integer freightType;   //报价类型(0=标准价 1=结算价)
    @ImportField(title = "生效日期")
    private Date effectiveDate;   //生效日期
    @ImportField(title = "失效日期")
    private Date failureDate;   //失效日期
    @ImportField(title = "起运地")
    private Long startPlaceId;   //起运地(市)
    @ImportField(title = "目的地")
    private Long arrivalPlaceId;   //目的地(市)
    @ImportField(title = "品牌")
    private Long brandId;   //品牌
    @ImportField(title = "车型类型")
    private Long carTypeId;   //车型类型
    @ImportField(title = "计价方式")
    private Integer denominatedMode;   //计价方式(0=统一价格 1=按里程计价)
    @ImportField(title = "单价")
    private BigDecimal unitPrice;   //单价
    @ImportField(title = "起运价")
    private BigDecimal freightRate;   //起运价
    @ImportField(title = "每公里单价")
    private BigDecimal perKilometerPrice;   //每公里单价
    @ImportField(title = "公里数")
    private Double kilometer;   //公里数(KM)
    @ImportField(title = "装卸费")
    private BigDecimal handleCharge;   //装卸费
    @ImportField(title = "描述")
    private String remark;   //描述
    private Integer billStatus;   //单据状态(0=制单 1=审核)
    @ImportField(title = "发货单位")
    private Long sendUnitId;//发货单位ID
    @ImportField(title = "收货单位")
    private Long receiveUnitId;//收货单位ID
    @ImportField(title = "结算单位")
    private Long settlementCorpId;//结算单位ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0=否 1=是)

    private String refStartPlaceName;//起运地
    private String refArrivalPlaceName;//目的地
    private String refBrandName;//品牌名称
    private String refCarTypeName;//车型类型名称
    private String refSendUnitName;//发货单位名称
    private String refReceiveUnitName;//收货单位名称
    private String refSettlementCorpName;//结算单位名称
    private String refCreateName;//创建人名称
    private String refCheckName;//审核人名称
    private String refCorpName;//公司名称

    private BigDecimal finalPrice; // 最终价格
}

