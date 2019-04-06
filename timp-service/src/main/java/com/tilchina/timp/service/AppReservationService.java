package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.tilchina.timp.model.LoadingReservation;

/**
 * App 预约装车
 * @author Xiahong
 *
 */
public interface AppReservationService {
	
	/**
	 * 预约
	 * @param record
	 */
	void appointment(LoadingReservation record);
	
	/**
	 * 查询
	 * @return
	 */
	List<LoadingReservation> queryList();
	
	/**
	 * 历史查询
	 * @param map
	 * @return
	 */
	List<LoadingReservation> queryList(Map<String, Object> map);

}
