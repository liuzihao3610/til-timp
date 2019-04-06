package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.DriverStatus;
import com.tilchina.timp.model.TransporterStatus;
import com.tilchina.timp.service.TransporterStatusService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterStatusMapper;

/**
* 轿运车状态表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransporterStatusServiceImpl extends BaseServiceImpl<TransporterStatus> implements TransporterStatusService {

	@Autowired
    private TransporterStatusMapper transporterstatusmapper;
	
	@Override
	protected BaseMapper<TransporterStatus> getMapper() {
		return transporterstatusmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransporterStatus transporterstatus) {
		StringBuilder s = new StringBuilder();
		transporterstatus.setCreateDate(new Date());
        s.append(CheckUtils.checkInteger("NO", "transporterStatus", "当前状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库", transporterstatus.getTransporterStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "startDate", "开始时间", transporterstatus.getStartDate()));
        s.append(CheckUtils.checkDate("YES", "endDate", "结束时间", transporterstatus.getEndDate()));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transporterstatus.getRemark(), 100));
        s.append(CheckUtils.checkString("YES", "location", "位置", transporterstatus.getLocation(), 100));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", transporterstatus.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transporterstatus.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransporterStatus transporterstatus) {
        StringBuilder s = checkNewRecord(transporterstatus);
        s.append(CheckUtils.checkPrimaryKey(transporterstatus.getTransporterStatusId()));
		return s;
	}
	 @Override
	    public void add(TransporterStatus record) {
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
		            throw new BusinessException("数据重复，请查证后重新保存！", e);
		        } else if(e instanceof BusinessException){
						throw e;
					}else {
						throw new RuntimeException("操作失败！", e);
					}
				
		    }
	    }
	 
	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		try {
			if(ids.length > 0) {
				transporterstatusmapper.logicDeleteByPrimaryKeyList(ids);
			}else {
				throw new BusinessException("9001","轿运车状态ID");
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
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		try {
			if( id != null) {
				transporterstatusmapper.logicDeleteByPrimaryKey(id);
			}else {
				throw new BusinessException("9001","轿运车状态ID");
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
	public void updateById(TransporterStatus record) {
        log.debug("updateTransporterStatus: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkInteger("NO", "transporterStatus", "当前状态", record.getTransporterStatus(), 20));
        	if(record.getDriverId() != null && record.getTransporterId() == null) {
        		s.append(CheckUtils.checkLong("NO", "driverId", "司机ID", record.getDriverId(), 20));
        	}else if(record.getTransporterId() != null && record.getDriverId() == null) {
           	 s.append(CheckUtils.checkLong("NO", "transporterId", "轿运车ID", record.getTransporterId(), 20));
        	}else {
        		throw new BusinessException("9001","Id");
        	}
  		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	transporterstatusmapper.updateById(record);
        } catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
    }

    @Override
    public TransporterStatus queryById(Long id) {
		 log.debug("queryById: {}",id);
    	 StringBuilder s;
    	 TransporterStatus transporterStatus = null;
    	 try {
         	s = new StringBuilder();
         	s.append(CheckUtils.checkLong("NO", "id", "轿运车ID", id, 20));
    		    if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
    		    transporterStatus = transporterstatusmapper.selectByPrimaryKey(id);
    		    if(transporterStatus == null)
    		    {
       		    	throw new BusinessException("9008","轿运车档案:轿运车ID");
       	   		}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transporterStatus;
    }

	@Override
	public List<TransporterStatus> getRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}", data);
		List<TransporterStatus> transporterStatuss = null;
		try {
			transporterStatuss = transporterstatusmapper.selectRefer(data);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return transporterStatuss;
	}

	@Override
	public TransporterStatus queryByTransporterId(Long transporterId) {
		 log.debug("queryByTransporterId: {}",transporterId);
    	 StringBuilder s;
    	 TransporterStatus transporterStatus = null;
    	 try {
         	s = new StringBuilder();
         	s.append(CheckUtils.checkLong("NO", "id", "轿运车ID", transporterId, 20));
    		    if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
    		    transporterStatus = transporterstatusmapper.selectByTransporterId(transporterId);
    		    /*if(transporterStatus == null)
    		    {
       		    	throw new BusinessException("9008","轿运车状态表档案:轿运车ID");
       	   		}*/
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transporterStatus;
    }

	@Override
	public PageInfo<TransporterStatus> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<TransporterStatus> transporterStatuss = null;
		try {
			Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			}
			if(data.get("refEndDate") != null && !"".equals(data.get("refEndDate"))){
				data.put("refEndDate",DateUtil.dateToString(DateUtil.endTime(data.get("refEndDate").toString())));
			}
			if (data.get("transporterStatus") == null){
				data.put("refEndTime",data.get("refEndDate"));
				data.put("refStartTime",data.get("refStartDate"));
				data.put("refStartDate",null);
				data.put("refEndDate",null);
			}
			transporterStatuss = new PageInfo<TransporterStatus>(transporterstatusmapper.selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
		}
		return transporterStatuss;
	}

}
