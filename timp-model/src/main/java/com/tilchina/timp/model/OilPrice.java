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
* 油价档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class OilPrice extends SuperVO {

    private Long oilPriceId;   //油价ID

    @ImportField(title = "名称")
    @ExportField(title = "名称")
    private String oilName;   //名称

    @EnumField(title = "类型", value = "com.tilchina.timp.enums.OilType")
    private Integer oilType;   //类型(0=汽油 1=柴油)

    @ImportField(title = "标号")
    @ExportField(title = "标号")
    private String labeling;   //标号

    @ImportField(title = "价格")
    @ExportField(title = "价格")
    private BigDecimal price;   //价格

    @ImportField(title = "生效日期")
    @ExportField(title = "生效日期")
    private Date effectiveDate;   //生效日期
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    private String refCorpName;//公司名称

}

