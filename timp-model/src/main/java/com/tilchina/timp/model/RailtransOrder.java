package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 铁路运输记录主表
 *
 * @author XueYuSong
 * @version 1.0.0
 */
@Getter
@Setter
@ToString
public class RailtransOrder extends SuperVO {

    private Long transOrderId;   //铁运记录主表ID
    private String transOrderCode;   //运单号
    private String carrier;   //运输商
    private String client;  //客户名称
    private String hubLocation;   //Hub位置
    private String carBatch;   //批次
    private String remark;   //备注
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Integer billStatus;   //单据状态
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCreateName;
    private String refCheckName;

    private List<RailtransOrderDetail> details;
}

