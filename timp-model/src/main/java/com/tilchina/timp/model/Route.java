package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;
import org.apache.commons.collections4.CollectionUtils;

/**
* 虚拟路线
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class Route extends SuperVO {

    private Long routeId;   //
    private String routeCode;   //路线编码
    private String routeName;   //路线名称
    private Long settlementCityId;   //结算隶属城市
    private Integer routeType;   //路线类型 0=利润优先 1=清仓线路
    private String tag;   //标签
    private String remark;   //备注
    private Long corpId;   //公司ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Integer flag;   //封存标志

    private String refCreator;

    private List<RouteDetail> details; // 虚拟城市列表

    @JSONField(serialize = false)
    private List<Long> cityAreas;

}

