package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.StockCarMapper;
import com.tilchina.timp.mapper.TransportOrderDetailMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
* 运单子表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransportOrderDetailServiceImpl extends BaseServiceImpl<TransportOrderDetail> implements TransportOrderDetailService {

	@Autowired
    private TransportOrderDetailMapper transportorderdetailmapper;
	
	@Autowired
    private TransportOrderService transportOrderService;
	
	@Autowired
    private TransportSettlementService transportSettlementService;
	
	@Autowired
	private ContactsService contactsService;
	
	@Autowired
    private DriverService  driverService;
	
	@Autowired
    private StockCarService stockCarService;
	
	@Autowired
    private StockCarTransService stockCarTransService;

	@Autowired
	private WarehouseLoadingPlanService warehouseLoadingPlanService;

	@Autowired
	private StockCarMapper stockcarmapper;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private LoadingReservationService loadingReservationService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private DriverSettlementService driverSettlementService;

	@Autowired
	private FreightService freightService;

	@Override
	protected BaseMapper<TransportOrderDetail> getMapper() {
		return transportorderdetailmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransportOrderDetail transportorderdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", transportorderdetail.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", transportorderdetail.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("NO", "examination", "车检标志", transportorderdetail.getExamination(), 10));
        s.append(CheckUtils.checkDate("NO", "claimLoadingDate", "指定装车日期", transportorderdetail.getClaimLoadingDate()));
        s.append(CheckUtils.checkDate("YES", "realityLoadingDate", "实际装车日期", transportorderdetail.getRealityLoadingDate()));
        s.append(CheckUtils.checkDate("NO", "claimDeliveryDate", "指定交车日期", transportorderdetail.getClaimDeliveryDate()));
        s.append(CheckUtils.checkDate("YES", "realityDeliveryDate", "指定交车日期", transportorderdetail.getRealityDeliveryDate()));
        s.append(CheckUtils.checkDate("YES", "arriveDateBesides", "到店时间（外）", transportorderdetail.getArriveDateBesides()));
        s.append(CheckUtils.checkDate("YES", "signDate", "签收时间", transportorderdetail.getSignDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transportorderdetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportOrderDetail transportorderdetail) {
        StringBuilder s = checkNewRecord(transportorderdetail);
        s.append(CheckUtils.checkPrimaryKey(transportorderdetail.getTransportOrderDetailId()));
		return s;
	}		
	
	@Transactional
    @Override
    public void adds(List<TransportOrderDetail> details,TransportOrder transportOrder) {
        log.debug("adds: {}",details);
        try {
        	//	维护发货单位
            Map<Long, TransportOrderDetail> detailMap = new HashMap<>();
            details.stream().forEach(detail -> {
            	detailMap.put(detail.getSendUnitId(), detail);
            });
            if(detailMap.size() != 1) {
            	 throw new BusinessException("请统一发货单位！");
            } 
        	if(details != null) {
	        	for (TransportOrderDetail detail : details) {
	            	//	0=UNCHANGE、1=NEW、2=EDIT、3=DELETE
		        	if(detail.getRowStatus() != null) {
						if(0 ==  detail.getRowStatus().intValue()) {
							
						}else if(1 ==  detail.getRowStatus().intValue()) {
							add(detail,transportOrder);
						}else if(2 ==  detail.getRowStatus().intValue()) {
							detail.setTransportOrderId(transportOrder.getTransportOrderId());
							detail.setTransportOrderCode(transportOrder.getTransportOrderCode());
							detail.setDriverId(transportOrder.getDriverId());
							transportorderdetailmapper.updateByPrimaryKeySelective(detail);
						}else if(3 ==  detail.getRowStatus().intValue()) {
							detail.setTransportOrderId(transportOrder.getTransportOrderId());
							detail.setTransportOrderCode(transportOrder.getTransportOrderCode());
							detail.setDriverId(transportOrder.getDriverId());
							transportorderdetailmapper.logicDeleteByPrimaryKey(detail);
						}
					}
	        	}
	        }
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
    }

    @Override
    public PageInfo<TransportOrderDetail> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TransportOrderDetail> pageInfo = null;
        try {
        	pageInfo = new PageInfo<TransportOrderDetail>(getMapper().selectList(map));
        	for (TransportOrderDetail transportOrderDetail : pageInfo.getList()) {
        		transportOrderDetail.setRowStatus(0);
    			transportOrderDetail.setContacts(contactsService.appQueryByUnitId(transportOrderDetail.getSendUnitId(),transportOrderDetail.getReceiveUnitId()));
			}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
    		}else {
				throw new RuntimeException("操作失败！", e);
    		}
        }
        return pageInfo;
    }
    
    @Override
    public List<TransportOrderDetail> queryList(Map<String, Object> map) {
        log.debug("queryList: {}, page: {},{}", map);
        List<TransportOrderDetail> transportOrderDetails = null;
        try {
        	transportOrderDetails = getMapper().selectList(map);
        	for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
        		if(transportOrderDetail.getSendUnitId() != null && transportOrderDetail.getSendUnitId() != null) {
        			transportOrderDetail.setRowStatus(0);
        			transportOrderDetail.setContacts(contactsService.appQueryByUnitId(transportOrderDetail.getSendUnitId(),transportOrderDetail.getReceiveUnitId()));
        		}
			}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
    		}else {
				throw new RuntimeException("操作失败！", e);
    		}
        }
        return transportOrderDetails;
    }
	
	@Transactional
    @Override
    public void add(TransportOrderDetail transportOrderDetail) {
        log.debug("add: {}",transportOrderDetail);
     /*   Environment env = Environment.getEnv();*/
        StringBuilder s;
        try {
             /*transportOrderDetail.setCorpId(env.getCorp().getCorpId());*/
        	transportOrderDetail.setFlag(0);
            s = checkNewRecord(transportOrderDetail);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().insertSelective(transportOrderDetail);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
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
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "运单子表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	transportorderdetailmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","运单子表ID");
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
	public void logicDeleteById(TransportOrderDetail transportOrderDetail) {
		log.debug("logicDeleteById: {}",transportOrderDetail);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "transportOrderDetailId", "运单子ID", transportOrderDetail.getTransportOrderDetailId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(transportOrderDetail.getTransportOrderDetailId());
   		    transportorderdetailmapper.logicDeleteByPrimaryKey(transportOrderDetail);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Override
	public void logicDeleteByTransportOrderId(TransportOrderDetail transportOrderDetail) {
		log.debug("logicDeleteById: {}",transportOrderDetail);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单ID", transportOrderDetail.getTransportOrderId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    //	维护车辆状态：入库
   		   /* storage(transportOrderDetail);*/
   		    transportorderdetailmapper.logicDeleteByPrimaryKey(transportOrderDetail);
/*   		    //	维护运单结算信息的基础运价、总运价、毛利率
   		    transportSettlementService.updateSelective(transportOrderDetail.getTransportOrderId());*/
   		    
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Override
	public void logicDeleteByTransportOrderIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "运单主表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		    transportOrderService.queryById(Long.valueOf(id));
	    		}
	        	transportorderdetailmapper.logicDeleteByTransportOrderKeyList(ids);
        	}else {
        		throw new BusinessException("9001","运单主表ID");
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
    public TransportOrderDetail queryById(Long id) {
        log.debug("query: {}",id);
        TransportOrderDetail transportOrderDetail = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单子表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	transportOrderDetail = getMapper().selectByPrimaryKey(id);
        	if(transportOrderDetail == null) {
        		throw new BusinessException("9008","运单子表ID");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportOrderDetail;
    }
	
	@Override
    public List<TransportOrderDetail> queryByOrderId(Map<String, Object> map) {
        log.debug("query: {}",map);
        List<TransportOrderDetail> transportOrderDetails = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单主表ID", Long.parseLong((map.get("transportOrderId").toString())), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    transportOrderDetails = transportorderdetailmapper.selectByOrderKey(map);
   		    
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportOrderDetails;
    }
	
	/**
	 * 维护运单主表的车检标志
	 * @param transportOrderId
	 */
	 public void maintainExamination(Long transportOrderId) {
        TransportOrder transportOrder = null;
        boolean flag = false;
        transportOrder = transportOrderService.queryById(transportOrderId);
	   	for (TransportOrderDetail transportOrderDetail : transportOrder.getDetails()) {
	   		 if(transportOrderDetail.getExamination() != null) {
	   			if(transportOrderDetail.getExamination().intValue() == 1) {
		   			flag = true;
	   			}	
	   		}
		}
	   	if(flag) {
	   		transportOrder.setExamination(1);
				transportOrderService.updateExamination(transportOrder);
	   	}else {
	   		transportOrder.setExamination(0);
				transportOrderService.updateExamination(transportOrder);
	   	}
    }
	 
	@Override
    public void updateSelective(TransportOrderDetail transportOrderDetail) {
        log.debug("updateSelective: {}",transportOrderDetail);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "transportOrderDetailId", "运单子表ID", transportOrderDetail.getTransportOrderDetailId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(transportOrderDetail.getTransportOrderDetailId());
   		    maintainExamination(transportOrderDetail.getTransportOrderId());
            getMapper().updateByPrimaryKeySelective(transportOrderDetail);
   		    //	维护运单结算信息的基础运价、总运价、毛利率
   		    transportSettlementService.updateSelective(transportOrderDetail.getTransportOrderId());
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
	public List<TransportOrderDetail> queryByDriverId(Long driverId) {
		List<TransportOrderDetail> transportOrderDetails = null;
		StringBuilder s;
		try {
			s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "transportOrderDetailId", "运单子表司机ID", driverId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			transportOrderDetails = transportorderdetailmapper.queryByDriverId(driverId);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		
		return transportOrderDetails;
	}

	@Override
	public List<Loading> queryLoadingCount(Map<String, Object> map,int loadingType) {
		log.debug("queryLoadingCount: {},{}", map,loadingType);
		List<Loading> loadings = null;
		try {
			if(map.get("transportOrderId") == null) {
				throw new BusinessException("请输入有效的运单主表id！");
			}
			if(loadingType == 0) {
				loadings = transportorderdetailmapper.selectStartUnitCount(Long.valueOf(map.get("transportOrderId").toString()));
			}else if(loadingType == 1) {
				loadings = transportorderdetailmapper.selectEndUnitCount(Long.valueOf(map.get("transportOrderId").toString()));
			}else if(loadingType == 2) {
				loadings = transportorderdetailmapper.selectLoadingCount(map);
			}
			else {
				throw new BusinessException("请输入有效的loadingType！");
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return loadings;
	}

	@Override
	public List<TransportOrderDetail> appQueryList(Map<String, Object> map) {
		log.debug("appQueryList: {}", map);
		List<TransportOrderDetail> transportOrderDetails = null;
		try {
			transportOrderDetails = transportorderdetailmapper.appSelectList(map);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return transportOrderDetails;
	}

	@Override
	public TransportOrderDetail queryByCarVin(String carVin,Long transportOrderId) {
		 log.debug("queryByCarVin: {},{}",carVin,transportOrderId);
		try {
			TransportOrderDetail transportOrderDetail = transportorderdetailmapper.queryByCarVin(carVin,transportOrderId);
			return transportOrderDetail;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<TransportOrderDetail> selectByCarVin(String carVin) {

		try {
			List<TransportOrderDetail> orderDetails = transportorderdetailmapper.selectByCarVin(carVin);
			return orderDetails;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void updateSelective(List<TransportOrderDetail> details) {
		log.debug("updateSelective: {}",details);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < details.size(); i++) {
				TransportOrderDetail detail = details.get(i);
				StringBuilder check = 	new StringBuilder(CheckUtils.checkLong("NO", "data", "运单子表id", detail.getTransportOrderDetailId(), 20));
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transportorderdetailmapper.updateByPrimaryKeySelective(details);

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
	public void add(TransportOrderDetail transportOrderDetail, TransportOrder transportOrder) {
        log.debug("add: {}",transportOrderDetail);
        Environment env = Environment.getEnv();
        StringBuilder s;
    /*    StockCar stockCar = null;*/
        try {
            transportOrderDetail.setTransportOrderId(transportOrder.getTransportOrderId());
            transportOrderDetail.setTransportOrderCode(transportOrder.getTransportOrderCode());
            transportOrderDetail.setCorpId(env.getCorp().getCorpId());
            transportOrderDetail.setDriverId(transportOrder.getDriverId());
            transportOrderDetail.setFlag(0);
            s = checkNewRecord(transportOrderDetail);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			//'车检标志:0=否 1=是',
            if(transportOrderDetail.getExamination() != null) {
            	if(transportOrderDetail.getExamination().intValue() == 1) {
    				if(transportOrderDetail.getExaminationLocationId() == null) {
    					 throw new BusinessException("请选择有效的车检地址！");
    				}
    			maintainExamination(transportOrder.getTransportOrderId());
    			}else if(transportOrderDetail.getExamination().intValue() == 0) {
    				transportOrderDetail.setExaminationLocationId(null);
    			}	
            }

            getMapper().insertSelective(transportOrderDetail);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else if(e instanceof BusinessException){
    					throw e;
    		}else {
    					throw new RuntimeException("操作失败！", e);
    		}
        }  
    }

	@Override
	@Transactional
	public TransportOrderDetail getDetail(Map<String, Object> map) {
		try {
			if (map == null) {
				throw new RuntimeException("参数为空");
			}
			return transportorderdetailmapper.getDetail(map);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public TransportOrderDetail getTransOrder(Map<String, Object> map) {
		try {
			
			return transportorderdetailmapper.getTransOrder(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public PageInfo<TransportOrderDetail> queryDetailRefer(Map<String, Object> data, int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
		List<TransportOrderDetail> details = new ArrayList<TransportOrderDetail>();
		PageInfo<TransportOrderDetail> transportOrderDetailPageInfo = null;
		try {
			if(data == null) {
				data = new HashMap<>();
        	}
			List<Long> lowerCorpIds = new ArrayList<>();
			ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<Long>();
		/*	List<TransportOrderDetail>   queryDetails = transportorderdetailmapper.selectRefer(data);*/
			Environment environment = Environment.getEnv();
			Long corpId = environment.getUser().getCorpId();

			queue.add(corpId);
			while (!queue.isEmpty()) {
				Long lowerCorpId = queue.poll();
				lowerCorpIds.add(lowerCorpId);

				queue.addAll(corpService.queryHigherCorpByCorpId(lowerCorpId));
			}

			lowerCorpIds.sort(Comparator.comparing(Long::longValue));
			if (CollectionUtils.isNotEmpty(lowerCorpIds)) {
				data.put("list",lowerCorpIds);
				transportOrderDetailPageInfo = queryByCorpIds(data, pageNum, pageSize);


			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
 		}
		return transportOrderDetailPageInfo;
	}

	/*@Override
	public List<TransportOrderDetail>  queryByDetaiIds(List<Long> detailIds) {
		try {
			log.debug("queryByDetaiIds: ", detailIds);
			List<TransportOrderDetail> details = transportorderdetailmapper.selectByDetaiIds(detailIds);
			return details;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}*/



	@Override
	@Transactional
	public List<Map<String, Object>> getArrivalList() {
		Environment environment=Environment.getEnv();
		long driverId=environment.getUser().getUserId();
		List<Map<String,Object>> list=transportorderdetailmapper.getArrivalList(driverId);
		list.forEach(map -> {
			int carStatus=Integer.parseInt(map.get("carStatus").toString());
			if (carStatus==9){
				map.put("status",true);
			}else{
				map.put("status",false);
			}
		});
		return list;
	}

	@Override
	@Transactional
	public List<Map<String, Object>> getLoadList() {
		Environment environment=Environment.getEnv();
		long driverId=environment.getUser().getUserId();
		List<Map<String,Object>> list=transportorderdetailmapper.getLoadList(driverId);
		list.forEach(map -> {
			int carStatus=Integer.parseInt(map.get("carStatus").toString());
			if (carStatus==6 || carStatus==7){
				map.put("status",true);
			}else{
				map.put("status",false);
			}
		});
		return list;
	}

	//扫描装车更新车辆状态
	@Override
	@Transactional
	public void updateBillStatus(List<TransportOrderDetail> list) {
		log.debug("updateBillStatus:{}", list);
		try {
			if (list.size()==0 || list==null) {
				throw new BusinessException("参数为空");
			}
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=new HashMap<>();
				map.put("carVin",list.get(i).getCarVin());

				Environment environment=Environment.getEnv();
				long driverId=environment.getUser().getUserId();
				String carVin=map.get("carVin").toString();
				map.put("driverId", driverId);
				//通过司机ID和车架号查询子运单
				TransportOrderDetail transportOrderDetail=transportorderdetailmapper.getDetail(map);
				if (transportOrderDetail==null) {
					throw new BusinessException("车架号:"+carVin+"不存在运单中");
				}
				if (transportOrderDetail.getCarStatus()<6) {
					throw new BusinessException("车架号:"+carVin+"未接单,不能进行扫描装车操作");
				}else if(transportOrderDetail.getCarStatus()==6){
					Map<String,Object> m=new HashMap<>();
					m.put("transportOrderId",transportOrderDetail.getTransportOrderId());
					List<LoadingReservation> loadingReservations=loadingReservationService.queryList(m);
					for (LoadingReservation loadingReservation:loadingReservations) {
						loadingReservation.setBillStatus(1);
						loadingReservationService.updateSelective(loadingReservation);
					}
				}else if (transportOrderDetail.getCarStatus()>7 && transportOrderDetail.getCarStatus()<11) {
					throw new BusinessException("车架号:"+carVin+"已装车,请勿重复进行扫描装车操作");
				}else if (transportOrderDetail.getCarStatus()==11) {
					throw new BusinessException("车架号:"+carVin+"已取消运输计划,不能进行扫描装车操作");
				}
				//设置运单子表车辆状态为8(已装车)
				transportOrderDetail.setCarStatus(8);
				transportOrderDetail.setRealityLoadingDate(new Date());
				transportorderdetailmapper.updateByPrimaryKeySelective(transportOrderDetail);
				//更改运单主表实际装车时间
				if (i==list.size()-1){
					TransportOrder transportOrder=transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
					transportOrder.setLoadingDate(new Date());
					transportOrderService.updateSelective(transportOrder);
				}
				//更新仓库装车计划表状态
				warehouseLoadingPlanService.updateCarVin(carVin, transportOrderDetail.getTransportOrderId(), 3);
				//flag=false,所有车辆已装车,flag=true,则不是
				boolean flag=false;
				//查询所有的运单子表,遍历子表,若所有的都是已装车,车辆状态为9,若不是,车辆状态为8
				Map<String, Object> m=new HashMap<>();
				m.put("transportOrderId", transportOrderDetail.getTransportOrderId());
				List<TransportOrderDetail> transportOrderDetails=transportorderdetailmapper.selectList(m);
				for (TransportOrderDetail transDetail : transportOrderDetails) {
					if (transDetail.getCarStatus()!=8) {
						flag=true;
					}
				}

				//flag=false,更新库存表中所有的车架号为9(在途)
				if (!flag) {
					for (TransportOrderDetail transDetail : transportOrderDetails) {
						StockCar stockCar=stockcarmapper.selectByTransOrderDetailId(transDetail.getTransportOrderDetailId());
						//通过车架号查询库表对应的车架号记录
						stockCar.setCarStatus(9);
						stockcarmapper.updateByPrimaryKeySelective(stockCar);
						//更新运单子表车辆状态为在途
						transDetail.setCarStatus(9);
						transportorderdetailmapper.updateByPrimaryKeySelective(transDetail);
						m.put("carVin", transDetail.getCarVin());
						List<OrderDetail> orderDetails=orderDetailService.queryList(m);
						for (OrderDetail orderDetail : orderDetails) {
							orderDetail.setCarStatus(9);
							orderDetailService.updateSelective(orderDetail);
						}
					}
					//运单更新为在途
					TransportOrder transportOrder=transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
					transportOrder.setBillStatus(4);
					for (TransportOrderDetail trans : transportOrder.getDetails()) {
						trans.setRowStatus(0);
					}
					transportOrderService.updateSelective(transportOrder);
				}else {
					StockCar stockCar=stockcarmapper.selectByTransOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
					//更新库存表车辆状态为已装车
					stockCar.setCarStatus(8);
					stockcarmapper.updateByPrimaryKeySelective(stockCar);
					//更新子订单车辆状态为已装车
					m.remove("transportOrderId");
					m.put("carVin", stockCar.getCarVin());
					m.put("orderId",stockCar.getOrderId());
					List<OrderDetail> orderDetails=orderDetailService.queryList(m);
					for (OrderDetail detail : orderDetails) {
						detail.setCarStatus(8);
						orderDetailService.updateSelective(detail);
					}

				}
			}
		} catch (Exception e) {
			throw e;
		}

	}
	//扫描到店
	@Override
	@Transactional
	public void updateStatus(List<TransportOrderDetail> list) {
		log.debug("updateStatus:{}", list);
		try {
			if (list==null || list.size()==0) {
				throw new BusinessException("参数为空");
			}
			list.forEach(transDetail -> {
				Map<String, Object> map = new HashMap<>();
				map.put("carVin", transDetail.getCarVin());

				Environment environment = Environment.getEnv();
				long driverId = environment.getUser().getUserId();
				String carVin = map.get("carVin").toString();
				map.put("driverId", driverId);
				//通过司机ID和车架号查询子运单
				TransportOrderDetail transportOrderDetail = transportorderdetailmapper.getDetail(map);
				if (transportOrderDetail == null) {
					throw new BusinessException("车架号:" + carVin + "不存在运单中");
				}
				if (transportOrderDetail.getCarStatus() != 9) {
					throw new BusinessException("车架号:" + carVin + "不是在途状态,不能进行扫描到店操作");
				}
				//更改子运单状态
				transportOrderDetail.setCarStatus(10);
				transportOrderDetail.setRealityDeliveryDate(new Date());
				transportorderdetailmapper.updateByPrimaryKeySelective(transportOrderDetail);

				//更新存存表状态
				StockCar stockCar = stockcarmapper.selectByTransOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
				stockCar.setCarStatus(10);
				stockcarmapper.updateByPrimaryKeySelective(stockCar);
				map.put("orderId",stockCar.getOrderId());
				List<OrderDetail> orderDetails = orderDetailService.queryList(map);
				for (OrderDetail detail : orderDetails) {
					detail.setCarStatus(10);
					orderDetailService.updateSelective(detail);
				}
				Map<String, Object> m = new HashMap<String, Object>();
				boolean flag = false;
				m.put("transportOrderId", transportOrderDetail.getTransportOrderId());
				List<TransportOrderDetail> transportOrderDetails = transportorderdetailmapper.selectList(m);
				for (TransportOrderDetail transportDetail : transportOrderDetails) {
					if (transportDetail.getCarStatus() != 10) {
						flag = true;
					}
				}
				if (!flag) {
					TransportOrder transOrder = transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
					transOrder.setBillStatus(6);
					for (TransportOrderDetail trans : transOrder.getDetails()) {
						trans.setRowStatus(0);
					}
					transportOrderService.updateSelective(transOrder);
				}
			});
		} catch (Exception e) {
			throw e;
		}

	}
	@Override
	public PageInfo<TransportOrderDetail>  queryByCorpIds(Map<String, Object> data, int pageNum, int pageSize) {
		try {
			log.debug("queryByCorpIds: {}, page: {},{}", data,pageNum,pageSize);
			PageHelper.startPage(pageNum, pageSize);
			List<TransportOrderDetail> details = transportorderdetailmapper.selectByCorpIds(data);
			return new PageInfo<TransportOrderDetail>(details);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void maintainFreighPrice(Freight freight) {
		log.debug("queryByFreightId: {}", freight);
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "freightId", "结算价格ID", freight.getFreightId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			List<TransportOrderDetail> details = Optional.ofNullable(transportorderdetailmapper.selectByFreightId(freight.getFreightId())).orElse(new ArrayList<>());
			freightService.getAllPrice(details);
			List<Long> transportOrderIds = new ArrayList<>();
			List<Long> driverIds = new ArrayList<>();
			for (TransportOrderDetail detail : details) {
				detail.setFinalPrice(freight.getFinalPrice());
				transportOrderIds.add(freight.getFreightId());
				driverIds.add(detail.getDriverId());
			}
			List<Long> collect = transportOrderIds.stream().distinct().collect(Collectors.toList());
			driverIds = driverIds.stream().distinct().collect(Collectors.toList());
			updateSelective(details);
/*            List<TransportSettlement> transportSettlements = transportSettlementService.queryByTransportOrderId(collect);
            transportSettlementService.maintainFreighPrice(transportSettlements,details);*/
			List<DriverSettlement> driverSettlements = driverSettlementService.queryByDriverIds(driverIds);
			driverSettlementService.maintainPrice(driverSettlements,details);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}



}
