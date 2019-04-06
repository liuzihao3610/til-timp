package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 骄运车提报记录
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransporterReport extends SuperVO {

    private Long transporterReportId;   //
    private Long transporterId;   //轿运车ID
    private Long driverId;   //司机ID
    private Integer transporterStatus;   //当前状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库
    private Date startDate;   //开始时间
    private Date endDate;   //结束时间
    private String remark;   //备注
    private String location;   //位置
    private Double lng;   //经度
    private Double lat;   //纬度
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refDriverName;   //司机名称
    private String refTransporterCode;   //轿运车编号
    private String refTractirPlateCode;	//车头车牌号
    private String refTrailerPlateCode;   //挂车车牌号
    private String refCorpName;   //公司名称
    private String refCreateName;   //创建名称
    private TransporterStatus refTransporterStatus;	//骄运车状态信息
    
    private String refDriverCode;   //司机名称
    
}

