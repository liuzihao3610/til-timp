package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* 收发货单位联系人
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Contacts extends SuperVO {

    private Long contactsId;   //主键
    private Long unitId;   //收发货单位ID
    private String contactsName;   //联系人姓名
    private String contactsCode;   //联系人编码
    private String phone;   //手机
    private String fix;   //固定电话
    private String email;   //邮箱
    private Integer qq;   //QQ
    private String wechat;   //微信号
    private String wechatId;   //微信身份ID(不显示)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refUnitName;//收发货单位名称
    private String refCreateName;//创建人名称
    private String refCorpName;//公司名称

}

