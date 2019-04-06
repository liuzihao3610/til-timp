package com.tilchina.timp.service.impl;

import com.tilchina.timp.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.service.DriverReportService;
import com.tilchina.timp.service.DriverService;
import com.tilchina.timp.service.DriverStatusService;
import com.tilchina.timp.service.TransporterAndDriverService;
import com.tilchina.timp.service.TransporterReportService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.utils.CheckReportStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DriverReportMapper;

/**
* 司机提报
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class DriverReportServiceImpl implements DriverReportService {

	@Autowired
    private DriverReportMapper driverreportmapper;
    
	@Autowired
	private DriverService driverService;
	
	@Autowired
	private DriverStatusService  driverStatusService;
	
	@Autowired
	private TransporterReportService transporterReportService;
	
	protected BaseMapper<DriverReport> getMapper() {
		return driverreportmapper;
	}

	protected StringBuilder checkNewRecord(DriverReport driverreport) {
		StringBuilder s = new StringBuilder();
		driverreport.setCreateDate(new Date());
        s.append(CheckUtils.checkInteger("NO", "reportType", "提报类型:0=司机+车辆,1=司机,2=车辆", driverreport.getReportType(), 10));
		s.append(CheckUtils.checkInteger("YES", "reportStatus", "提报状态:0=默认,1=在途,2=到店,3=回程,4=请假,5=培训", driverreport.getReportStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "driverStatus", "司机状态:0=默认,1=在途,2=到店,3=回程,4=请假,5=培训", driverreport.getDriverStatus(), 10));
        s.append(CheckUtils.checkInteger("YES", "transporterStatus", "骄运车状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库", driverreport.getTransporterStatus(), 10));
        s.append(CheckUtils.checkString("YES", "location", "位置(通过经纬度得到位置)", driverreport.getLocation(), 100));
        s.append(CheckUtils.checkDate("YES", "startTime", "开始时间", driverreport.getStartDate()));
        s.append(CheckUtils.checkDate("YES", "endTime", "结束时间", driverreport.getEndDate()));
        s.append(CheckUtils.checkString("YES", "remark", "备注", driverreport.getRemark(), 200));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", driverreport.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", driverreport.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(DriverReport driverreport) {
        StringBuilder s = checkNewRecord(driverreport);
        s.append(CheckUtils.checkPrimaryKey(driverreport.getDriverReportId()));
		return s;
	}
    
	@Transactional
	@Override
	public void dt(DriverReport driverReport) {
        log.debug("dt: {}",driverReport);
        Map<String, Long> map;
       /* TransporterAndDriver transporterAndDriver = null;*/
        Environment env =  Environment.getEnv();
        TransporterReport transporterReport = null;
        try {
        	map = new HashMap<>();
        	map.put("driverId", env.getUser().getUserId());
        	map.put("transporterId",driverReport.getTransporterId());
        	driverReport.setDriverStatus(0);
        /*	transporterAndDriver =  transporterAndDriverService.queryByKeyId(map);
        	transporterAndDriver.getTransporterId();*/
        	//添加 轿运车提报数据
        	transporterReport = new TransporterReport();
        	transporterReport.setLocation(driverReport.getLocation());
        	transporterReport.setLng(driverReport.getLng());
        	transporterReport.setLat(driverReport.getLat());
        	transporterReport.setTransporterStatus(0);
        	transporterReport.setTransporterId(driverReport.getTransporterId());
        	transporterReport.setStartDate(driverReport.getStartDate());
        	transporterReport.setEndDate(driverReport.getEndDate());
        	transporterReport.setRemark(driverReport.getRemark());
        	//轿运车提报/司机提报
        	transporterReportService.report(transporterReport);
        	report(driverReport);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
    }
	
	@Transactional
	@Override
	public void report(DriverReport updateDriverReport) {
		 log.debug("report: {}",updateDriverReport);
		 DriverStatus driverStatus = null;
		 Long delDriverStatusId;
		 Environment env = Environment.getEnv();
		 User driver = null;
		 StringBuilder s;
		 try {
			driver = driverService.queryById(env.getUser().getUserId());
	        updateDriverReport.setCreator(env.getUser().getUserId());
	        updateDriverReport.setCorpId(driver.getCorpId());
/*			driver = driverService.queryById(Long.valueOf(4));
	        updateDriverReport.setCreator(Long.valueOf(4));
	        updateDriverReport.setCorpId(Long.valueOf(1));*/
	        updateDriverReport.setFlag(0);
	        updateDriverReport.setDriverId(env.getUser().getUserId());
	        s = checkNewRecord(updateDriverReport);
	        if (!StringUtils.isBlank(s)) {
	             throw new BusinessException("数据检查失败：" + s.toString());
	        }
	        //司机状态:0=空闲 1=事假 2=病假 3=培训 4=学习 5=审证 6=在途 7=到店 8=待回程 9=回程
	        if(updateDriverReport.getDriverStatus() > 9) {
	        	throw new BusinessException("请输入有效的司机状态！");
	        }
	        driverStatus = driverStatusService.queryByDirverId(env.getUser().getUserId());
	        if(driverStatus != null) {
	        	delDriverStatusId = driverStatus.getDriverStatusId();
	        	if(updateDriverReport.getTransporterId() != null) {
	        		driverStatus = driverStatusService.queryByDirverIdAndTransportId(env.getUser().getUserId(),updateDriverReport.getTransporterId());
					if(driverStatus == null) {
						driverStatusService.deleteById(delDriverStatusId);
					}
	        	}
	        }
            if(driverStatus != null) {
        	/*	CheckReportStatus.checkDriverReportStatuc(updateDriverReport.getDriverStatus(), driverStatus.getDriverStatus(),0);*/
            	driverStatus.setLng(updateDriverReport.getLng());
            	driverStatus.setLat(updateDriverReport.getLat());
            	driverStatus.setLocation(updateDriverReport.getLocation());
            	driverStatus.setStartDate(updateDriverReport.getStartDate());
        		driverStatus.setEndDate(updateDriverReport.getEndDate());
        		driverStatus.setDriverStatus(updateDriverReport.getDriverStatus());
        		driverStatus.setRemark(updateDriverReport.getRemark());
        		driverStatusService.updateSelective(driverStatus);
        	}else {
        		driverStatus = new DriverStatus();
            	driverStatus.setLng(updateDriverReport.getLng());
            	driverStatus.setLat(updateDriverReport.getLat());
            	driverStatus.setLocation(updateDriverReport.getLocation());
            	driverStatus.setCreator(env.getUser().getUserId());
            	driverStatus.setDriverId(env.getUser().getUserId());
            	driverStatus.setCorpId(updateDriverReport.getCorpId());
            	driverStatus.setDriverStatus(updateDriverReport.getDriverStatus());
            	driverStatus.setTransporterTransporterId(updateDriverReport.getTransporterId());
            	driverStatus.setStartDate(updateDriverReport.getStartDate());
        		driverStatus.setEndDate(updateDriverReport.getEndDate());
        		driverStatus.setTransporterTransporterId(updateDriverReport.getTransporterId());
            	driverStatus.setRemark(updateDriverReport.getRemark());
            	driverStatus.setFlag(updateDriverReport.getFlag());
            /*	CheckReportStatus.checkDriverReportStatuc(updateDriverReport.getDriverStatus(), driverStatus.getDriverStatus(),1);*/
            	driverStatusService.add(driverStatus);
        	}
            driverreportmapper.insertSelective(updateDriverReport);
		} catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}

	@Override
	public PageInfo<DriverReport> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		 log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		 PageInfo<DriverReport> driverReports = null;
		 PageHelper.startPage(pageNum, pageSize);
		 try {
		 	Boolean flag = true;
			 Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			 if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				 data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			 }
			 if(data.get("refEndDate") != null && !"".equals(data.get("refEndDate"))){
				 data.put("refEndDate",DateUtil.dateToString(DateUtil.endTime(data.get("refEndDate").toString())));
			 }
			 if (data.get("transporterStatus") == null || "".equals(data.get("transporterStatus"))){
				 data.put("refEndTime",data.get("refEndDate"));
				 data.put("refStartTime",data.get("refStartDate"));
				 data.put("refStartDate",null);
				 data.put("refEndDate",null);
				 flag = false;
			 }
			 if (flag){
				 data.put("refEndTime",data.get("refEndDate"));
				 data.put("refStartTime",data.get("refStartDate"));
				 data.put("refStartDate",null);
				 data.put("refEndDate",null);
			 }
			 driverReports = new PageInfo<DriverReport>(driverreportmapper.selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
      }
	        return driverReports;
	}
	
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<DriverReport> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<DriverReport>(driverreportmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public PageInfo<DriverReport> queryByDriverIdList(Map<String, Object> map, int pageNum, int pageSize) {
		 log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		 PageInfo<DriverReport> driverReports = null;
		 PageHelper.startPage(pageNum, pageSize);
		 Environment env = Environment.getEnv();
		 if(map == null ) {
			 map = new HashMap<>();
		 }
		 map.put("driverId", env.getUser().getUserId());
		 try {
			 driverReports = new PageInfo<DriverReport>(driverreportmapper.selectList(map));
		} catch (Exception e) {
        if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
     }
	        return driverReports;
	}

	@Override
	public Integer getParticipationDay(DriverSettlement driverSettlement) {
		 log.debug("getParticipationDay:{}", driverSettlement);
		 StringBuilder s;
		 List<DriverReport> driverReports;
		 Integer participationDay = 0;
		 try {
			 
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员ID", driverSettlement.getDriverId(), 20));
	   		 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
	   		driverReports = driverreportmapper.getParticipationDay(driverSettlement.getDriverId(),driverSettlement.getSettlementDateStart()/*,driverSettlement.getSettlementDateEnd()*/);
	   		for (DriverReport driverReport : driverReports) {
	   			if(driverReport.getEndDate().getTime() <= driverSettlement.getSettlementDateEnd().getTime()){
					participationDay += DateUtil.differentDays(driverReport.getStartDate(),driverReport.getEndDate()) + 1;
				}else if(driverReport.getEndDate().getTime() > driverSettlement.getSettlementDateEnd().getTime()){
					participationDay += DateUtil.differentDays(driverReport.getStartDate(),driverSettlement.getSettlementDateEnd()) + 1;
				}
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
				}else {
					throw new RuntimeException("操作失败！", e);
				}
	     }
		return participationDay;
	}

}
