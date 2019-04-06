package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.ReportOrder;

/**
* 在途提报运单
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface ReportOrderMapper{
	
	int deleteByPrimaryKey(Long id);
	
    void insert(ReportOrder record);
    
    void insert(List<ReportOrder> records);
	
    List<ReportOrder> selectList(Map<String, Object> map);
    
    ReportOrder selectByPrimaryKey(Long id);

    
}
