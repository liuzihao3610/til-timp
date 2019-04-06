package com.tilchina.timp.vo;

import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.model.Trailer;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @version 1.0.0 2018/5/19
 * @author WangShengguang
 */
@Data
public class AssemblyResultVO implements Serializable{

    private Long trailerId;
    private Trailer trailer;

    private List<Cargo> cargos;

    private List<PendingOrderVO> orders;

    private Integer max;

    private int useIndex;

    private int lastLong;

    private boolean isFull;

    public void addOrder(PendingOrderVO order){
        if(CollectionUtils.isEmpty(orders)){
            orders = new ArrayList<>();
        }
        orders.add(order);
    }
}
