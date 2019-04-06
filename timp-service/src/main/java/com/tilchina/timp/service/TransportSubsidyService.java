package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.TransportSettlement;
import com.tilchina.timp.model.TransportSubsidy;


/**
* 运单结算子表
*
* @version 1.0.0
* @author Xiahong
*/
public interface TransportSubsidyService {
		
	 public TransportSubsidy queryById(Long id);
	    
	    public PageInfo<TransportSubsidy> queryList(Map<String, Object> map, int pageNum, int pageSize);

	    public List<TransportSubsidy> queryList(Map<String, Object> map);
	    
	    public void add(TransportSubsidy record);
	    
	    public void add(List<TransportSubsidy> records);

	    void updateSelective(TransportSubsidy record);

	    public void update(TransportSubsidy record);
	    
	    public void update(List<TransportSubsidy> records);
	    
	    public void deleteById(Long id);
	    
		public List<TransportSubsidy> queryBySettlementId(Long transportSettlementId);

		public List<TransportSubsidy> queryByTransportOrderId(Long data);

}
