package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Loading;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 运单子表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransportOrderDetailMapper extends BaseMapper<TransportOrderDetail> {
	
	int logicDeleteByPrimaryKey(TransportOrderDetail transportOrderDetail);

    void updateByPrimaryKeySelective(List<TransportOrderDetail> record);
	
	int logicDeleteByTransportOrderKeyList(int[] ids);
	
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	List<TransportOrderDetail> selectByOrderKey(Map<String, Object> map);

	List<TransportOrderDetail> queryByDriverId(Long driverId);
	
	List<Loading> selectLoadingCount(Map<String, Object> map);
	
	List<Loading> selectEndUnitCount(Long transportOrderId);
	
	List<Loading> selectStartUnitCount(Long transportOrderId);

	List<TransportOrderDetail> appSelectList(Map<String, Object> map);

	TransportOrderDetail queryByCarVin(@Param("carVin")String CarVin, @Param("transportOrderId")Long transportOrderId);

	List<TransportOrderDetail> selectByCarVin(String carVin);

	TransportOrderDetail getDetail(Map<String, Object> map);

	TransportOrderDetail getTransOrder(Map<String, Object> map);
	
	/**
	 * 参照
	 * @param map
	 * @return
	 */
	List<TransportOrderDetail> selectRefer(Map<String, Object> map);

	/**
	 * 获取扫描到店车辆列表
	 * @param driverId
	 * @return
	 */
    List<Map<String,Object>> getArrivalList(long driverId);

	/**
	 * 获取扫描装车车辆列表
	 * @param driverId
	 * @return
	 */
	List<Map<String,Object>> getLoadList(long driverId);

/*	List<TransportOrderDetail> selectByCorpIds(List<Long> lowerCorpIds);*/

	List<TransportOrderDetail> selectByCorpIds(Map<String,Object> map);

	List<TransportOrderDetail> selectByDetaiIds(List<Long> detaiIds);

	List<TransportOrderDetail>  selectByFreightId(long freightId);

}
