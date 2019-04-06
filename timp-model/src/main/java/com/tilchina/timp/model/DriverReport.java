package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 驾驶员提报记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class DriverReport extends SuperVO {

    private Long driverReportId;   //驾驶员提报记录ID
    private Integer reportType;   //提报类型:0=司机+车辆,1=司机,2=车辆
    private Integer reportStatus;	//提报状态
    private Long driverId;   //司机ID
    private Integer driverStatus;   //司机状态:0=默认,1=在途,2=到店,3=回程,4=请假,5=培训
    private Long transporterId;   //骄运车ID
    private Integer transporterStatus;   //骄运车状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库
    private String location;   //位置(通过经纬度得到位置)
    private Double lng;   //经度
    private Double lat;   //纬度
    private Date startDate;   //开始时间
    private Date endDate;   //结束时间
    private String remark;   //备注
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refDriverName;   //司机名称
    private String refDriverCode;   //司机编码
    private String refTransporterCode;   //轿运车编号
    private String refTractirPlateCode;	//车头车牌号
    private String refTrailerPlateCode;   //挂车车牌号
    private String refCorpName;   //公司名称
    private String refCreateName;   //创建人名称
    private DriverStatus refDriverStatus;	//司机状态信息
    private TransporterStatus refTransporterStatus;	//骄运车状态信息

}

