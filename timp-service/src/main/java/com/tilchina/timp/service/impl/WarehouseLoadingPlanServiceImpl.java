package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.WarehouseLoadingPlan;
import com.tilchina.timp.service.WarehouseLoadingPlanService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.WarehouseLoadingPlanMapper;

/**
* 仓库装车计划表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class WarehouseLoadingPlanServiceImpl extends BaseServiceImpl<WarehouseLoadingPlan> implements WarehouseLoadingPlanService {

	@Autowired
    private WarehouseLoadingPlanMapper warehouseloadingplanmapper;
	
	@Override
	protected BaseMapper<WarehouseLoadingPlan> getMapper() {
		return warehouseloadingplanmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(WarehouseLoadingPlan warehouseloadingplan) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkDate("NO", "planDate", "计划日期", warehouseloadingplan.getPlanDate()));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", warehouseloadingplan.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "状态", warehouseloadingplan.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "loadingDate", "预约装车时间", warehouseloadingplan.getLoadingDate()));
        s.append(CheckUtils.checkString("YES", "loadingLocation", "装车点", warehouseloadingplan.getLoadingLocation(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "备注", warehouseloadingplan.getRemark(), 200));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(WarehouseLoadingPlan warehouseloadingplan) {
        StringBuilder s = checkNewRecord(warehouseloadingplan);
        s.append(CheckUtils.checkPrimaryKey(warehouseloadingplan.getWarehouseLoadingPlanId()));
		return s;
	}
	
	@Override
    public void add(WarehouseLoadingPlan record) {
        log.debug("add: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            record.setCorpId(env.getCorp().getCorpId());
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
	public void updateByTransportOrderId(WarehouseLoadingPlan record) {
		log.debug("updateByTransportOrderId: {}",record);
		StringBuilder s;
		try {
			s = new StringBuilder();
	        s.append(CheckUtils.checkLong("NO", "TransportOrderId", "运单主表id", record.getTransportOrderId(), 20))
	         .append(CheckUtils.checkInteger("NO", "BillStatus", "状态", record.getBillStatus(), 20));
	        if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
	        warehouseloadingplanmapper.updateStatusByTransportOrderId(record);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("".toString());
	}
	 @Override
    public void updateSelective(WarehouseLoadingPlan warehouseLoadingPlan) {
        log.debug("updateSelective: {}",warehouseLoadingPlan);
        try {
            getMapper().updateByPrimaryKeySelective(warehouseLoadingPlan);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            }if(e instanceof BusinessException){
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
        	s.append(CheckUtils.checkLong("NO", "data", "仓库装车计划ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 warehouseloadingplanmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
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
		    		s.append(CheckUtils.checkInteger("NO", "data", "仓库装车计划ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	warehouseloadingplanmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","仓库装车计划ID");
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
	public void updateCarVin(String carVin,Long transportOrderId,Integer billStatus) {
		log.debug("updateCarVin: {}",carVin);
		StringBuilder s;
		WarehouseLoadingPlan warehouseLoadingPlan = null;
        try {
    		s = new StringBuilder();
    		s.append(CheckUtils.checkString("NO", "carVin", "仓库装车计划:商品车车架号", carVin, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    warehouseLoadingPlan = queryByCarVin(carVin,transportOrderId);
   		    warehouseLoadingPlan.setBillStatus(billStatus);
        	warehouseloadingplanmapper.updateByCarVinSelective(warehouseLoadingPlan);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public WarehouseLoadingPlan queryByCarVin(String carVin,Long transportOrderId) {
		log.debug("queryById: {}",carVin);
		StringBuilder s;
		WarehouseLoadingPlan warehouseLoadingPlan = null;
        try {
    		s = new StringBuilder();
    		s.append(CheckUtils.checkString("NO", "carVin", "仓库装车计划:商品车车架号", carVin, 20))
    		.append(CheckUtils.checkLong("NO", "transportOrderId", "仓库装车计划:运单主表id", transportOrderId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 warehouseLoadingPlan = warehouseloadingplanmapper.selectByCarVin(carVin,transportOrderId);
   		 if(warehouseLoadingPlan == null) {
   			throw new  BusinessException("仓库装车计划:商品车"+carVin+"车架号不存在");
   		 }
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return warehouseLoadingPlan;
	}
	
    @Override
    public WarehouseLoadingPlan queryById(Long id) {
    	log.debug("queryById: {}",id);
		StringBuilder s;
		WarehouseLoadingPlan warehouseLoadingPlan = null;
        try {
    		s = new StringBuilder();
    		s.append(CheckUtils.checkLong("NO", "data", "仓库装车计划:仓库装车计划id", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 warehouseLoadingPlan = warehouseloadingplanmapper.selectByPrimaryKey(id);
   		 if(warehouseLoadingPlan == null) {
   			throw new  BusinessException("仓库装车计划:该记录不存在！");
   		 }
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return warehouseLoadingPlan;
    }

	@Override
	public void logicDeleteByTransportOrderId(Long id) {
		log.debug("logicDeleteByTransportOrderId: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "仓库装车计划ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 warehouseloadingplanmapper.logicDeleteByTransportOrderId(id);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void logicDeleteByTransportOrderId(int[] ids) {
		log.debug("logicDeleteByTransportOrderId: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "仓库装车计划ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	warehouseloadingplanmapper.logicDeleteByTransportOrderIds(ids);
        	}else {
        		throw new BusinessException("9001","仓库装车计划ID");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

/*	@Override
	public void updateStatus(WarehouseLoadingPlan record) {
        log.debug("updateStatus: {}",record);
        WarehouseLoadingPlan warehouseLoadingPlan;
        try {
        	warehouseLoadingPlan = new WarehouseLoadingPlan();
        	warehouseLoadingPlan.setWarehouseLoadingPlanId(record.getWarehouseLoadingPlanId());
        	warehouseLoadingPlan.setBillStatus(record.getBillStatus());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }*/
    
}
