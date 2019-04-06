package com.tilchina.timp.vo;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class UnitVO extends SuperVO {

    private String unitCode;    //  收发货单位编码/账号
    private String unitName;    //  收发货单位名称
    private String password;    //  收发货单位登陆密码

}

