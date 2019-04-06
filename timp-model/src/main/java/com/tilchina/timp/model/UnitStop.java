package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 接车点管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class UnitStop extends SuperVO {

    private Long unitStopId;   //主键
    private Long unitId;   //收发货单位ID
    private String unitStopAdress;   //停车点地址
    private Integer continueTransport;   //是否需要继续运输(0= 否 1=是)
    private Integer smallCartPay;   //小板费支付方式(0=司机自付 1=公司支付 2=经销店垫付)
    private Double lng;   //接车点经度
    private Double lat;   //接车点纬度
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0= 否 1=是)

    private String refUnitName;//收发货单位名称
    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称

}

