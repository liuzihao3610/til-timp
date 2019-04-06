package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.LoadingReservationDetail;

/**
* 预约装车计划子表
*
* @version 1.0.0
* @author Xiahong
*/
public interface LoadingReservationDetailService extends BaseService<LoadingReservationDetail> {
	
	 void logicDeleteById(Long id);
	 
	 void logicDeleteByReservationId(Long id);
		
	 void logicDeleteByIdList(int[]  ids);
	 
     List<LoadingReservationDetail> queryList(Long loadingReservationId);

	void logicDeleteByTransportOrderIds(int[] ids);

	void logicDeleteByTransportOrderId(Long id);

	List<LoadingReservationDetail> queryByReservationId(Long loadingReservationId);
	 
	/* LoadingReservationDetail queryByReservationId(Long id);*/
	 
}
