package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.LoadingReservationDetail;
import com.tilchina.framework.mapper.BaseMapper;

/**
*  预约装车子表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface LoadingReservationDetailMapper extends BaseMapper<LoadingReservationDetail> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	int logicDeleteByLoadingId(Long id);
	
	List<LoadingReservationDetail> selecByLoadingReservationIdtList(Long loadingReservationId);

	void logicDeleteByTransportOrderIdsList(int[] ids);

	void logicDeleteByTransportOrderId(Long id);

	
}
