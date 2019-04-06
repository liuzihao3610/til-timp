package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 商品车状态变更记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class StockCarHis extends SuperVO {

    private Long stockCarHisId;   //商品车状态变更记录表ID
    private Long stockCarId;   //商品车库存表id
    private String carVin;   //车架号
    private Integer carStatus;   //车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
    private Date changeDate;   //变更时间
    private Long orderBillId;   //单据id
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refStockCarCode;   //商品车库存表编码
    private String refOrderBillCode;   //单据编码
    private String refCorpName;//公司名称
    
}

