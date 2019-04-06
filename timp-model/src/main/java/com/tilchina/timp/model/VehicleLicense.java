package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 证件管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class VehicleLicense extends SuperVO {

    private Long licenseId;        // 主键
    private Long vehicleId;        // 轿运车ID
    private String licenseNumber;  // 证件号码
    private Integer licenseType;   // 证件类型(0:行驶证 1:营运证 2:保单)
    private Date startValidDate;   // 有效期起
    private Date endValidDate;     // 有效期止
    private Integer licenseStatus; // 证件状态(0:未审核 1:已审核 2:已过期)
    private Long creator;          // 创建人
    private Date createDate;       // 创建日期
    private Long checker;          // 审核人
    private Date checkDate;        // 审核日期
    private Long corpId;           // 公司ID
    private Integer flag;          // 封存标志(0:否 1:是)

    private String refVehicleCode; // 轿运车编号
    private String refTractorCode; // 车头车牌号
    private String refTrailerCode; // 挂车车牌号
    private String refCreatorName; // 创建人
    private String refCheckerName; // 审核人
    private String refCorpName;    // 公司
}

