package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 *
 * @version 1.0.0 2018/5/22
 * @author WangShengguang
 */
@Getter
@Setter
@ToString
public class OrderPieceVO extends SuperVO{

    private Long corpCustomerId;
    private Long cityId;
    private Long unitId;
    private Integer workType;

}
