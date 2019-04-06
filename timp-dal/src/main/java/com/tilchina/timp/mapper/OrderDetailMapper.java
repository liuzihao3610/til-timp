package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.OrderDetail;
import com.tilchina.timp.vo.PendingOrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 订单子表
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

	void deleteList(int[] data);

	OrderDetail queryByVin(String carVin);

	List<OrderDetail> queryByOrderId(Long data);

	void deleteByOrderId(Long orderId);

	List<OrderDetail> queryOrderDetail(Map<String, Object> map);

	OrderDetail getByVin(Long corpCarrierId, String carVin);

	List<PendingOrderVO> selectForAssembly(Map<String,Object> map);

	List<OrderDetail> getReferenceList();

	OrderDetail getByVin(Map<String, Object> map);

	void deleteByPrimaryKey(List<Long> id);

	List<OrderDetail> selectDetails(Map<String,Object> map);

	void updateByPrimaryKeySelective(List<OrderDetail> details);

	List<OrderDetail> selectForSettlement(Map<String,Object> map);

	OrderDetail getOne(Map<String, Object> map);

	void insertSelective(List<OrderDetail> details);

	List<OrderDetail> queryByCarVin(String carVin);

	OrderDetail queryForOutsourcingReconciliation(Map<String, Object> params);

	List<OrderDetail> selectDetailByDetailIds(@Param("detailIds") List<Long> detailIds);

	List<OrderDetail> selectByOrderId(Long orderId);
}
