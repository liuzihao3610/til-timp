package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.ReportOrder;
import com.tilchina.timp.model.ReportOrderDetail;
import com.tilchina.timp.model.ReportPhoto;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.model.WayReport;
import com.tilchina.timp.service.ReportOrderDetailService;
import com.tilchina.timp.service.ReportOrderService;
import com.tilchina.timp.service.ReportPhotoService;
import com.tilchina.timp.service.StockCarService;
import com.tilchina.timp.service.StockCarTransService;
import com.tilchina.timp.service.TransportOrderDetailService;
import com.tilchina.timp.service.TransportOrderService;
import com.tilchina.timp.service.TransporterService;
import com.tilchina.timp.service.WayReportService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.WayReportMapper;

/**
* 在途提报
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class WayReportServiceImpl extends BaseServiceImpl<WayReport> implements WayReportService {

	@Autowired
    private WayReportMapper wayreportmapper;
	
	@Autowired
	private ReportOrderService reportOrderService;
	
	@Autowired
	private ReportPhotoService reportPhotoService;
	
	@Autowired
	private TransportOrderDetailService transportOrderDetailService;
	
	@Autowired
	private TransportOrderService transportOrderService;
	
	@Autowired
	private TransporterService transporterService;
	
	@Autowired
	private StockCarService stockCarService;
	
	@Autowired
	private ReportOrderDetailService reportOrderDetailService;
	
	@Override
	protected BaseMapper<WayReport> getMapper() {
		return wayreportmapper;
	}
	
	public static String checkDouble(String nullable, String attName, String comment, double dou, int length, int scale) {
        String desc = comment+"["+attName+"]";
        String str=dou+"";
        if ("".equals(str.trim())) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if ((str.substring(0, str.indexOf("."))).length()> length) {
            return desc + " value "+dou+ " out of range [" + length + "].";
        }else if ((str.substring(str.indexOf("."),str.length())).length()> scale) {
        	return desc + " value "+dou+ " out of range [" + scale + "].";
		}

        
        return "";
    }
	
	private static boolean isNullAble(String nullable) {
        if ("YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase())) {
            return true;
        }
        return false;
    }

	@Override
	protected StringBuilder checkNewRecord(WayReport wayreport) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("YES", "reportStatus", "提报状态", wayreport.getReportStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "reportDate", "提报时间", wayreport.getReportDate()));
        s.append(CheckUtils.checkLong("YES", "driverId", "司机ID", wayreport.getDriverId(), 20));
        s.append(CheckUtils.checkLong("YES", "transporterId", "轿运车ID", wayreport.getTransporterId(), 20));
        s.append(CheckUtils.checkString("NO", "location", "位置", wayreport.getLocation(), 200));
        s.append(CheckUtils.checkLong("YES", "corpId", "公司ID", wayreport.getCorpId(), 20));
        s.append(checkDouble("NO", "originLng", "默认经度", wayreport.getOriginLng(), 10,30));
        s.append(checkDouble("NO", "originLat", "默认纬度", wayreport.getOriginLat(), 10,30));
        s.append(checkDouble("NO", "lng", "经度", wayreport.getLng(), 10,30));
        s.append(checkDouble("NO", "lat", "纬度", wayreport.getLat(), 10,30));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(WayReport wayreport) {
        StringBuilder s = checkNewRecord(wayreport);
        s.append(CheckUtils.checkPrimaryKey(wayreport.getReportId()));
		return s;
	}

	@Override
	@Transactional
	public void submission(String json,MultipartFile[] files) {
		log.debug("submission: {}",json);
        StringBuilder s;
        Environment env = Environment.getEnv();
        StockCar stockCar;
    	ReportOrder reportOrder = null;
    	ReportOrderDetail reportOrderDetail;
    	List<ReportOrderDetail> reportOrderDetails;
    	List<TransportOrder> transportOrders;
    /*	TransportOrder transportOrder;*/
    	Map<String, Object> map;
        try {
        	WayReport wayReport = JSON.parseObject(json,WayReport.class);
            s = checkNewRecord(wayReport);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            wayReport.setReportDate(new Date());
            wayReport.setDriverId(env.getUser().getUserId());
            wayReport.setCorpId(env.getCorp().getCorpId());
//          wayReport.setDriverId((long) 4);
//          wayReport.setCorpId((long) 1);
            map = new HashMap<>();
            map.put("driverId", env.getUser().getUserId());
            map.put("billStatus", 4);
            transportOrders = transportOrderService.queryByDriverId(map);
          
            if (transportOrders == null || transportOrders.size() == 0) {
				throw new BusinessException("您没有在途运单");
			}
            for (TransportOrder transportOrder : transportOrders) {
            	Transporter transporter  = transporterService.queryById(transportOrder.getTransporterId());
                if (transporter == null) {
    				throw new BusinessException("您没有承包轿运车");
    			}
                map.put("transportOrderId", transportOrder.getTransportOrderId());
                List<TransportOrderDetail> transportOrderDetails = transportOrderDetailService.queryList(map);
                wayReport.setTransporterId(transporter.getTransporterId());
                if( wayReport.getReportStatus() == null) {
                	//0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库
                	wayReport.setReportStatus(1);	
                }
                wayreportmapper.insertSelective(wayReport);
                reportOrderDetails = new ArrayList<>();
    			if (transportOrderDetails != null) {
    				if(transportOrderDetails.size() > 0) {
    					reportOrder = new ReportOrder();
    					reportOrder.setTransportOrderId(transportOrderDetails.get(0).getTransportOrderId());
    					stockCar = stockCarService.queryByTransOrderDetailId(transportOrderDetails.get(0).getTransportOrderDetailId());
    					if(stockCar != null) {
    						reportOrder.setOrderId(stockCar.getOrderId());
    					}
    					reportOrder.setReportId(wayReport.getReportId());
    					reportOrder.setCorpId(wayReport.getCorpId());
    					reportOrderService.add(reportOrder);
    				}
    				for (TransportOrderDetail detail : transportOrderDetails) {
    					reportOrderDetail = new ReportOrderDetail();
    					reportOrderDetail.setCarId(detail.getCarId());
    					reportOrderDetail.setCarVin(detail.getCarVin());
    					reportOrderDetail.setCorpId(wayReport.getCorpId());
    					reportOrderDetail.setReportId(wayReport.getReportId());
    					reportOrderDetail.setReportOrderId(reportOrder.getReportOrderId());
    					reportOrderDetail.setTransportDetailId(detail.getTransportOrderDetailId());
    					stockCar = stockCarService.queryByTransOrderDetailId(detail.getTransportOrderDetailId());
    					if(stockCar != null) {
    						reportOrderDetail.setOrdertDetailId(stockCar.getOrderDetailId());
    					}
    					reportOrderDetails.add(reportOrderDetail);
    				}
    			}
    			reportOrderDetailService.add(reportOrderDetails);
			}
            
			if (null!= files) {
				for (int i = 0; i < files.length; i++) {
					ReportPhoto reportPhoto=new ReportPhoto();
					try {
						String photoPath = FileUtil.uploadFile(files[i],"photoPath");
						reportPhoto.setPhotoPath(photoPath);
						reportPhoto.setPhotoType(0);
						reportPhoto.setReportId(wayReport.getReportId());
						reportPhoto.setCorpId(wayReport.getCorpId());
						reportPhotoService.add(reportPhoto);
					}catch (Exception e) {
						throw e;
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		
	}

	@Override
	@Transactional
	public List<WayReport> getByDriverId() {
//		Environment environment=Environment.getEnv();
//		long driverId=environment.getUser().getUserId();
		try {
			
			//int startdate = PropertiesUtils.getInt("start",7);
			String startdate=PropertiesUtils.getString("start");
			long driverId=1;
			Date end=new Date();
			Date start=DateUtil.getStartDate(Integer.parseInt(startdate));
			return wayreportmapper.getByDriverId(driverId,start,end);
		} catch (Exception e) {
			
			throw new BusinessException(e.getMessage());
		}
		
		
	}
}
