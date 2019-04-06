package com.tilchina.timp.model;

import com.tilchina.timp.annotation.EnumField;
import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import lombok.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 
*
* @version 1.0.0
* @author WangShengguang
*/
@Getter
@Setter
@ToString
public class User extends SuperVO {

    private Long userId;   //用户ID
    private String userCode;   //用户编码

    @ImportField(title = "用户名称")
    @ExportField(title = "用户名称")
    private String userName;   //用户名称

    @ImportField(title = "英文名")
    @ExportField(title = "英文名")
    private String userEnName;   //英文名

    @ImportField(title = "身份证号码")
    @ExportField(title = "身份证号码")
    private String identityCardNumber;//身份证号码

    @ImportField(title = "手机号")
    @ExportField(title = "手机号")
    private String phone;   //手机号

    @ImportField(title = "电子邮箱")
    @ExportField(title = "电子邮箱")
    private String email;   //电子邮箱

    @EnumField(title = "用户类型",value = "com.tilchina.timp.enums.UserType")
    private Integer userType;   //用户类型 0=系统用户 1=司机

    @EnumField(title = "司机类型",value = "com.tilchina.timp.enums.DriverType")
    private Integer driverType;   //司机类型 0=未指定 1=承包司机 2=非承包司机

    @ImportField(title = "QQ")
    @ExportField(title = "QQ")
    private String qq;   //QQ

    @ImportField(title = "微信")
    @ExportField(title = "微信")
    private String wechat;   //微信

    @EnumField(title = "性别",value = "com.tilchina.timp.enums.Sex")
    private Integer sex;   //性别(0 - 未知的性别,1 - 男性,2 - 女性,5 - 女性改（变）为男性,6 - 男性改（变）为女性,9 - 未说明的性别)

    @ImportField(title = "出生年月日")
    @ExportField(title = "出生年月日")
    private Date birthday;   //出生年月日

    @ImportField(title = "民族")
    @ExportField(title = "民族")
    private String nation;   //民族

    @ImportField(title = "学历")
    @ExportField(title = "学历")
    private String education;   //学历

    @ImportField(title = "备注")
    @ExportField(title = "备注")
    private String remark;   //备注

    private String avatrar;   //头像地址
    private String photoPath;   //照片地址
    private Integer bindingPhone;   //已绑定手机号:0=未绑定,1=已绑定

    private Long deptId;   //部门ID
    private Long positionId;   //职务ID

    @ImportField(title = "入职时间")
    @ExportField(title = "入职时间")
    private Date joinDate;   //入职时间

    @ImportField(title = "离职时间")
    @ExportField(title = "离职时间")
    private Date resignationDate;   //离职时间

    private Integer billStatus;   //状态(0=制单 1=审核)
    private Long creator;   //创建人
    private Date createDate;   //创建时间
    private Long checker;   //审核人
    private Date checkDate;   //审核时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    private Integer superManager;  //超级经理
    private Integer admin;  //系统管理员

    private String refCorpName;	//公司名称

    @ImportField(title = "部门名称")
    @ExportField(title = "部门名称")
    private String refDeptName;	//部门名称

    @ImportField(title = "职务名称")
    @ExportField(title = "职务名称")
    private String refPositionName;	//职务名称
    private String refCreateName;	//创建人姓名
    private String refCheckerName;	//审核姓名
    private String defPassword; 	//默认密码
    private Login refLoginInfo;	//认证信息档案 
    private Integer refBlock; //锁定状态:0=锁定,1=未锁定
    private Integer refErrorTimes;	//密码输入错误次数
    private String refIp;	//IP
    private Date refRecentlyLogintime; //最近一次登陆时间
    
    
//    private String photoDes;   //照片名称
//    private Integer photoType;   //照片类型(0=头像 1=其它)
    private List<Map<String, Object >> path;   //照片路径
    private String licenseName;   //驾驶证名称
    private Integer licenseType;   //驾驶证类型 
    private String licensePath;   //照片路径

    private Long tractorId;
    private String tractorCode;
    private String tractorName;
    private String tractirPlateCode;
    private Long transporterId;

}

