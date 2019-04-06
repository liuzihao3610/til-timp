package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 驾驶员照片档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class DriverPhoto extends SuperVO {

    private Long driverPhotoId;   //主键
    private Long driverId;   //驾驶员ID
    private String photoDes;   //照片名称
    private Integer photoType;   //照片类型(0=头像 1=其它)
    private String photoPath;   //照片路径
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refDriverName;//驾驶员姓名
    private String refCreateName; //创建人名称
    private String refCheckerName; //审核人名称
    private String refCorpName; //公司名称

}

