package com.tilchina.timp.vo;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运单：收发货联系人信息
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class ContactsVO extends SuperVO {

    private String startUnitContactsName;//发货单位用户姓名
    private String startUnitPhone;//发货单位机号
    private String endUnitContactsName;//收货单位用户姓名
    private String endUnitPhone;//收货单位手机号

}

