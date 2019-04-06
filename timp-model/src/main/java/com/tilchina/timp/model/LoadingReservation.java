package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.util.List;
import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.vo.ContactsVO;

/**
* 预约装车主表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class LoadingReservation extends SuperVO {

    private Long loadingReservationId;   //预约装车主表ID
    private String reservationCode;   //预约单号
    private Long transportOrderId;   //运单ID
    private String transportOrderCode;   //运单号
    private Long unitId;   //仓库ID
    private Long driverId;   //司机ID
    private Long transporterId;   //轿运车ID
    private Integer carLoadingNumber;   //装车数量
    private Date loadingTime;   //预约装车时间
    private String loadingLocation;   //装车位置
    private Integer billStatus;   //单据状态
    private Long creator;   //制单人
    private Date createDate;   //制单日期
    private Long checker;   //审核人
    private Date checkDate;   //审核日期
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private LoadingReservationDetail refLoadingReservationDetail;//预约装车子表
    private List<LoadingReservationDetail> sendUnits;//预约装车子表
    private Long refDetailTotal;//预约装车子表总条数
    private String refUnitName;   //仓库名称
    private String refDriverName;   //司机名称
    private String refTransporterCode;	//轿运车编号
    private String refCreateName;	//制单人名称
    private String refCheckerName;   //审核人
    private String refCorpName;   //公司名称
    private String refUnitCity;   //城市
    private String refUnitAddr;   //地址称
    private String refDriverCode;   // 驾驶员名编号
    private String refTractirPlateCode;   //车头车牌号
/*    private String refContactName;   //联系人
    private String refContactPhone;   //联系人电话
*/    
    
}

