package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 轿运车司机关系表
*
* @version 1.1.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class TransporterAndDriver extends SuperVO {

    private Long transporterAndDriverId; // 轿运车司机关系表ID
    private Long transporterId;          // 轿运车ID(组合唯一)
    private Long assistantDriverId;      // 司机ID(组合唯一)
    private Long driverId;               // 司机ID(组合唯一)
    private Long creator;                // 创建人
    private Date createDate;             // 创建时间
    private Long corpId;                 // 公司ID
    private Integer flag;                // 封存标志
    private Date startContractingTime;   // 承包时间起
    private Date endContractingTime;     // 承包时间止
    private Integer contractingStatus;   // 承包状态(0:有效 1:失效)

    private String refTransporterCode;     // 轿运车编号(组合唯一)
    private String refDriverName;          // 主驾驶员名称(组合唯一)
    private String refAssistantDriverCode; // 副驾驶用户编号
    private String refCorpName;            // 公司名称
    private String refCreateName;          // 创建人姓名
    private String refAssistantDriverName; // 副驾驶员名称(司机id)
    private String refTrailerPlateCode;    // 挂车车牌号
    private String refDriverCode;          // 驾驶员名编号
    private String refTractirPlateCode;    // 车头车牌号
    private String refBrandName;           // 车头型号品牌
    
}

