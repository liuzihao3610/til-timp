package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.RailtransCabinStatus;
import com.tilchina.timp.model.RailtransOrderDetail;
import com.tilchina.timp.model.RailtransParsedTextModel;

import java.util.List;
import java.util.Map;

/**
* 铁路运输记录子表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface RailtransOrderDetailService extends BaseService<RailtransOrderDetail> {

    List selectDistinctCabinNumber();

    void deleteByOrderId(Long orderId);

    void delete(Long orderDetailId);
    void deleteList(Long[] orderDetailId);

    int updateByCarVin(RailtransParsedTextModel model);
    String updateRangeByCarVin(List<RailtransParsedTextModel> models);

    void updateCabinStatusByCabinNumber(RailtransCabinStatus railtransCabinStatus);
    void updateRangeCabinStatusByCabinNumber(List<RailtransCabinStatus> railtransCabinStatuses);

    List<RailtransOrderDetail> getByTransOrderId(Map<String, Object> params);

    List<RailtransOrderDetail> getByCarVin(List<String> carVins);

    List<RailtransOrderDetail> getByCabinNumber(List<String> cabinNumbers);
}
