package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.model.TransportSettlement;
import com.tilchina.timp.model.TransportSubsidy;
import com.tilchina.timp.vo.AppSettlementDetailVO;
import com.tilchina.timp.vo.AppSettlementVO;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
public interface TransportSettlementService {


    public TransportSettlement queryById(Long id);
    
    public PageInfo<TransportSettlement> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<TransportSettlement> queryList(Map<String, Object> map);
    
    public void add(TransportSettlement record);
    
    public void add(List<TransportSettlement> records);

    void updateSelective(TransportSettlement record);

	void updateSelective(List<TransportSettlement> records);

     void update(TransportSettlement record);
    
     void update(List<TransportSettlement> records);
    
     void deleteById(Long id);

     TransportSettlement queryByTransportOrderId(Long transportOrderId);

	List<TransportSettlement> queryByTransportOrderId(List<Long> transportOrderIds);

	 void deleteByTransportOrderId(Long id);

	 void deleteByTransportOrderId(int[] ids);
	
	/**
	 * 审核/取消审核
	 * @param updateTransportOrder 运单主表id
	 * @param type 0=审核 1=取消审核
	 */
	 void check(TransportOrder updateTransportOrder, int type);
	
	/**
	 * 下达/取消下达
	 * @param updateTransportOrder 运单主表id
	 * @param type 0=下达 1=取消下达
	 */
	 void transmit(TransportOrder updateTransportOrder, int type);
	 
	 /**
	  * 取消运单
	  * @param transportOrder
	  */
	public void bill(TransportOrder transportOrder);
	
	/**
	 * 添加补贴项目
	 * @param transportSubsidy
	 */
	public void addPrograms(TransportSubsidy transportSubsidy);
	
	/**
	 * 删除补贴项目
	 * @param transportSubsidyId
	 */
	public void delPrograms(Long transportSubsidyId);
	
	/**
	 * 创建运单结算信息表
	 * @param transportOrder
	 */
	public void add(TransportOrder transportOrder);
	
	/**
	 * 	修改运单结算信息表
	 * @param transportOrderId
	 */
	public void updateSelective(Long transportOrderId);




	/**
	 * app:查询运单结算信息
	 * @param map
	 * @param pageNum 
	 * @param pageSize 
	 * @return
	 */
	public PageInfo<AppSettlementVO> queryAppSettlement(Map<String, Object> map, int pageNum, int pageSize);
	
	/**
	 * app:查询运单结算明细信息
	 * @param transportOrderId	运单主表id
	 * @return
	 */
	public AppSettlementDetailVO queryPrograms(Long transportOrderId);

    void maintainFreighPrice(List<TransportSettlement> transportSettlements, List<TransportOrderDetail> details);

}
