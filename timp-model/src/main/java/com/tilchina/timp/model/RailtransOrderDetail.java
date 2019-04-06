package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 铁路运输记录子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class RailtransOrderDetail extends SuperVO {

    private Long transOrderDetailId;   //铁运记录子表ID
    private Long transOrderId;   //铁运记录主表ID
    private String transOrder;   //运输指令
    private String carVin;   //底盘编号
    private String carModel;   //车型
    private String dealerNo;   //经销商编号
    private String dealerName;   //经销商名称
    private Date toDate;   //运输指令下达日
    private Date plannedDispatchedDate;   //计划发运日期
    private Date actualPickupDate;   //实际提车日期
    private Date actualDispatchedDate;   //实际发运日期
    private Date estTohubDate;   //预计到达Hub仓库日期
    private Date dueDate;   //交货期限
    private String zone;   //位置
    private String remark;   //备注
    private String startingStation;   //始发站
    private String terminalStation;   //终点站
    private String cabinNumber;   //车厢号
    private String latestProvince;   //车厢最新停靠站所在省市
    private String latestCity;   //车厢最新停靠站所在城市
    private String latestStation;   //车厢最新停靠站所在站台
    private String latestStatus;   //车厢最新停靠状态
	private Date latestUpdateTime; // 最后更新时间
    private Integer isArrived;   //到达状态
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Integer billStatus;   //单据状态
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

}

