package com.tilchina.auth.vo;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.model.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @version 1.0.0 2018/3/25
 * @author WangShengguang   
 */
@Getter
@Setter
@ToString
public class UserInfoVO extends SuperVO{

    private User user;
    private Corp corp;
    private Unit unit;
    private List<UnitRelation> unitRelations;
    private Transporter transporter;
    // 权限列表
    private List<Regist> regists;
    private String token;
    // 可管理公司列表
    private List<Map<String, Object>> corpList;

}
