package com.tilchina.timp.vo;

import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.model.Trailer;
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
public class TransporterCountVO implements Serializable{

    private Long trailerId;
    private Trailer trailer;
    private Integer max;
    private List<Cargo> cargos;

    private Integer count;
    private int useCount;
    private int tempUseCount;

}
