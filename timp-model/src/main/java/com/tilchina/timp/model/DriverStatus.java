package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class DriverStatus extends SuperVO {

	private Long driverStatusId;   //驾驶员状态ID
    private Long driverId;   //司机ID
    private Long transporterTransporterId;   //骄运车ID
    private Integer driverStatus;   //当前状态:0=空闲,1=在途,2=到店,3=回程,4=请假,5=培训
    private String location;   //位置
    private Double lng;   //经度
    private Double lat;   //纬度
    private Date startDate;   //开始时间
    private Date endDate;   //结束时间
    private String remark;   //备注
    private Long creator;   //创建人
    private Date createDate;   //提报时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refDriverCode;   //司机编码
    private String refDriverName;   //司机名称
    private String refAssistantDriverId;		//副司机id
    private String refAssistantDriverCode;   //副司机编码
    private String refAssistantDriverName;   //副司机名称
    private String refTransporterCode;   //轿运车编号
    private String refTractirPlateCode;	//车头车牌号
    private String refTrailerPlateCode;   //挂车车牌号
    private String refCorpName;   //公司名称
    private String refCreateName;   //创建人名称

}

