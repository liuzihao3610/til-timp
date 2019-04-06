package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.RailtransOrder;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 铁路运输记录主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface RailtransOrderMapper extends BaseMapper<RailtransOrder> {

    void delete(Long orderId);
}
