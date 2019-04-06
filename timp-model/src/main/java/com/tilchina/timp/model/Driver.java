package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 驾驶员档案
*
* @version 1.0.0
* @author LiuShuqi
*/
@Getter
@Setter
@ToString
public class Driver extends SuperVO {

    private Long userId;   //用户ID
    private String userCode;   //用户编码
    private String userName;   //用户名称
    private String userEnName;   //英文名
    private String phone;   //手机号
    private String email;   //电子邮箱
    private Integer driverType;   //司机类型 0=未指定 1=承包司机 2=非承包司机
    private String qq;   //QQ
    private String wechat;   //微信
    private Integer sex;   //性别(0 - 未知的性别,1 - 男性,2 - 女性,5 - 女性改（变）为男性,6 - 男性改（变）为女性,9 - 未说明的性别)
    private Date birthday;   //出生年月日
    private String nation;   //民族
    private String education;   //学历
    private String remark;   //备注
    private String avatrar;   //头像地址
    private String photoPath;   //照片地址
    private Integer bindingPhone;   //已绑定手机号:0=未绑定,1=已绑定
    private Long deptId;   //部门ID
    private Long positionId;   //职务ID
    private Date joinDate;   //入职时间
    private Date resignationDate;   //离职时间
    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCorpName;	//公司名称
    private String refDeptName;	//部门名称
    private String refPositionName;	//职务名称
    private String refCreateName;	//创建人姓名
    private String refCheckerName;	//审核姓名
    private String defPassword; 	//默认密码
    private Login refLoginInfo;	//认证信息档案 
    private Integer refBlock; //锁定状态:0=锁定,1=未锁定
    private Integer refErrorTimes;	//密码输入错误次数
    private String refIp;	//IP
    private Date refRecentlyLogintime; //最近一次登陆时间
   
}

