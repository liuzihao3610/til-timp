package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 质损部位档案
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class DamagePosition extends SuperVO {

    private Long positionId;   //质损部位ID
    private Integer positionCode;   //质损部位代号
    private String positionNameCn;   //质损部位中文名称
    private String positionNameEn;   //质损部位英文名称
    private String positionDescCn;   //质损部位中文描述
    private String positionDescEn;   //质损部位英文描述
    private String positionImage;   //质损部位参考图片路径
    private Long creator;   //创建人ID
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)

    private String refCreator;
    private String refCorp;
}

