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
* 卡资源管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Getter
@Setter
@ToString
public class CardResource extends SuperVO {

    private Long cardResourceId;   //卡ID

    @ImportField(title = "卡号")
    @ExportField(title = "卡号")
    private String cardResourceCode;   //卡号

    @EnumField(title = "卡类型", value = "com.tilchina.timp.enums.CardType")
    private Integer cardType;   //卡类型(0=油卡 1=ETC卡)

    @ImportField(title = "余额")
    @ExportField(title = "余额")
    private BigDecimal balance;   //余额

    @EnumField(title = "状态", value = "com.tilchina.timp.enums.CardStatus")
    private Integer cardStatus;   //状态(0=未领用 1=领用)

    @EnumField(title = "来源", value = "com.tilchina.timp.enums.SourceType")
    private Integer source;   //来源(0=采购 1=客户抵用)

    @ImportField(title = "发行公司")
    @ExportField(title = "发行公司")
    private String issueCompany;   //发行公司(如:中石油,中石化)

    @ImportField(title = "备注")
    @ExportField(title = "备注")
    private String remark;   //备注
    private Long driverId;   //领用人ID
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志(0= 否 1=是)

    private String refDriverName;//领用人名称
    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称

}

