package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.LoadingReservation;
import com.tilchina.framework.mapper.BaseMapper;

/**
*  预约装车主表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface LoadingReservationMapper extends BaseMapper<LoadingReservation> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	/**
	 * 审核
	 * @param loadingReservation
	 */
	void updateCheck(LoadingReservation loadingReservation);

	void logicDeleteByTransportOrderIds(int[] ids);

	void logicDeleteByTransportOrderId(Long id);
	
	
}
