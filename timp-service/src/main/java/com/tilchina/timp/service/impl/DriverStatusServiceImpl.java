package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.Dept;
import com.tilchina.timp.model.DriverStatus;
import com.tilchina.timp.service.DriverStatusService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DriverStatusMapper;

/**
* 驾驶员提报状态
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class DriverStatusServiceImpl extends BaseServiceImpl<DriverStatus> implements DriverStatusService {

	@Autowired
    private DriverStatusMapper driverstatusmapper;
	
	@Override
	protected BaseMapper<DriverStatus> getMapper() {
		return driverstatusmapper;
	}
	
	@Override
	protected StringBuilder checkNewRecord(DriverStatus driverstatus) {
		StringBuilder s = new StringBuilder();
		driverstatus.setCreateDate(new Date());
        s.append(CheckUtils.checkInteger("NO", "driverStatus", "当前状态:0=空闲,1=在途,2=到店,3=回程,4=请假,5=培训", driverstatus.getDriverStatus(), 10));
        s.append(CheckUtils.checkString("YES", "location", "位置", driverstatus.getLocation(), 100));
        s.append(CheckUtils.checkDate("YES", "startDate", "开始时间", driverstatus.getStartDate()));
        s.append(CheckUtils.checkDate("YES", "endDate", "结束时间", driverstatus.getEndDate()));
        s.append(CheckUtils.checkString("YES", "remark", "备注", driverstatus.getRemark(), 200));
        s.append(CheckUtils.checkDate("YES", "createDate", "提报时间", driverstatus.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", driverstatus.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DriverStatus driverstatus) {
        StringBuilder s = checkNewRecord(driverstatus);
        s.append(CheckUtils.checkPrimaryKey(driverstatus.getDriverStatusId()));
		return s;
	}
	
	@Override
    public void add(DriverStatus record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            }else if(e instanceof BusinessException){
				throw e;
			} else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }
	  
	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
		try {
			if(ids.length > 0) {
			s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "驾驶员状态ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
	
	@Override
    public void updateSelective(DriverStatus record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "司机ID", record.getDriverStatusId(), 20));
        	if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	queryById(record.getDriverStatusId());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            }else if(e instanceof BusinessException){
				throw e;
			} else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		   StringBuilder s;
	        try {
	        	s = new StringBuilder();
	        	s.append(CheckUtils.checkLong("NO", "data", "驾驶员状态ID",id, 20));
	        	if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	        	queryById(id);
				driverstatusmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
	
	@Override
    public DriverStatus queryById(Long id) {
        log.debug("query: {}",id);
        DriverStatus driverStatus = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "驾驶员状态ID",id, 20));
        	if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	driverStatus = getMapper().selectByPrimaryKey(id); 
            if(driverStatus == null) {
            	throw new BusinessException("9008","驾驶员状态ID");
            }
		} catch (Exception e) {
           if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return driverStatus;
    }
	
	@Override
	public DriverStatus queryByDirverId(Long driverId) {
		 log.debug("queryByDirverId: {}",driverId);
    	 StringBuilder s;
    	 DriverStatus driverStatus = null;
    	 try {
         	s = new StringBuilder();
         	s.append(CheckUtils.checkLong("NO", "data", "司机ID", driverId, 20));
    		if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
    		 driverStatus = driverstatusmapper.selectByDriverId(driverId);
		} catch (Exception e) {
           if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return driverStatus;
    }

	@Override
	public void updateDriverStatus(DriverStatus record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkInteger("NO", "driverStatus", "当前状态", record.getDriverStatus(), 20))
        	 .append(CheckUtils.checkLong("NO", "driverId", "司机ID", record.getDriverId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	driverstatusmapper.updateByDriverId(record);
        } catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
    }

	@Override
	public PageInfo<DriverStatus> getRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<DriverStatus> driverStatuss = null;
		try {
			if(data == null) {
				data = new HashMap<String, Object>();
			}
			if(data.get("driverStatus") == null) {
				data.put("driverStatus", "0");
			}else if(data.get("driverStatus") != null){
				data.put("driverStatus", data.get("driverStatus").toString());
			}
			driverStatuss = new  PageInfo<DriverStatus> (driverstatusmapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return driverStatuss;
	}

	@Override
	public PageInfo<DriverStatus> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {},sd page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<DriverStatus> driverStatus = null;
		try {
			Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			}
			if(data.get("refEndDate") != null && !"".equals(data.get("refEndDate"))){
				data.put("refEndDate",DateUtil.dateToString(DateUtil.endTime(data.get("refEndDate").toString())));
			}
			if (data.get("driverStatus") == null ){
				data.put("refEndTime",data.get("refEndDate"));
				data.put("refStartTime",data.get("refStartDate"));
				data.put("refStartDate",null);
				data.put("refEndDate",null);
			}
			driverStatus = new PageInfo<DriverStatus>(driverstatusmapper.selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
		}
		return driverStatus;
	}


	@Override
	public DriverStatus queryByDirverIdAndTransportId(Long driverId, Long transportId) {
		 log.debug("queryByDirverIdAndTransportId: {}",driverId);
    	 StringBuilder s;
    	 DriverStatus driverStatus = null;
    	 try {
         	s = new StringBuilder();
         	s.append(CheckUtils.checkLong("NO", "data", "司机ID", driverId, 20));
         	s.append(CheckUtils.checkLong("NO", "data", "轿运车ID", transportId, 20));
    		if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
    		 driverStatus = driverstatusmapper.selectByDriverIdAndTransportId(driverId,transportId);
		} catch (Exception e) {
           if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return driverStatus;
    }
	
}
