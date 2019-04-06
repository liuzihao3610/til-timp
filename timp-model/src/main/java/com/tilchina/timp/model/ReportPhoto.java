package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 在途提报照片
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class ReportPhoto extends SuperVO {

    private Long reportPhotoId;   //主键
    private Long reportId;   //在途提报ID
    private Integer photoType;   //照片类型
    private String photoPath;   //照片路劲
    private String description;   //照片描述
    private Long corpId;   //公司ID

}

