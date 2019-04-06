package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by demon on 2018/2/8.
 */
@Data

public class BackTraceVO implements Serializable{
    private Long id;
    private Integer count;

    public BackTraceVO() {
    }

    public BackTraceVO(Long id, Integer count) {
        this.id = id;
        this.count = count;
    }
}
