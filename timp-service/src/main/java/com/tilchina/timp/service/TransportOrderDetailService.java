package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Freight;
import com.tilchina.timp.model.Loading;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;

import java.util.List;
import java.util.Map;

/**
* 运单子表
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransportOrderDetailService extends BaseService<TransportOrderDetail> {

	void updateSelective(List<TransportOrderDetail> record);

	void add(TransportOrderDetail record, TransportOrder transportOrder);
	
	void logicDeleteById(TransportOrderDetail transportOrderDetail);
	
	void logicDeleteByIdList(int[]  ids);
	
	void logicDeleteByTransportOrderIdList(int[]  ids);
	
	void logicDeleteByTransportOrderId(TransportOrderDetail transportOrderDetail); 
	
	List<TransportOrderDetail> queryByOrderId(Map<String, Object> map);

	List<TransportOrderDetail> queryByDriverId(Long driverId);
	
	void adds(List<TransportOrderDetail> details, TransportOrder transportOrder);
	
	/**
	 * 收发货单位分组
	 * @param map
	 * @param loadingType 0=发货 1=收货 2=收发货
	 * @return
	 */
	List<Loading>  queryLoadingCount(Map<String, Object> map,int loadingType);
	
	List<TransportOrderDetail> appQueryList(Map<String, Object> map);
	
	/**
	 * 根据车架号加运单id去查询运单子表信息
	 * @param carVin	车架号
	 * @param transportOrderId 运单主表id
	 * @return
	 */
	TransportOrderDetail queryByCarVin(String carVin,Long transportOrderId);

	List<TransportOrderDetail> selectByCarVin(String carVin);

	TransportOrderDetail getDetail(Map<String, Object> map);
	/**
	 * 通过车架号和驾驶员查询最近的一条运单子表
	 * @param map
	 * @return
	 */
	TransportOrderDetail getTransOrder(Map<String, Object> map);
	
	/**
	 * 运单子表参照
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<TransportOrderDetail> queryDetailRefer(Map<String, Object> data, int pageNum, int pageSize);

	/**
	 * 获取扫描到店的车辆列表
	 * @return
	 */
	List<Map<String, Object>> getArrivalList();

	/**
	 * 获取扫描装车的车辆列表
	 * @return
	 */
	List<Map<String, Object>> getLoadList();

	//扫描装车
	void updateBillStatus(List<TransportOrderDetail> transportOrderDetails);
	//扫描到店
	void updateStatus(List<TransportOrderDetail> transportOrderDetails);

	/*PageInfo<TransportOrderDetail>   queryByCorpIds(List<Long> lowerCorpIds);*/

	PageInfo<TransportOrderDetail>   queryByCorpIds(Map<String, Object> data, int pageNum, int pageSize);

	void maintainFreighPrice(Freight freight);

}
