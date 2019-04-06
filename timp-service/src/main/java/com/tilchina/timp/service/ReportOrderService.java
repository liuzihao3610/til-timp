package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.ReportOrder;
import com.tilchina.timp.vo.WayReportVO;

/**
* 在途提报运单
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface ReportOrderService{
	

    public ReportOrder queryById(Long id);
    
    public PageInfo<WayReportVO> routeQuery(Map<String, Object> map, int pageNum, int pageSize);
    
    public PageInfo<ReportOrder> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<ReportOrder> queryList(Map<String, Object> map);
    
    public void add(ReportOrder record);
    
    public void add(List<ReportOrder> records);

    public void deleteById(Long id);
    
}
