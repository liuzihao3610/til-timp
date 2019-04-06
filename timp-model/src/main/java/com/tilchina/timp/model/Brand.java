package com.tilchina.timp.model;

import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import lombok.*;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 品牌档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Brand extends SuperVO {

    private Long brandId;   //品牌ID
    private String brandCode;   //品牌编码

    @ImportField(title = "品牌名称")
    @ExportField(title = "品牌名称")
    private String brandName;   //品牌名称

    @ImportField(title = "英文名称")
    @ExportField(title = "英文名称")
    private String brandEnName;   //英文名称

    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCreateName;//创建人姓名
    private String refCorpName;//公司名称

}

