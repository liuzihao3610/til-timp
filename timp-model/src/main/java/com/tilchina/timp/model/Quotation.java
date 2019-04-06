package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* 客户报价
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class Quotation extends SuperVO {

    private Long quotationId;       // 报价ID
    private String quotationNumber; // 报价编号
    private Integer quotationType;  // 报价类型 0:合同价, 1:预估价
    private Long contractId;        // 合同编号
    private Long customerId;        // 客户ID
    private Long vendorCorpId;      // 承运公司ID
    private String quotationName;   // 报价名称
    private String quotationFile;   // 报价文件
    private Integer jobType;        // 作业类型 0:长途, 1:市内, 2:短驳, 3:回程, 4:铁运, 5:空运, 6:海运, 7:展会
    private Date effectiveDate;     // 生效日期
    private Date expirationDate;    // 失效日期
    private Long creator;           // 创建人
    private Date createDate;        // 创建时间
    private Long checker;           // 审核人
    private Date checkDate;         // 审核时间
    private Integer billStatus;     // 制单状态
    private Long corpId;            // 公司ID
    private Integer flag;           // 封存标志(0:否 1:是)

    private List<QuotationDetail> details;

    private Integer refLeadTime;    // 子表交期

    private String refContractNumber;
    private String refCustomerName;
    private String refVendorCorpName;
    private String refCreatorName;
    private String refCheckerName;
    private String refCorpName;

    public void addDetail(QuotationDetail detail){
        if(CollectionUtils.isEmpty(details)){
            details = new ArrayList<>();
        }
        details.add(detail);
    }
}

