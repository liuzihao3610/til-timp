package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 商品车类别档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class CarType extends SuperVO {

    private Long carTypeId;   //主键
    private String carTypeCode;   //类别编码

    @ImportField(title = "类别名称")
    @ExportField(title = "类别名称")
    private String carTypeName;   //类别名称
    private Long brandId;   //品牌ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    @ImportField(title = "品牌名称")
    @ExportField(title = "品牌名称")
    private String refBrandName;//品牌名称
    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称
}

