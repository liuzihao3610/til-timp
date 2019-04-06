package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.model.TransporterReport;
import com.tilchina.timp.model.TransporterStatus;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.DriverService;
import com.tilchina.timp.service.TransporterReportService;
import com.tilchina.timp.service.TransporterStatusService;
import com.tilchina.timp.utils.CheckReportStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterReportMapper;

/**
* 骄运车状态变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransporterReportServiceImpl  implements TransporterReportService {

	@Autowired
    private TransporterReportMapper transporterreportmapper;
	
	@Autowired
	private TransporterStatusService transporterstatusservice;
	
	@Autowired
	private DriverService driverService;

	protected StringBuilder checkNewRecord(TransporterReport transporterreport) {
		StringBuilder s = new StringBuilder();
		transporterreport.setCreateDate(new Date());
        s.append(CheckUtils.checkInteger("NO", "transporterStatus", "当前状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库", transporterreport.getTransporterStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "startDate", "开始时间", transporterreport.getStartDate()));
        s.append(CheckUtils.checkDate("YES", "endDate", "结束时间", transporterreport.getEndDate()));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transporterreport.getRemark(), 100));
        s.append(CheckUtils.checkString("YES", "location", "位置", transporterreport.getLocation(), 100));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", transporterreport.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transporterreport.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(TransporterReport transporterreport) {
        StringBuilder s = checkNewRecord(transporterreport);
        s.append(CheckUtils.checkPrimaryKey(transporterreport.getTransporterReportId()));
		return s;
	}
	
	@Transactional
	@Override
	public void report(TransporterReport updateTransporterReport) {
		 log.debug("report: {}",updateTransporterReport);
		 Environment env = Environment.getEnv();
		 TransporterStatus transporterStatus = null;
		 User driver = null;
		 StringBuilder s;
		  try {
			    s = new StringBuilder();
		        driver = driverService.queryById(env.getUser().getUserId());
		        updateTransporterReport.setCreator(env.getUser().getUserId());
		        updateTransporterReport.setCorpId(driver.getCorpId());
		        updateTransporterReport.setFlag(0);
		        updateTransporterReport.setDriverId(env.getUser().getUserId());
		        s = checkNewRecord(updateTransporterReport);
		        if (!StringUtils.isBlank(s)) {
		             throw new BusinessException("数据检查失败：" + s.toString());
		        }
		        if(updateTransporterReport.getTransporterStatus() > 6) {
		        	throw new BusinessException("请输入有效的轿运车状态！");
		        }
			transporterStatus = transporterstatusservice.queryByTransporterId(updateTransporterReport.getTransporterId());
			//骄运车状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库
        	if(transporterStatus != null) {
/*        		CheckReportStatus.checkTransporterReportStatuc(updateTransporterReport.getTransporterStatus(), transporterStatus.getTransporterStatus(),0);
*/        		transporterStatus.setLocation(updateTransporterReport.getLocation());
				transporterStatus.setLat(updateTransporterReport.getLat());
				transporterStatus.setLng(updateTransporterReport.getLng());
        		transporterStatus.setTransporterStatus(updateTransporterReport.getTransporterStatus());
                transporterStatus.setStartDate(updateTransporterReport.getStartDate());
                transporterStatus.setEndDate(updateTransporterReport.getEndDate());
                transporterStatus.setRemark(updateTransporterReport.getRemark());
            	transporterStatus.setDriverId(env.getUser().getUserId());
        		transporterstatusservice.updateSelective(transporterStatus);
        	}else {
            	transporterStatus = new TransporterStatus();
            	transporterStatus.setLocation(updateTransporterReport.getLocation());
				transporterStatus.setLat(updateTransporterReport.getLat());
				transporterStatus.setLng(updateTransporterReport.getLng());
            	transporterStatus.setCreator(env.getUser().getUserId());
            	transporterStatus.setDriverId(env.getUser().getUserId());
            	transporterStatus.setCorpId(updateTransporterReport.getCorpId());
            	transporterStatus.setTransporterId(updateTransporterReport.getTransporterId());
            	transporterStatus.setTransporterStatus(updateTransporterReport.getTransporterStatus());
                transporterStatus.setStartDate(updateTransporterReport.getStartDate());
                transporterStatus.setEndDate(updateTransporterReport.getEndDate());
            	transporterStatus.setRemark(updateTransporterReport.getRemark());
            	transporterStatus.setCreateDate(new Date());
            	transporterStatus.setFlag(0);
            /*	CheckReportStatus.checkTransporterReportStatuc(updateTransporterReport.getTransporterStatus(), transporterStatus.getTransporterStatus(),1);
            */	transporterstatusservice.add(transporterStatus);
        	}
        	transporterreportmapper.insertSelective(updateTransporterReport);
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
	public PageInfo<TransporterReport> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		 log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		 PageInfo<TransporterReport> transporterReports = null;
		 PageHelper.startPage(pageNum, pageSize);
		 try {
			 Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			 if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				 data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			 }
			 if(data.get("refEndDate") != null && !"".equals(data.get("refEndDate"))){
				 data.put("refEndDate",DateUtil.dateToString(DateUtil.endTime(data.get("refEndDate").toString())));
			 }
			 if (data.get("transporterStatus") == null || "".equals(data.get("transporterStatus") )){
				 data.put("refEndTime",data.get("refEndDate"));
				 data.put("refStartTime",data.get("refStartDate"));
				 data.put("refStartDate",null);
				 data.put("refEndDate",null);
			 }
			 transporterReports = new PageInfo<TransporterReport>(transporterreportmapper.selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
       }
	        return transporterReports;
	}
    
}
