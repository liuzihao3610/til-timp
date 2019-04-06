package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportPlanMapper;
import com.tilchina.timp.model.TransportPlan;
import com.tilchina.timp.service.TransportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
* 运输计划表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransportPlanServiceImpl extends BaseServiceImpl<TransportPlan> implements TransportPlanService {

	@Autowired
    private TransportPlanMapper transportplanmapper;
	
	@Override
	protected BaseMapper<TransportPlan> getMapper() {
		return transportplanmapper;
	}


	@Override
	protected StringBuilder checkNewRecord(TransportPlan transportplan) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", transportplan.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "handingCount", "装载数量", transportplan.getHandingCount(), 10));
        s.append(CheckUtils.checkInteger("NO", "carCount", "装卸数量", transportplan.getCarCount(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportPlan transportplan) {
        StringBuilder s = checkNewRecord(transportplan);
        s.append(CheckUtils.checkPrimaryKey(transportplan.getTransportPlanId()));
		return s;
	}
	
    @Override
    public void add(TransportPlan record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }
    
	@Override
    public TransportPlan queryById(Long id) {
        log.debug("query: {}",id);
        TransportPlan transportPlan = null;
        try {
        	transportPlan = getMapper().selectByPrimaryKey(id);
        	if(transportPlan == null){
        		throw new BusinessException("9008","运输计划ID");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportPlan;
    }
	
	
	@Override
	public List<TransportPlan> queryByOrderId(Long id) {
        log.debug("query: {}",id);
		List<TransportPlan> transportPlans = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			transportPlans = transportplanmapper.selectByOrderKey(id);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportPlans;
    }

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运输计划ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
   		    transportplanmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void logicDeleteByIdList(Integer[] dels) {
		log.debug("logicDeleteById: {}",dels);
		int[] ints;
		StringBuilder s;
        try {
        	if(dels.length > 0) {
        		s = new StringBuilder();
	        	for (int id : dels) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "运输计划ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
	        	ints = Arrays.stream(dels).mapToInt(Integer::valueOf).toArray();
	        	transportplanmapper.logicDeleteByPrimaryKeyList(ints);
        	}else {
        		throw new BusinessException("9010","运输计划表");
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
    public PageInfo<TransportPlan> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
        	 if(map == null) {
             	map = new HashMap<>();
             }
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return new PageInfo<TransportPlan>(getMapper().selectList(map));
    }
    
    @Override
    public List<TransportPlan> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        try {
       	 if(map == null) {
            	map = new HashMap<>();
            }
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return getMapper().selectList(map);
    }


	@Override
	public List<TransportPlan> querySequenceList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        try {
       	 if(map == null) {
            	map = new HashMap<>();
            }
       	 map.put("orderByClause", "SEQUENCE");
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return  transportplanmapper.selectSequenceList(map);
    }
    
}
