package com.tilchina.timp.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverSettlement;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.vo.DriverSettlementVO;
import org.springframework.web.multipart.MultipartFile;

/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
public interface DriverSettlementService  {
	
	/**
	 * 结算
	 * @param driverSettlement
	 */
	void settlement(DriverSettlement driverSettlement);

    /**
     * 批量结算
     */
    void settlementList();
	
    public DriverSettlement queryById(Long id);

    public List<DriverSettlementVO>  queryDetailsById(Long id);
    
    public PageInfo<DriverSettlement> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<DriverSettlement> queryList(Map<String, Object> map);
    
    public void add(DriverSettlement record);
    
    public void add(List<DriverSettlement> records);

    void updateSelective(DriverSettlement record);

    void voidorderReceiving(Long driverSettlementId);

    public void update(DriverSettlement record);
    
    public void update(List<DriverSettlement> records);

    void updateSelective(List<DriverSettlement>record);
    
    public void deleteById(Long id);

    List<DriverSettlement> queryByDriverIds(List<Long> driverIds);

    DriverSettlement queryByDriverId(Long driverId,Date settlementMonth);

    PageInfo<DriverSettlement> appQueryList(Map<String,Object> data, int pageNum, int pageSize);

    void updateCheck(Long data, int i);

    /**
     * 结算价格更改时维护司机结算信息
     * @param driverSettlements
     */
    void maintainPrice(List<DriverSettlement> driverSettlements,List<TransportOrderDetail> details);

    /**
     * 导入补贴信息
     * @param file
     * @param settlementMonth
     */
    void importSubsidy(MultipartFile file, String settlementMonth) throws Exception;

    /**
     * 结算月份：动态下拉框
     * @return
     */
    List<String> querySettlementMonth();

    /**
     * 根据结算月份查询司机结算信息
     * @param settlementMonth
     * @return
     */
    List<DriverSettlement> queryBySettlementMonth(String settlementMonth);

}
