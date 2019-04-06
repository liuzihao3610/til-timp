package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.RailtransCabinStatus;
import com.tilchina.timp.model.RailtransOrderDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 铁路运输记录子表
 *
 * @author XueYuSong
 * @version 1.0.0
 */
@Repository
public interface RailtransOrderDetailMapper extends BaseMapper<RailtransOrderDetail> {

	List selectDistinctCabinNumber(@Param("latestUpdateTime") Date timeStamp);

    void delete(Long orderDetailId);

    void deleteList(Long[] orderDetailIds);

	void deleteByOrderId(Long transOrderId);

    int updateByCarVin(Map params);

    void updateCabinStatusByCabinNumber(RailtransCabinStatus status);

    void updateCabinStatusByCabinNumber(List<RailtransCabinStatus> status);

    List<RailtransOrderDetail> getByTransOrderId(Map params);

    List<RailtransOrderDetail> getByCarVin(List<String> carVins);

    List<RailtransOrderDetail> getByCabinNumber(List<String> cabinNumbers);
}
