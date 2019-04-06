package com.tilchina.timp.vo;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
*	功能注册:树形结构
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class RegistTreeVO extends SuperVO {

    // 前端需要字段名称
    private Long registId;   // 功能ID
    private Integer registType;   //功能类型,0=虚拟节点,1=功能节点,2=按扭
    private String title;   // 功能名称
    private Long upRegistId;   //上级节点
    private Integer sequence;   //显示顺序
    private List<RegistTreeVO> children;  // 下级节点
    private Integer ichecked;    // 勾选状态
    private Boolean checked;    // 勾选状态
    private Boolean indeterminate;    // 父节点勾选状态

}

