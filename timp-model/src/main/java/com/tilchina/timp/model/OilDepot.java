package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 油库管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class OilDepot extends SuperVO {

    private Long oilDepotId;   //油库ID
    private String oilDepotCode;   //油库编号
    private Integer oilDepotType;   //油库类型(0=汽油 1=柴油)
    private String oilLabel;   //标号
    private Double allowance;   //余量(m³)
    private Integer oilDepotStatus;   //状态(0= 停用 1=正常)
    private String remark;   //备注
    private Long managePerson;   //负责人
    private String phone;   //联系电话
    private Long provinceId;   //省
    private Long cityId;   //市
    private Long areaId;   //区
    private String oilDepotAddress;   //地址
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0= 否 1=是)
    
    private String refManagePersonName;//负责人名称
    private String refProvinceName;//省名称
    private String refCityName;//市名称
    private String refAreaName;//区名称
    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称

}

