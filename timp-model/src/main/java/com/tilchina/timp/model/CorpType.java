package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 公司类型
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class CorpType extends SuperVO {

    private Long corpTypeId;   //主键
    private Long corpId;   //公司ID
    private Integer typeKey;   //类型值(0=承运公司 1=运输公司 2=客户 3=经销店)
    private String typeName;   //类型名称
    private Integer flag;   //封存标志
    
    private String refCorpName;//公司名称

}

