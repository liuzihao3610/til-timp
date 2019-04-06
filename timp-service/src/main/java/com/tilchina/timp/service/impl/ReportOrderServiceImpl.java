package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.model.ReportOrder;
import com.tilchina.timp.model.ReportOrderDetail;
import com.tilchina.timp.model.ReportPhoto;
import com.tilchina.timp.model.WayReport;
import com.tilchina.timp.service.ReportOrderDetailService;
import com.tilchina.timp.service.ReportOrderService;
import com.tilchina.timp.service.ReportPhotoService;
import com.tilchina.timp.service.WayReportService;
import com.tilchina.timp.vo.WayReportVO;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ReportOrderMapper;

/**
* 在途提报运单
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class ReportOrderServiceImpl implements ReportOrderService {

	@Autowired
    private ReportOrderMapper reportordermapper;
	
	@Autowired
    private ReportPhotoService reportPhotoService;
	
	@Autowired
    private ReportOrderDetailService reportOrderDetailService;
	
	@Autowired
    private WayReportService wayReportService;
	
	protected StringBuilder checkNewRecord(ReportOrder reportorder) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表ID", reportorder.getTransportOrderId(), 20));
		s.append(CheckUtils.checkLong("NO", "transportDetailId", "订单主表ID", reportorder.getOrderId(), 20));
		s.append(CheckUtils.checkLong("YES", "reportId", "在途提报ID", reportorder.getReportId(), 20));
		s.append(CheckUtils.checkLong("YES", "corpId", "公司ID", reportorder.getCorpId(), 20));
		return s;
	}

	protected StringBuilder checkUpdate(ReportOrder reportorder) {
        StringBuilder s = checkNewRecord(reportorder);
        s.append(CheckUtils.checkPrimaryKey(reportorder.getReportOrderId()));
		return s;
	}

	@Override
    public ReportOrder queryById(Long id) {
        log.debug("query: {}",id);
        return reportordermapper.selectByPrimaryKey(id);
    }

/*    @Override
    public PageInfo<ReportOrder> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<ReportOrder>(reportordermapper.selectList(map));
    }
    */
    @Override
    public List<ReportOrder> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return reportordermapper.selectList(map);
    }

    @Override
    public void add(ReportOrder record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            reportordermapper.insert(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }

    @Override
    public void add(List<ReportOrder> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	ReportOrder record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            reportordermapper.insert(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("批量保存失败！", e);
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        reportordermapper.deleteByPrimaryKey(id);
    }

	 @Override
    public PageInfo<ReportOrder> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<ReportOrder> pageInfo,reportOrderPageInfo;
        StringBuilder carVinMessage;
        List<ReportOrderDetail> reportOrderDetails;
        ReportOrderDetail reportOrderDetail;
        ReportOrder report;
        WayReport wayReport;
        List<ReportPhoto> reportPhotos;
        List<ReportOrder> reportOrders;
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	
        	carVinMessage = new StringBuilder();
        	if(map.get("carVin") != null && !StringUtils.isBlank(map.get("carVin").toString())) {
        		reportOrders = new ArrayList<ReportOrder>();
        		reportOrderDetails = reportOrderDetailService.queryByCarVinList(map.get("carVin").toString());
        			for (int i = 0; i < reportOrderDetails.size(); i++) {
        				reportOrderDetail = reportOrderDetails.get(i);
        				if(reportOrderDetail.getReportId() != null) {
							reportPhotos = reportPhotoService.queryByReportIdList(reportOrderDetail.getReportId());
							report =  queryById(reportOrderDetail.getReportOrderId());
							if(report != null) {
								if(!carVinMessage.toString().contains(reportOrderDetail.getCarVin())) {
		            				carVinMessage.append(reportOrderDetail.getCarVin()+"；");
		            			}
								wayReport = wayReportService.queryById(reportOrderDetail.getReportId());
								report.setWayReport(wayReport);
								report.setCarVinMessage(carVinMessage.toString());
								report.setReportPhoto(reportPhotos);
								reportOrders.add(report);
							}
        				}
					}
        		reportOrders = reportOrders.stream().collect(collectingAndThen( toCollection(() -> new TreeSet<>(comparingLong(ReportOrder :: getReportOrderId))), ArrayList::new));
        		reportOrderPageInfo = new PageInfo<ReportOrder>(reportOrders);
        		return reportOrderPageInfo;
        	}else {
        		pageInfo = new PageInfo<ReportOrder>(reportordermapper.selectList(map));
            	for (ReportOrder reportOrder : pageInfo.getList()) {
            		reportOrderDetails = reportOrderDetailService.queryByReportIdList(reportOrder.getReportId());
            		carVinMessage = new StringBuilder();
            		for (int i = 0; i < reportOrderDetails.size(); i++) {
            			reportOrderDetail = reportOrderDetails.get(i);
            			if(!carVinMessage.toString().contains(reportOrderDetail.getCarVin())) {
            				carVinMessage.append(reportOrderDetail.getCarVin()+"；");
            			}
    				}
            		reportPhotos = reportPhotoService.queryByReportIdList(reportOrder.getReportId());
            		wayReport = wayReportService.queryById(reportOrder.getReportId());
        			reportOrder.setWayReport(wayReport);
        			reportOrder.setCarVinMessage(carVinMessage.toString());
        			reportOrder.setReportPhoto(reportPhotos);
    			}
        	}
        	return pageInfo;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
    }

	@Override
	public PageInfo<WayReportVO> routeQuery(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("routeQuery: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<WayReportVO> wayReportVOPageInfo;
        PageInfo<ReportOrder> pageInfo;
        StringBuilder carVinMessage;
        List<ReportOrderDetail> reportOrderDetails;
        List<WayReportVO> wayReportVOs;
        WayReportVO wayReportVO;
        ReportOrderDetail reportOrderDetail;
        ReportOrder report;
        WayReport wayReport;
        List<ReportPhoto> reportPhotos;
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	wayReportVOs = new ArrayList<WayReportVO>();
        	if(map.get("carVin") != null && !StringUtils.isBlank(map.get("carVin").toString())) {
        		reportOrderDetails = reportOrderDetailService.queryByCarVinList(map.get("carVin").toString());
        			for (int i = 0; i < reportOrderDetails.size(); i++) {
        				carVinMessage = new StringBuilder();
        				reportOrderDetail = reportOrderDetails.get(i);
        				if(reportOrderDetail.getReportId() != null) {
							reportPhotos = reportPhotoService.queryByReportIdList(reportOrderDetail.getReportId());
							report =  queryById(reportOrderDetail.getReportOrderId());
							wayReportVO = new WayReportVO();
							if(report != null) {
								if(!carVinMessage.toString().contains(reportOrderDetail.getCarVin())) {
		            				carVinMessage.append(reportOrderDetail.getCarVin()+"；");
		            			}
								wayReport = wayReportService.queryById(reportOrderDetail.getReportId());
								//在途查询
								wayReportVO.setReportOrderId(report.getReportOrderId());
								wayReportVO.setTransportOrderId(report.getTransportOrderId());
								wayReportVO.setOrderId(report.getOrderId());
								wayReportVO.setReportId(report.getReportId());
								wayReportVO.setCorpId(report.getCorpId());
								wayReportVO.setRefTransporterCode(report.getRefTransporterCode());
								wayReportVO.setRefTractirPlateCode(report.getRefTractirPlateCode());
								wayReportVO.setRefOrderCode(report.getRefOrderCode());
								wayReportVO.setRefDriverName(report.getRefDriverName());
								wayReportVO.setRefDriverCode(report.getRefDriverCode());
								wayReportVO.setRefTransportOrderCode(report.getRefTransportOrderCode());
								wayReportVO.setRefCorpName(report.getRefCorpName());
								wayReportVO.setCarVinMessage(carVinMessage.toString());
								wayReportVO.setReportPhoto(reportPhotos);
								wayReportVO.setReportStatus(wayReport.getReportStatus());
								wayReportVO.setReportDate(wayReport.getReportDate());
								wayReportVO.setLocation(wayReport.getLocation());
								wayReportVO.setOriginLng(wayReport.getOriginLng());
								wayReportVO.setOriginLat(wayReport.getOriginLat());
								wayReportVO.setLng(wayReport.getLng());
								wayReportVO.setLat(wayReport.getLat());
								wayReportVOs.add(wayReportVO);
							}
        				}
					}
        			wayReportVOs = wayReportVOs.stream().collect(collectingAndThen( toCollection(() -> new TreeSet<>(comparingLong(WayReportVO :: getReportOrderId))), ArrayList::new));
        		wayReportVOPageInfo = new PageInfo<WayReportVO>(wayReportVOs);
        		return wayReportVOPageInfo;
        	}else {
        		map.put("orderByClause", "REPORT_ORDER_ID");
        		pageInfo = new PageInfo<ReportOrder>(reportordermapper.selectList(map));
            	for (ReportOrder reportOrder : pageInfo.getList()) {
            		reportOrderDetails = reportOrderDetailService.queryByReportIdList(reportOrder.getReportId());
            		wayReportVO = new WayReportVO();
            		carVinMessage = new StringBuilder();
            		//在途查询
					wayReportVO.setReportOrderId(reportOrder.getReportOrderId());
					wayReportVO.setTransportOrderId(reportOrder.getTransportOrderId());
					wayReportVO.setOrderId(reportOrder.getOrderId());
					wayReportVO.setReportId(reportOrder.getReportId());
					wayReportVO.setCorpId(reportOrder.getCorpId());
					wayReportVO.setRefTransporterCode(reportOrder.getRefTransporterCode());
					wayReportVO.setRefTractirPlateCode(reportOrder.getRefTractirPlateCode());
					wayReportVO.setRefOrderCode(reportOrder.getRefOrderCode());
					wayReportVO.setRefDriverName(reportOrder.getRefDriverName());
					wayReportVO.setRefDriverCode(reportOrder.getRefDriverCode());
					wayReportVO.setRefTransportOrderCode(reportOrder.getRefTransportOrderCode());
					wayReportVO.setRefCorpName(reportOrder.getRefCorpName());
					
            		for (int i = 0; i < reportOrderDetails.size(); i++) {
            			reportOrderDetail = reportOrderDetails.get(i);
            			if(!carVinMessage.toString().contains(reportOrderDetail.getCarVin())) {
            				carVinMessage.append(reportOrderDetail.getCarVin()+"；");
            			}
    				}
            		reportPhotos = reportPhotoService.queryByReportIdList(reportOrder.getReportId());
            		wayReport = wayReportService.queryById(reportOrder.getReportId());
            		//在途查询
					wayReportVO.setCarVinMessage(carVinMessage.toString());
					wayReportVO.setReportPhoto(reportPhotos);
					wayReportVO.setReportStatus(wayReport.getReportStatus());
					wayReportVO.setReportDate(wayReport.getReportDate());
					wayReportVO.setLocation(wayReport.getLocation());
					wayReportVO.setOriginLng(wayReport.getOriginLng());
					wayReportVO.setOriginLat(wayReport.getOriginLat());
					wayReportVO.setLng(wayReport.getLng());
					wayReportVO.setLat(wayReport.getLat());
					wayReportVOs.add(wayReportVO);
    			}
            	wayReportVOPageInfo = new PageInfo<WayReportVO>(wayReportVOs);
        	}
        	wayReportVOPageInfo.setTotal(pageInfo.getTotal());
        	return wayReportVOPageInfo;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
    }

}
