package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 保时捷导入模板
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Porsche extends SuperVO {

    private Integer serialNumber;   //序号
    private String vendorCode;   //供应商编码
    private Date eta;   //船期
    private Date to;   //指定装车时间
    private String commNo;   //销售确认号
    private String model;   //车型
    private String vin;   //底盘号
    private String dealerCode;   //经销商编码
    private String dealerName;   //经销商名称
    private String remarks;   //备注
    
    private List<DefaultTransportCorp> defaultTransportCorps; // 默认运输公司
    private Long receiveUnitId; // 收货单位ID

}

