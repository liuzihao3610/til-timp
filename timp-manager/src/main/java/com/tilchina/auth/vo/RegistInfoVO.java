package com.tilchina.auth.vo;

import com.tilchina.timp.model.Regist;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @version 1.0.0 2018/4/11
 * @author WangShengguang   
 */
@Getter
@Setter
@ToString
public class RegistInfoVO extends Regist {

    public List<RegistInfoVO> items;

    public void addItem(RegistInfoVO item){
        if(CollectionUtils.isEmpty(items)){
            items = new ArrayList<>();
        }
        this.items.add(item);
    }

}
