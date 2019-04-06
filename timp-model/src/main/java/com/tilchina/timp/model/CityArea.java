package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.experimental.FieldDefaults;

/**
* 虚拟城市区域
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class CityArea extends SuperVO {

    private Long cityAreaId;   //
    private String caCode;   //虚拟区域编码
    private String caName;   //虚拟区域名称
    private Long settlementCityId;   //结算隶属城市
    private Long corpId;   //公司ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Integer flag;   //封存标志

    private String refSetCityName;// 结算隶属城市名称
    private String refCreator;

    @JSONField(serialize = false)
    private List<Unit> units;

    private List<CityAreaDetail> details;

}

