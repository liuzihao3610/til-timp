package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
* 通用质损管理主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CommonDamage extends SuperVO {

    private Long damageId;          // 质损主表ID
    private Long transportOrderId;  // 运单子表ID
    private String damageCode;      // 质损号
    private Date damageDate;        // 质损日期
    private String carVin;          // 车架号
    private Long carBrandId;        // 车辆品牌ID
    private String voyageNumber;    // 航次
    private Long creator;           // 创建人ID
    private Date createDate;        // 创建时间
    private Long corpId;            // 公司ID
    private Integer flag;           // 封存标志

    private String refCorpName;
    private String refCreatorName;
    private String refCarBrandName;
    private String refTransOrderCode;

    private List<CommonDamagePhoto> damagePhotos;
    private List<CommonDamageDetail> damageDetails;
}

