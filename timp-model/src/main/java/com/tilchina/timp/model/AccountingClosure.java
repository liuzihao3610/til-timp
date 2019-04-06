package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 关账
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class AccountingClosure extends SuperVO {

    private Long closureId;   //主键
    private String closureNumber;   //关账单号
    private Date accountPeriod;   //账期
    private Integer vinCount;   //车架号数量
    private Long creator;   //创建人ID
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID

}

