package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.LoadingReservationMapper;
import com.tilchina.timp.model.LoadingReservation;
import com.tilchina.timp.model.LoadingReservationDetail;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.model.WarehouseLoadingPlan;
import com.tilchina.timp.service.LoadingReservationDetailService;
import com.tilchina.timp.service.LoadingReservationService;
import com.tilchina.timp.service.UnitService;
import com.tilchina.timp.service.WarehouseLoadingPlanService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* 预约装车主表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class LoadingReservationServiceImpl extends BaseServiceImpl<LoadingReservation> implements LoadingReservationService {

	@Autowired
    private LoadingReservationMapper loadingreservationmapper;
	
	@Autowired
    private LoadingReservationDetailService loadingReservationDetailService;
	
	@Autowired
    private WarehouseLoadingPlanService warehouseLoadingPlanService;

	@Autowired
	private UnitService unitService;
	
	@Override
	protected BaseMapper<LoadingReservation> getMapper() {
		return loadingreservationmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(LoadingReservation loadingreservation) {
		StringBuilder s = new StringBuilder();
		loadingreservation.setReservationCode(DateUtil.dateToStringCode(new Date()));
		loadingreservation.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "reservationCode", "预约单号", loadingreservation.getReservationCode(), 20));
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", loadingreservation.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "carLoadingNumber", "装车数量", loadingreservation.getCarLoadingNumber(), 10));
        s.append(CheckUtils.checkDate("NO", "loadingTime", "预约装车时间", loadingreservation.getLoadingTime()));
        s.append(CheckUtils.checkString("YES", "loadingLocation", "装车位置", loadingreservation.getLoadingLocation(), 20));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "单据状态", loadingreservation.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "制单日期", loadingreservation.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", loadingreservation.getCheckDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", loadingreservation.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(LoadingReservation loadingreservation) {
        StringBuilder s = checkNewRecord(loadingreservation);
        s.append(CheckUtils.checkPrimaryKey(loadingreservation.getLoadingReservationId()));
		return s;
	}
	
	@Transactional
	@Override
    public void add(LoadingReservation record) {
        log.debug("add: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
        WarehouseLoadingPlan warehouseLoadingPlan = null;
        List<LoadingReservationDetail> loadingReservationDetails = null;
        try {
        	record.setCreator(env.getUser().getUserId());
        	record.setCorpId(env.getCorp().getCorpId());
/*        	record.setCreator(Long.valueOf(537));
        	record.setCorpId(Long.valueOf(1));*/
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().insertSelective(record);
            loadingReservationDetails = record.getSendUnits();
            if(loadingReservationDetails != null) {
            	for (LoadingReservationDetail loadingReservationDetail : loadingReservationDetails) {
                	//0=UNCHANGE、1=NEW、2=EDIT、3=DELETE
            		if(null !=  loadingReservationDetail.getRowStatus()) {
            			if(0 ==  loadingReservationDetail.getRowStatus().intValue()) {
        					
        				}else if(1 ==  loadingReservationDetail.getRowStatus().intValue()) {
        					loadingReservationDetail.setCarId(env.getCorp().getCorpId());
        					loadingReservationDetailService.add(loadingReservationDetail);
        				}else if(2 ==  loadingReservationDetail.getRowStatus().intValue()) {
        					loadingReservationDetailService.updateSelective(loadingReservationDetail);
        				}else if(3 ==  loadingReservationDetail.getRowStatus().intValue()) {
        					loadingReservationDetailService.logicDeleteByReservationId(loadingReservationDetail.getLoadingReservationId());
        				}
            		}
    			}
                //维护仓库装车计划表,状态为已预约
            	warehouseLoadingPlan = new WarehouseLoadingPlan();
            	warehouseLoadingPlan.setTransportOrderId(record.getTransportOrderId());
            	warehouseLoadingPlan.setBillStatus(1);
            	warehouseLoadingPlanService.updateByTransportOrderId(warehouseLoadingPlan);
            }
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
    public void updateSelective(LoadingReservation record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
        List<LoadingReservationDetail> loadingReservationDetails = null;
        try {
        	s = new StringBuilder();
        	 s.append(CheckUtils.checkLong("NO", "reservationId", "预约ID", record.getLoadingReservationId(), 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().updateByPrimaryKeySelective(record);
         /*   record = queryById(record.getLoadingReservationId());*/
            loadingReservationDetails = record.getSendUnits();
            if(loadingReservationDetails != null) {
	            for (LoadingReservationDetail loadingReservationDetail : loadingReservationDetails) {
	            	//0=UNCHANGE、1=NEW、2=EDIT、3=DELETE
	           		if(null !=  loadingReservationDetail.getRowStatus()) {
	        			if(0 ==  loadingReservationDetail.getRowStatus().intValue()) {
	    					
	    				}else if(1 ==  loadingReservationDetail.getRowStatus().intValue()) {
	    					loadingReservationDetail.setCarId(env.getCorp().getCorpId());
	    					loadingReservationDetailService.add(loadingReservationDetail);
	    				}else if(2 ==  loadingReservationDetail.getRowStatus().intValue()) {
	    					loadingReservationDetailService.updateSelective(loadingReservationDetail);
	    				}else if(3 ==  loadingReservationDetail.getRowStatus().intValue()) {
	    					loadingReservationDetailService.logicDeleteByReservationId(loadingReservationDetail.getLoadingReservationId());
	    				}
	        		}
				}
	        }
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
	            if(e instanceof BusinessException){
					throw e;
				}else {
					throw new RuntimeException("操作失败！", e);
				}
	        }
        }
    }
	
	@Transactional
	@Override
	public void updateBill(Long transportOrderId,Integer billStatus) {
        log.debug("updateBill: {}",transportOrderId);
        //仓库 0=待定 1=申请 2=确认 3=已装车4=取消计划
        //预约装车 0=未预约 1=已预约 2=已审核 3=取消计划
        StringBuilder s;
        List<LoadingReservation> loadingReservations = null;
        List<WarehouseLoadingPlan> warehouseLoadingPlans = null;
        Map<String, Object> map;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrderId, 20))
             .append(CheckUtils.checkInteger("NO", "billStatus", "单据状态", billStatus, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            map = new HashMap<>();
            map.put("transportOrderId",transportOrderId);
            loadingReservations = queryList(map);
           for (LoadingReservation loadingReservation : loadingReservations) {
        	   loadingReservation.setBillStatus(3);
        	   getMapper().updateByPrimaryKeySelective(loadingReservation);
           }
        /*   billStatus = WarehouseLoadingPlanBillStatus.getWareIndex(billStatus);*/
            warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
            for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
				warehouseLoadingPlan.setBillStatus(4);
				warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
			}
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
    public LoadingReservation queryById(Map<String, Object> map, int pageNum, int pageSize) {
    	log.debug("queryById: {}, page: {},{}", map,pageNum,pageSize);
        StringBuilder s;
        LoadingReservation loadingReservation = null;
        PageInfo<LoadingReservationDetail> pageInfo;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkString("NO", "loadingReservationId", "预约主表Id", map.get("loadingReservationId").toString(), 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            pageInfo = loadingReservationDetailService.queryList(map, pageNum, pageSize);
        	loadingReservation = getMapper().selectByPrimaryKey(Long.valueOf(map.get("loadingReservationId").toString()));
        	loadingReservation.setSendUnits(pageInfo.getList());
        	loadingReservation.setRefDetailTotal(pageInfo.getTotal());
			Unit unit = Optional.ofNullable(unitService.queryById(loadingReservation.getUnitId())).orElse(new Unit());
			loadingReservation.setRefUnitAddr(unit.getAddress());
			loadingReservation.setRefUnitName(unit.getUnitName());
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservation;
    }
    @Override
    public LoadingReservation queryById(Long id) {
        log.debug("query: {}",id);
        LoadingReservation loadingReservation = null;
        try {
        	loadingReservation = getMapper().selectByPrimaryKey(id);
        	if(loadingReservation == null) {
        		throw new BusinessException("预约装车主表不存在此条记录！");
        	}
        	loadingReservation.setSendUnits(loadingReservationDetailService.queryList(id));
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservation;
    }
    
    
    @Transactional
	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "预约主表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
   		 loadingreservationmapper.logicDeleteByPrimaryKey(id);
   		 //子表信息
   		 loadingReservationDetailService.logicDeleteById(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
    
    @Transactional
	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "预约主表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
	        	loadingreservationmapper.logicDeleteByPrimaryKeyList(ids);
	        	//子表信息
	        	loadingReservationDetailService.logicDeleteByIdList(ids);
        	}else {
        		throw new BusinessException("9001","预约主表ID");
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
    public List<LoadingReservation> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        Map<String, Object> mapDetail;
        List<LoadingReservation> loadingReservations = null;
        try {
        	mapDetail = new HashMap<>();
        	loadingReservations = new ArrayList<>();
        	loadingReservations = loadingreservationmapper.selectList(map);
        	List<Long> unitIds = new ArrayList<>();
        	for (LoadingReservation loadingReservation : loadingReservations) {
				unitIds.add(loadingReservation.getUnitId());
        		mapDetail.put("loadingReservationId", loadingReservation.getLoadingReservationId());
        		mapDetail.put("transportOrderId", loadingReservation.getTransportOrderId());
				loadingReservation.setSendUnits(loadingReservationDetailService.queryList(mapDetail));
			}
			unitIds = unitIds.stream().distinct().collect(Collectors.toList());
			List<Unit> units = unitService.queryByIds(unitIds);
			Map<Long,Unit> unitMap = new HashMap<>();
			units.forEach(unit -> unitMap.put(unit.getUnitId(),unit));
			loadingReservations.forEach(loadingReservation -> {
				Unit unit = unitMap.get(loadingReservation.getUnitId());
				if (unit != null){
					loadingReservation.setRefUnitName(unit.getUnitName());
					loadingReservation.setRefUnitAddr(unit.getAddress());
				}
			});
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservations;
    }
    
    @Override
    public PageInfo<LoadingReservation> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}", map);
        Map<String, Object> mapDetail;
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<LoadingReservation> loadingReservations = null;
        try {
        	mapDetail = new HashMap<>();
        	loadingReservations = new PageInfo<LoadingReservation>(loadingreservationmapper.selectList(map));
			List<Long> unitIds = new ArrayList<>();
        	for (LoadingReservation loadingReservation : loadingReservations.getList()) {
				unitIds.add(loadingReservation.getUnitId());
        		mapDetail.put("loadingReservationId", loadingReservation.getLoadingReservationId());
				loadingReservation.setSendUnits(loadingReservationDetailService.queryList(mapDetail));
			}
			unitIds = unitIds.stream().distinct().collect(Collectors.toList());
			List<Unit> units = unitService.queryByIds(unitIds);
			Map<Long,Unit> unitMap = new HashMap<>();
			units.forEach(unit -> unitMap.put(unit.getUnitId(),unit));
			List<LoadingReservation> list = loadingReservations.getList();
			list.forEach(loadingReservation -> {
				Unit unit = unitMap.get(loadingReservation.getUnitId());
				if (unit != null){
					loadingReservation.setRefUnitName(unit.getUnitName());
					loadingReservation.setRefUnitAddr(unit.getAddress());
				}
			});
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservations;
    }
    
    @Transactional
	@Override
	public void check(LoadingReservation updateLoading, Integer checkType) {
		log.debug("check: {},{}", updateLoading,checkType);
		Environment env =  Environment.getEnv();
		/*WarehouseLoadingPlan warehouseLoadingPlan;*/
		List<WarehouseLoadingPlan> warehouseLoadingPlans;
		LoadingReservation queryLoading;
		Map<String, Object> map;
		try {
			//0=审核 1=取消审核
			queryLoading = queryById(updateLoading.getLoadingReservationId());
			if(checkType == 0) {
				if(queryLoading.getBillStatus().intValue() == checkType.intValue()) {
					throw new BusinessException("该预约装车单号已审核通过,请勿重复审核！");
				}
				updateLoading.setBillStatus(2);
				updateLoading.setChecker(env.getUser().getUserId());
				updateLoading.setCheckDate(new Date());
				map = new HashMap<>();
				map.put("transportOrderId", queryLoading.getTransportOrderId());
				//维护仓库装车状态
				warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
				for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
					warehouseLoadingPlan.setBillStatus(2);
					warehouseLoadingPlan.setUnitId(queryLoading.getUnitId());
					warehouseLoadingPlan.setTransporterId(queryLoading.getTransporterId());
					warehouseLoadingPlan.setLoadingDate(updateLoading.getLoadingTime());
					warehouseLoadingPlan.setLoadingLocation(updateLoading.getLoadingLocation());
					warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
				}
			}else if(checkType.intValue() == 1) {
				if(queryLoading.getBillStatus().intValue() == checkType.intValue()) {
					throw new BusinessException("该预约装车单号未通过审核,不需要取消审核！");
				}
				updateLoading.setCheckDate(null);
				updateLoading.setChecker(null);
				updateLoading.setLoadingTime(null);
				updateLoading.setBillStatus(1);
				//维护仓库装车状态
				map = new HashMap<>();
				map.put("transportOrderId", queryLoading.getTransportOrderId());
				warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
				for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
					warehouseLoadingPlan.setBillStatus(1);
					warehouseLoadingPlan.setUnitId(queryLoading.getUnitId());
					warehouseLoadingPlan.setTransporterId(queryLoading.getTransporterId());
					warehouseLoadingPlan.setLoadingDate(queryLoading.getLoadingTime());
					warehouseLoadingPlan.setLoadingLocation(queryLoading.getLoadingLocation());
					warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
				}
			}
			loadingreservationmapper.updateCheck(updateLoading);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}

	@Override
	public void logicDeleteByTransportOrderId(Long id) {
		log.debug("logicDeleteByTransportOrderId: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "预约主表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
   		 loadingreservationmapper.logicDeleteByTransportOrderId(id);
   		 //子表信息
   		 loadingReservationDetailService.logicDeleteByTransportOrderId(id);
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
		    		s.append(CheckUtils.checkInteger("NO", "data", "预约主表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
	        	loadingreservationmapper.logicDeleteByTransportOrderIds(ids);
	        	//子表信息
	        	loadingReservationDetailService.logicDeleteByTransportOrderIds(ids);
        	}else {
        		throw new BusinessException("9001","预约主表ID");
        	}
        	
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}

}
