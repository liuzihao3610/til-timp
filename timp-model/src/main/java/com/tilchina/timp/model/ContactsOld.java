package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 收发货单位联系人
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class ContactsOld extends SuperVO {

    private Long contactsId;   //主键
    private Long userId;   //用户ID
    private Long unitId;   //收发货单位ID
    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String contactsName;//用户姓名
    private String phone;//手机号
    
    private String refContactsName;//用户姓名
    private String refContactsCode;//用户编码
    private String refPhone;//手机号
    private String refEmail;//邮箱
    private String refFix;//收发货单位固定电话
    private String refAddress;//收发货单位地址
    private String refUnitName;//收发货单位名称
    private String refCreateName;//创建人姓名
    private String refCorpName;//公司名称

}

