package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 驾驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class DriverLicense extends SuperVO {

    private Long driverLicenseId;   //主键
    private Long driverId;   //驾驶员ID
    private String licenseName;   //驾驶证名称
    private Integer licenseType;   //驾驶证类型 
    private String licensePath;   //照片路径
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refDriverName;//驾驶员姓名
    private String refDriverCode;//驾驶员编码
    private String refCreateName; //创建人名称
    private String refCheckerName; //审核人名称
    private String refCorpName; //公司名称

}

