package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 轿运车状态表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransporterStatus extends SuperVO {

    private Long transporterStatusId;   //轿运车状态表ID
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
    private String refTransporterCode;   //轿运车名称
    private String refCorpName;   //公司名称
    private String refCreateName;//创建人姓名
    private String refTractirPlateCode;   //车头车牌号(Axxxxx)
    private String refTrailerPlateCode;   //挂车车牌号
    private String refDriverCode;   // 驾驶员名编号
    
}

