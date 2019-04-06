package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.WarehouseLoadingPlan;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 仓库装车计划
*
* @version 1.1.0
* @author Xiahong
*/
@Repository
public interface WarehouseLoadingPlanMapper extends BaseMapper<WarehouseLoadingPlan> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	int updateStatusByTransportOrderId(WarehouseLoadingPlan record);
	
	int updateByCarVinSelective(WarehouseLoadingPlan warehouseLoadingPlan);
	
	WarehouseLoadingPlan selectByCarVin(@Param("carVin")String CarVin, @Param("transportOrderId")Long transportOrderId);

	void logicDeleteByTransportOrderId(Long id);

	void logicDeleteByTransportOrderIds(int[] ids);
	
}
