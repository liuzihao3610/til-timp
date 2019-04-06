package com.tilchina.timp.model;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tilchina.catalyst.vo.SuperVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* 在途提报
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class WayReport extends SuperVO {

    private Long reportId;   //主键
    private Integer reportStatus;   //提报状态
    private Date reportDate;   //提报时间
    private Long driverId;   //司机ID
    private Long transporterId;   //轿运车ID
    private String location;   //位置
    private Double originLng;   //经度
    private Double originLat;   //纬度
    private Double lng;   //经度
    private Double lat;   //纬度
    private Long corpId;   //公司ID
    
    List<Map<Integer,File>> reportPhotos;//提报照片
    
    

}

