package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.RailtransOrder;

/**
* 铁路运输记录主表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface RailtransOrderService extends BaseService<RailtransOrder> {

    void update(RailtransOrder railtransOrder);

    void delete(Long orderId);

    void deleteList(Long[] orderIds);
}
