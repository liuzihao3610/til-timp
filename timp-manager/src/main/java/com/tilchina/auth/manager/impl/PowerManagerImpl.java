package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.PowerManager;
import com.tilchina.auth.vo.RegistInfoVO;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.service.RegistService;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 *
 * @version 1.0.0 2018/4/17
 * @author WangShengguang
 */
@Service
public class PowerManagerImpl implements PowerManager {

    @Autowired
    private RegistService registService;

    @Override
    public RegistInfoVO getPower() {
        Environment env = Environment.getEnv();
        List<Regist> regists = registService.queryByUserId(env.getUser().getUserId());
        return buildTree(regists);
    }

    private RegistInfoVO buildTree(List<Regist> regists){
        Map<Long,List<Regist>> subMap = regists.stream().filter(regist -> regist.getUpRegistId() != null)
                .collect(Collectors.groupingBy(Regist::getUpRegistId));
        List<Regist> rootRegists = regists.stream().filter(regist -> regist.getUpRegistId() == null).collect(Collectors.toList());
        rootRegists.sort(Comparator.comparing(r -> r.getSequence()));

        RegistInfoVO root = new RegistInfoVO();
        rootRegists.forEach(regist -> {
            RegistInfoVO info = new RegistInfoVO();
            BeanUtils.copyProperties(regist,info);
            root.addItem(info);
            buildItem(info,subMap);
        });
        return root;
    }

    private void buildItem(RegistInfoVO upItem, Map<Long,List<Regist>> subMap){
        if(CollectionUtils.isEmpty(subMap.get(upItem.getRegistId()))){
            return;
        }
        List<Regist> items = subMap.get(upItem.getRegistId());
        for (Regist item : items) {
            RegistInfoVO info = new RegistInfoVO();
            BeanUtils.copyProperties(item,info);
            upItem.addItem(info);
            buildItem(info,subMap);
        }
    }
}
