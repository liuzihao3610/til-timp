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
* 挂车型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Trailer extends SuperVO {

    private Long trailerId;   //主键
    private String trailerCode;   //挂车型号

    @ImportField(title = "挂车名称")
    @ExportField(title = "挂车名称")
    private String trailerName;   //挂车名称
    private Long brandId;   //品牌ID

    @EnumField(title = "挂车类型", value = "com.tilchina.timp.enums.TrailerType")
    private Integer trailerType;   //挂车类型(0=固定 1=半挂 2=全挂)

    @EnumField(title = "货物类型", value = "com.tilchina.timp.enums.CargoType")
    private Integer cargoType;   //货物类型(0=通用 1=轿车 2=集装箱)

    @ImportField(title = "长(毫米）")
    @ExportField(title = "长(毫米）")
    private Integer trailerLong;   //长(毫米）

    @ImportField(title = "宽(毫米)")
    @ExportField(title = "宽(毫米)")
    private Integer trailerWidth;   //宽(毫米)

    @ImportField(title = "高(毫米)")
    @ExportField(title = "高(毫米)")
    private Integer trailerHigh;   //高(毫米)

    @ImportField(title = "自重(公斤)")
    @ExportField(title = "自重(公斤)")
    private Double trailerWeight;   //自重(kg)

    @ImportField(title = "总载重(公斤)")
    @ExportField(title = "总载重(公斤)")
    private Double fullWeight;   //总载重(kg)

    @ImportField(title = "价格(元)")
    @ExportField(title = "价格(元)")
    private BigDecimal price;   //价格(元)

    @ImportField(title = "产地")
    @ExportField(title = "产地")
    private String origin;   //产地
    private Long supplierId;   //经销商ID

    @ImportField(title = "装车数量")
    @ExportField(title = "装车数量")
    private Integer maxQuantity;   //装车数量

    @ImportField(title = "层数")
    @ExportField(title = "层数")
    private Integer layer;   //层数
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    @ImportField(title = "品牌名称")
    @ExportField(title = "品牌名称")
    private String refBrandName;//品牌名称

    @ImportField(title = "经销商名称")
    @ExportField(title = "经销商名称")
    private String refSupplierName;//经销商名称
    private String refCreateName;//创建人名称
    private String refCheckerName;//审核人名称
    private String refCorpName;//公司名称

}

