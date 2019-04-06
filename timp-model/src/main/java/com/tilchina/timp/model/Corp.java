package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 公司档案
 *
 * @author XueYuSong
 * @version 1.1.0
 */
@Getter
@Setter
@ToString
public class Corp extends SuperVO {

	private Long corpId;                  // 主键
	private String corpCode;              // 公司编码
	private String corpName;              // 公司名称
	private String enName;                // 英文名称
	private Long corpTypeId;              // 公司类型(0=承运公司 1=运输公司 2=客户 3=经销店)
	private String remark;                // 备注
	private Long upCorpId;                // 上级公司ID
	private Long creator;                 // 创建人
	private Date createDate;              // 创建时间
	private Integer flag;                 // 封存标志
	private String socialCreditCode;      // 统一社会信用代码(Unified Social Credit Code)
	private String billingAddress;        // 开票信息 - 地址电话
	private String bankAccountName;       // 开票信息 - 开户银行
	private String bankAccountNumber;     // 开票信息 - 银行账号
	private Integer accountPeriodType;    // 账期类型(0:按下单时间,1:按开票时间,2:按对账时间)
	private Integer accountPeriodDays;    // 账期（天）
	private String contactName;           // 联系人
	private String contactPhone;          // 联系电话
	private String accountOpeningAddress; // 开户地址
	private String accountOpeningPhone;   // 开户电话
	private String corpAddress;           // 公司地址
	private String corpPhone;             // 公司电话

	private String refUpCorpName;   // 上级公司名称
	private String refCorpTypeName; // 公司类型名称
	private String refCreatorName;  // 创建人姓名

}

