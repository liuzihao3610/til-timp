package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.DriverSettlement;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
@Repository
public interface DriverSettlementMapper extends BaseMapper<DriverSettlement> {

	DriverSettlement selectByDriverId(@Param("driverId") Long driverId, @Param("settlementMonth")Date settlementMonth);

    List<DriverSettlement> appSelectList(Map<String,Object> map);

    void updateCheck(DriverSettlement driverSettlement);

    List<DriverSettlement> selectByDriverIds(@Param("driverIds")List<Long> driverIds);

    void  updateByPrimaryKeySelective(List<DriverSettlement> sttlements);

    List<Date>  selectSettlementMonth();

    List<DriverSettlement> selectBySettlementMonth(@Param("settlementMonth")String settlementMonth);
}
