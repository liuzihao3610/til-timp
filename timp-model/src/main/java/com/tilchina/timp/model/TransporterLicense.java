package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 行驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransporterLicense extends SuperVO {

    private Long transporterLicenseId;   //行驶证档案ID
    private Long transporterId;   //轿运车ID
    private String platrCode;   //车牌号码
    private String transporterType;   //车牌类型
    private Long owner;   //所有人
    private String address;   //住址
    private String useType;   //使用性质
    private String brandModer;   //品牌型号
    private String vin;   //车辆识别代号VIN
    private String engineCode;   //发动机号码
    private Date registDate;   //注册日期
    private Date issuedDate;   //发证日期
    private Date validUntil;   //有效期至
    private String fileCode;   //档案标号
    private Integer loadNumber;   //核定载人数
    private Double totalQuality;   //总质量(KG)
    private Double hostlingQuality;   //整备质量(KG)
    private Double loadQuality;   //核定载质量(KG)
    private String externalSize;   //外廓尺寸(长×宽×高)单位:毫米
    private Double pullTotalQuality;   //准牵引总质量(KG)
    private String remark;   //备注
    private String imagePath;   //照片路劲
    private Integer billStatus;   //状态:0=制单,1=审核
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refTransporterCode;   //轿运车编号
    private String refTractirPlateCode;	//车头车牌号
    private String refTrailerPlateCode;   //挂车车牌号
    private String refCorpName;   //公司名称
    private String refCreateName;//创建人姓名
    private String refCheckerName;//审核人姓名
    private String refOwner;	//所有人名称
    private String refOwnerCode;	//所有人编码

}

