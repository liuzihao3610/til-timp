package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* 质损照片
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class CommonDamagePhoto extends SuperVO {

    private Long damagePhotoId;   //质损照片ID
    private Long damageId;   //通用质损管理主表ID
    private Long damageDetailId;   //通用质损管理子表ID
    private Integer photoType;   //照片类型(0=车架号 1=全景图 2=质损部位图)
    private String photoPath;   //照片路径
    private String photoDesc;   //照片描述
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0:否 1:是)
}

