package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 检查站
*
* @version 1.0.0
* @author LiShuqi
*/
@Getter
@Setter
@ToString
public class CheckPoint extends SuperVO {

    private Long checkPointId;   //主键
    private String checkPointCode;   //检查站编码

    @ImportField(title = "检查站名称")
    @ExportField(title = "检查站名称")
    private String checkPointName;   //检查站名称

    @ImportField(title = "英文名称")
    @ExportField(title = "英文名称")
    private String enName;   //英文名称

    @ImportField(title = "检查站类型")
    @ExportField(title = "检查站类型")
    private Integer checkPointType;   //检查站类型(1、安全检测站 2、维修检测站 3、综合检测站 )

    @ImportField(title = "检查内容")
    @ExportField(title = "检查内容")
    private String checkContent;   //检查内容

    private Long provinceId;   //省
    private Long cityId;   //市
    private Long areaId;   //区
    private Long contact;   //联系人

    @ImportField(title = "检查站地址")
    @ExportField(title = "检查站地址")
    private String address;   //检查站地址

    @ImportField(title = "固定电话")
    @ExportField(title = "固定电话")
    private String telephone;   //固定电话

    @ImportField(title = "传真")
    @ExportField(title = "传真")
    private String fax;   //传真

    @ImportField(title = "经度")
    @ExportField(title = "经度")
    private Double lng;   //经度

    @ImportField(title = "纬度")
    @ExportField(title = "纬度")
    private Double lat;   //纬度

    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Integer flag;   //封存标志

    @ImportField(title = "联系人")
    @ExportField(title = "联系人")
    private String refUserName;//联系人

    @ImportField(title = "省名称")
    @ExportField(title = "省名称")
    private String refProvinceName; //省名称

    @ImportField(title = "市名称")
    @ExportField(title = "市名称")
    private String refCityName; //市名称

    @ImportField(title = "区名称")
    @ExportField(title = "区名称")
    private String refAreaName; //区名称
    private String refCreateName; //创建人名称

}

