package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterAndDriverMapper;
import com.tilchina.timp.model.TransporterAndDriver;
import com.tilchina.timp.service.TransporterAndDriverService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* 轿运车司机关系
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class TransporterAndDriverServiceImpl extends BaseServiceImpl<TransporterAndDriver> implements TransporterAndDriverService {

	@Autowired
    private TransporterAndDriverMapper transporteranddrivermapper;
	
	@Override
	protected BaseMapper<TransporterAndDriver> getMapper() {
		return transporteranddrivermapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransporterAndDriver transporteranddriver) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", transporteranddriver.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transporteranddriver.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransporterAndDriver transporteranddriver) {
        StringBuilder s = checkNewRecord(transporteranddriver);
        s.append(CheckUtils.checkPrimaryKey(transporteranddriver.getTransporterAndDriverId()));
		return s;
	}
	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "轿运车司机关系ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	transporteranddrivermapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","轿运车司机关系ID");
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
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "轿运车司机关系ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			transporteranddrivermapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}
	
	@Override
    public TransporterAndDriver queryById(Long id) {
        log.debug("query: {}",id);
        TransporterAndDriver transporterAndDriver = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "轿运车司机关系ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 transporterAndDriver = getMapper().selectByPrimaryKey(id); 
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return transporterAndDriver;
    }

	@Override
	public TransporterAndDriver queryByKeyId(Map<String, Long> map) {
		 log.debug("queryByKeyId: {}",map);
        TransporterAndDriver transporterAndDriver = null;
        StringBuilder s;
        try {
        	if(map.isEmpty()) {
        		throw new BusinessException("9001","查询条件");
        	}else {
        		s = new StringBuilder();
        		if(map.get("transporterId") != null &&  map.get("driverId") == null) {
                	s.append(CheckUtils.checkLong("NO", "transporterId", "轿运车ID", map.get("transporterId"), 20));
        		}else if(map.get("driverId") != null &&  map.get("transporterId") == null) {
        			s.append(CheckUtils.checkLong("NO", "driverId", "司机ID", map.get("driverId"), 20));
        		}else if(map.get("driverId") != null &&  map.get("transporterId") != null) {
        			s.append(CheckUtils.checkLong("NO", "driverId", "司机ID", map.get("driverId"), 20));
        			s.append(CheckUtils.checkLong("NO", "transporterId", "轿运车ID", map.get("transporterId"), 20));
        		}else {
        			throw new BusinessException("9003");
        		}
       		    if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
       		    transporterAndDriver = transporteranddrivermapper.selectByKeyId(map);
        	}
   		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transporterAndDriver;
	}

	@Override
	public List<Map<String, Object>> queryIdleTransporter(){
		return transporteranddrivermapper.selectIdleTransporter();
	}

	@Override
	public PageInfo<TransporterAndDriver> getRefer(Map<String, Object> data, int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TransporterAndDriver>  transporterAndDrivers = null;
		try {
			transporterAndDrivers = new PageInfo<TransporterAndDriver> (transporteranddrivermapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return transporterAndDrivers;
	}

	@Override
	public void setInvalidStatusByTransporterId(Long transporterId) {
		try {
			transporteranddrivermapper.setInvalidStatusByTransporterId(transporterId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}


	@Override
	public void updateSelective(TransporterAndDriver record) {
		try {
			setInvalidStatusById(record.getTransporterAndDriverId());
			this.getMapper().updateByPrimaryKeySelective(record);
		} catch (Exception var3) {
			if (var3.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", var3);
			} else {
				throw var3;
			}
		}
	}

	@Override
	public void setInvalidStatusById(Long transporterAndDriverId) {
		try {
			transporteranddrivermapper.setInvalidStatusById(transporterAndDriverId);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public PageInfo<TransporterAndDriver> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<TransporterAndDriver> transporterAndDrivers = null;
		try {
			Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			}
			transporterAndDrivers = new PageInfo<>(getMapper().selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
		}
		return transporterAndDrivers;
	}

	@Override
	public void add(TransporterAndDriver record) {
		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			} else {
				setInvalidStatusByTransporterId(record.getTransporterId());
				this.getMapper().insertSelective(record);
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
