package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 运单主表
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransportOrderMapper extends BaseMapper<TransportOrder> {
	
	/**
	 * 单条：逻辑删除
	 * @param id
	 * @return
	 */
	int logicDeleteByPrimaryKey(Long id);
	
	
	/**
	 * 批量：逻辑删除
	 * @param ids
	 * @return
	 */
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	/**
	 * 根据司机id查询运单
	 * @param map
	 * @return
	 */
	List<TransportOrder> queryByDriverId(Map<String, Object> map);
	
	/**
	 * 参照
	 * @param map
	 * @return
	 */
	List<TransportOrder> selectRefer(Map<String, Object> map);
	
	/**
	 * 审核
	 * @param updateTransportOrder
	 */
	void updateCheck(TransportOrder updateTransportOrder);

	/**
	 * 下达
	 * @param transportOrder
	 */
	void updateByTransmit(TransportOrder transportOrder);

	/**
	 * 通过订单ID 获取运单
	 * @param orderId
	 * @return
	 */
	List<TransportOrder> getByOrderId(long orderId);

	List<TransportOrder> selectByTransportOrderIds(@Param("transportOrderIds") List<Long> transportOrderIds);

}
