package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransportOrderService extends BaseService<TransportOrder> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);

	List<TransportOrder> queryByIds(List<Long> transportOrderIds);
	
/*	void logicDeleteByDetailId(Long id);*/
	
	TransportOrder queryById(Map<String, Object> map, int pageNum, int pageSize);
	
	/**
	 * 根据司机id查询运单信息
	 * @param map
	 * @return
	 */
	List<TransportOrder> queryByDriverId(Map<String, Object> map);

	List<TransportOrder> querySettlementByDriverId(Map<String, Object> map);

	/**
	 * 参照
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<TransportOrder> queryRefer(Map<String, Object> map, int pageNum, int pageSize);
	
	/**
	 * 审核
	 * @param data
	 */
	void updateCheck(TransportOrder data);
	
	/**
	 * 下达
	 * @param
	 */
	void updateTransmit(Long transportOrderId);
	
	/**
	 * 
	 * @param transportOrderId 运单主表id
	 * @param transmitType	0=下达 1= 取消下达
	 */
/*	void updateTransmit(Long transportOrderId,Integer transmitType);*/
	
	
	/**
	 * 取消审核
	 * @param
	 */
	void updateCancelCheck(TransportOrder transportOrder);
	
	/**
	 *  修改车辆状态
	 * @param transportOrderId	运单主表id
	 * @param carStatus	车辆状态
	 * @param type 0=有效 1=无效
	 */
	void updateCarStatus(Long transportOrderId,Integer carStatus,Integer type);
	
	/**
	 * 取消下达
	 * @param data
	 */
	void updateCancelTransmit(Long data);
	
	/**
	 * 取消下达
	 * @param carVins 车架号
	 */
	void updateCancelTransmit(List<String> carVins);
	
	/**
	 * 取消运单
	 * @param data
	 */
	void updateBill(Long data);
	
	/**
	 * 修改到店时间（对外）
	 * @param transportOrderDetails
	 */
	void updateRrriveDateBesides(List<TransportOrderDetail> transportOrderDetails);
	
	/**
	 * 修改到车检标志
	 * @param transportOrder
	 */
	void updateExamination(TransportOrder transportOrder);
	
	/**
	 * 运单导入
	 * @param file 文件路劲
	 * @throws Exception
	 */
	void importAssembly(MultipartFile file) throws Exception;

    void getCustomerPrice(TransportOrder order);
    /**
     * 通过订单ID 获取运单
     * @param map
     * @param pageSize 
     * @param pageNum 
     * @return
     */
	PageInfo<TransportOrder> getByOrderId(Map<String, Object> map, int pageNum, int pageSize);


}
