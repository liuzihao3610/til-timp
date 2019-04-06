package com.tilchina.timp.model;

import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

/**
*	功能注册
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Regist extends SuperVO {

    private Long registId;   //功能ID
    private String registCode;   //功能编码
    private String registName;   //功能名称
    private String urlPath;   //URL地址
    private Integer registType;   //功能类型,0=虚拟节点,1=功能节点,2=按扭
    private String icon;   //图标
    private Long upRegistId;   //上级节点
    private Integer sequence;   //显示顺序
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志

    private String refCorpName;   //公司名称
    private String refUpRegistName;   //上级节点名称
    private String refUpRegistCode;   //上级节点编码

    // 前端需要字段名称
    private List<Regist> children;  // 下级节点
    private String title;   //功能名称
    private Integer ichecked;    // 勾选状态
    private Boolean checked;    // 勾选状态

}

