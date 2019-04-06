package com.tilchina.timp.vo;

import com.tilchina.timp.model.Cargo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @version 1.0.0 2018/5/9
 * @author WangShengguang
 */
@Data
public class AssemblyVO implements Serializable{

    private Long trailerId;
    private String trailerCode;
    private String trailerName;
    private String trailerLong;
    private String fullWeight;

    private List<Cargo> cargos;
    private List<PendingOrderVO> orders;
}
