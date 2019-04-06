package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.tilchina.timp.model.ContactsOld;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.vo.TransportPlanVO;

/**
 * App 运单
 * @author Xiahong
 *
 */
public interface AppTransOrderService {
	/**
	 * 查询已分配、未完成的运单
	 * @param map
	 * @return
	 */
	List<TransportOrder> appQueryList(Map<String, Object> map);
	/**
	 * 根据传入得经纬度提示距离为150公里内得收货单位得联系方式
	 * @param map
	 * @return
	 */
	List<ContactsOld>  appQueryUnitContacts(Map<String, Object> map);
	/**
	 * 历史运单查询，只显示当前司机、已完成的运单
	 * @param map
	 * @return
	 */
	List<TransportOrder> appQueryListByPrimary(Map<String, Object> map);
	/**
	 * 确认接单
	 * @param record
	 */
	void updateStatusCheck(TransportOrder record); 
	/**
	 * 发车
	 * @param record
	 */
	void updateStatusRun(TransportOrder record); 
	/**
	 * 查看运输计划
	 * @param map
	 * @return
	 */
	TransportPlanVO appQueryListRoute(Map<String, Object> map);
	
	/**
	 * 在途/地图：查看运输计划
	 * @param data
	 * @return
	 */
	List<TransportPlanVO> appQueryListAassistRoute(Map<String, Object> data);
	
}
