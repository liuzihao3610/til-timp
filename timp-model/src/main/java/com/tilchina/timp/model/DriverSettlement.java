package com.tilchina.timp.model;

import com.tilchina.timp.vo.DriverSettlementVO;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class DriverSettlement extends SuperVO {

    private Long settlementId;   //司机结算id
    private String settlementCode;   //结算编号
    private Long driverId;   //司机id
    private String driverCode;   //司机编号
    private String driverName;   //司机名称
    private Long transporterId;   //轿运车ID
    private String tractirPlateCode;   //车头车牌号
    private Long tractorId;   //车头型号ID
    private String tractorCode;   //车头型号
    private String tractorName;   //车头名称
    private Date settlementMonth;   //结算月份
    private Date settlementDateStart;   //结算时间起
    private Date settlementDateEnd;   //结算时间止
    private Integer allPlanCount;   //总运输数量
    private Integer receiptCount;   //已回单数量
    private Date settlementDate;   //结算日期
    private Integer participationDay;   //出勤天数
    private BigDecimal allContractPrice;   //总承包价
    private BigDecimal etc;   //etc
    private BigDecimal oilCost;   //油费
    private Integer billStatus;   //0=制单、1=审核、2=已确认
    private BigDecimal socialSecurityPrice;   //代缴社保
    private BigDecimal washCarPrice;   //洗车费
    private BigDecimal repairPrice;   //修理费
    private BigDecimal pourTrailerPrice;   //倒板费
    private BigDecimal littleTrailerPrice;   //小板费
    private BigDecimal bonusPrice;   //特别补助
    private BigDecimal orerrunPrice;   //超限
    private BigDecimal lossPrice;   //质损
    private BigDecimal qualitycControlPrice;   //品控
    private BigDecimal otherSubsidiesPrice;   //其他补贴
    private BigDecimal sreceivePrice;   //领用
    private BigDecimal oilCardPrice;   //油卡
    private BigDecimal borrowingPrice;   //借款
    private BigDecimal maneuveringPrice;   //机动补贴
    private BigDecimal attendancePrice;   //考勤代扣
    private BigDecimal minorRepairPrice;   //小修代扣
    private BigDecimal shouldTicketPrice;   //应票额
    private BigDecimal taxesPrice;   //应税额
    private BigDecimal actualSalaryPrice;   //实发金额
    private BigDecimal actualSalaryAllPrice;   //实发合计
    private Long creator;   //制单人
    private Date createDate;   //制单日期
    private Long checker;   //审核人
    private Date checkDate;   //审核日期
    private Date notarizeDate;   //确认时间
    private Long corpId;   //公司ID
    private String remark;   //备注

    private String refCreatorName;	//制单人姓名
    private String refCheckerName;	//审核人姓名
    private String refCorpName; //  公司名称
    private List<DriverSettlementVO> orders;  //  运单信息

}

