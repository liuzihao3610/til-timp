package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 系统参数设置
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Config extends SuperVO {

    private Long configId;   //主键
    private String code;   //编码
    private String value;   //值
    private String description;   //描述
    private Long corpId;   //公司ID
    
    private String refCorpName; //公司名称

}

