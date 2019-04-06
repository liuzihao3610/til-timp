package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.annotation.EnumField;
import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Car extends SuperVO {

    private Long carId;   //商品车ID
    private String carCode;   //商品车编码

    @ImportField(title = "商品车名称")
    @ExportField(title = "商品车名称")
    private String carName;   //商品车名称

    private Long brandId;   //品牌ID

    private Long carTypeId;   //商品车类型ID

    private Long categoryId;   //商品车类别ID

    @EnumField(title = "汽车等级",value="com.tilchina.timp.enums.CarLevel")
    private Integer carLevel;   //汽车等级(0=未指定 1~12微型-轻客)

    @EnumField(title = "SUV",value="com.tilchina.timp.enums.SUV")
    private Integer suv;   //SUV

    @ImportField(title = "描述")
    @ExportField(title = "描述")
    private String description;   //描述

    @ImportField(title = "数据级别")
    @ExportField(title = "数据级别")
    private Integer dataLevel;   //数据级别(0-N)

    @ImportField(title = "长(毫米)")
    @ExportField(title = "长(毫米)")
    private Integer carLong;   //长(毫米)

    @ImportField(title = "宽(毫米)")
    @ExportField(title = "宽(毫米)")
    private Integer carWidth;   //宽(毫米)

    @ImportField(title = "高(毫米)")
    @ExportField(title = "高(毫米)")
    private Integer carHigh;   //高(毫米)

    @ImportField(title = "轴距(毫米)")
    @ExportField(title = "轴距(毫米)")
    private Integer wheelBase;   //轴距(毫米)

    @ImportField(title = "车轮半径(毫米)")
    @ExportField(title = "车轮半径(毫米)")
    private Integer wheelRadius;   //车轮半径(毫米)

    @ImportField(title = "前悬(毫米)")
    @ExportField(title = "前悬(毫米)")
    private Integer frontSuspension;   //前悬(毫米)

    @ImportField(title = "后悬(毫米)")
    @ExportField(title = "后悬(毫米)")
    private Integer rearSuspension;   //后悬(毫米)

    @ImportField(title = "底盘高度(毫米)")
    @ExportField(title = "底盘高度(毫米)")
    private Integer rideHeight;   //底盘高度(毫米)

    @ImportField(title = "自重(kg)")
    @ExportField(title = "自重(kg)")
    private Double carWeight;   //自重(kg)

    @ImportField(title = "价格(元)")
    @ExportField(title = "价格(元)")
    private BigDecimal price;   //价格(元)

    @ImportField(title = "产地")
    @ExportField(title = "产地")
    private String origin;   //产地

    @ImportField(title = "装卸费倍数")
    @ExportField(title = "装卸费倍数")
    private Double handleChangeMultiple;//装卸费倍数

    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    @ImportField(title = "品牌名称")
    @ExportField(title = "品牌名称")
    private String refBrandName; //品牌名称
    @ImportField(title = "商品车类型名称")
    @ExportField(title = "商品车类型名称")
    private String refCarTypeName; //商品车类型名称
    @ImportField(title = "商品类别名称")
    @ExportField(title = "商品类别名称")
    private String refCategoryName;//商品类别名称

    @ExportField(title = "汽车等级")
    private String refCarLevel;//汽车级别名称

    @ExportField(title = "SUV")
    private String refSuv;   //是否SUV
    private String refCreateName;//创建人名称
    private String refCheckerName;//审核人名称
    private String refCorpName;//公司名称
    
    private String appCarVin;//app车架号
}

