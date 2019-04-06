package com.tilchina.timp.vo;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.model.Regist;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
* 功能注册:返回
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class RegistVO extends SuperVO {

    private Long registId;   // 功能ID
    private String registCode;   // 功能编码
    private String registName;   // 功能名称
    private String urlPath;   // URL地址
    private String refUpRegistName;   // 上级节点
    private Integer registType;   // 功能类型,0=虚拟节点,1=功能节点,2=按扭
    private Long upRegistId;   // 上级节点

}

