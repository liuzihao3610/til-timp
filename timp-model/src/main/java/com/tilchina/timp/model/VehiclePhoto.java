package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 轿运车照片档案表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class VehiclePhoto extends SuperVO {

    private Long photoId;          // 主键
    private Long vehicleId;        // 轿运车ID
    private Long licenseId;        // 证件ID
    private Integer licenseType;   // 证件类型(0:行驶证 1:营运证 2:保单)
    private Integer photoType;     // 照片类型(0:轿运车 1:证件)
    private String photoDesc;      // 照片描述
    private String photoPath;      // 照片路径
    private Long creator;          // 创建人
    private Date createDate;       // 创建日期
    private Long corpId;           // 公司ID
    private Integer flag;          // 封存标志(0:否 1:是)

    private String refVehicleCode; // 轿运车编号
    private String refTractorCode; // 车头车牌号
    private String refTrailerCode; // 挂车车牌号
    private String refCreatorName; // 创建人名称
    private String refCorpName;    // 公司名称
}

