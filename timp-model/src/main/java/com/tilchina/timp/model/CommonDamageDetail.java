package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
* 通用质损管理子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CommonDamageDetail extends SuperVO {

    private Long damageDetailId;   //通用质损管理子表ID
    private Long damageId;   //通用质损管理主表ID
    private Long damagePositionId;   //质损部位ID
    private Integer damageDegree;   //质损程度 (0:轻微 1:中等 2:重度)
    private Integer damageRegistration;   //质损环节 (0:装车 1:到店)
    private String damageCauseCn;   //质损原因 - 中文
    private String damageCauseEn;   //质损原因 - 英文
    private Date damageDate;   //质损时间
    private String remark;   //备注
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private List<CommonDamagePhoto> damagePhotos;

    private String refPositionNameCN;
}

