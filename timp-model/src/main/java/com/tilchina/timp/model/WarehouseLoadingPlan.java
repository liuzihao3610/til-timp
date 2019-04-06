package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class WarehouseLoadingPlan extends SuperVO {

    private Long warehouseLoadingPlanId;   //仓库装车ID
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private Date planDate;   //计划日期
    private Long unitId;   //仓库ID(收发货单位ID)
    private Long carId;   //商品车ID
    private String carVin;   //车架号
    private Integer billStatus;   //状态 0=待定 1=申请 2=确认 3=已装车
    private Long driverId;   //司机ID
    private Long transporterId;   //轿运车ID
    private Date loadingDate;   //预约装车时间
    private String loadingLocation;   //装车点
    private String remark;   //备注
    private Long corpId;   //公司ID
    
    private String refUnitName;   //仓库名称(收发货单位名称)
    private String refCarName;   //商品车名称
    private String refDriverName;   //司机名称
    private String refBrandName; //品牌
    private String refTransporterCode;	//轿运车编号
    private String refCorpName;   //公司名称
    private String refAddress;	//收发货单位地址
    private String refDriverCode;   // 驾驶员名编号
    private String refTractirPlateCode;   //车头车牌号

}

