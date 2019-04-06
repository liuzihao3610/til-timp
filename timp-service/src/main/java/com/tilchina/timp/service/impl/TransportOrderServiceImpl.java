package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.AudienceType;
import com.tilchina.timp.enums.CarStatus;
import com.tilchina.timp.enums.PlatformType;
import com.tilchina.timp.enums.TransportOrderBillStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportOrderMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.util.GaodeApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
* 运单主表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransportOrderServiceImpl extends BaseServiceImpl<TransportOrder> implements TransportOrderService {

	@Autowired
    private TransportOrderMapper transportordermapper;
	
	@Autowired
    private TransportOrderDetailService transportOrderDetailService;
	
	@Autowired
    private TransporterService transporterService;

	@Autowired
    private TrailerService trailerService;
	
	@Autowired
    private WarehouseLoadingPlanService  warehouseLoadingPlanService;
	
	@Autowired
    private DriverService  driverService;
	
	@Autowired
    private PushService pushService;
	
	@Autowired
    private StockCarService stockCarService;
	
	@Autowired
    private StockCarTransService stockCarTransService;
	
	@Autowired
    private TransportPlanService transportPlanService;
	
	@Autowired
    private TransportSettlementService transportSettlementService;
	
	@Autowired
    private UnitService unitService;

	@Autowired
	private CarTypeService carTypeService;

	@Autowired
    private LoadingReservationService  loadingReservationService ;
	
	@Autowired
    private LoadingReservationDetailService loadingReservationDetailService;
	
	@Autowired
    private OrderDetailService orderDetailService;

	@Autowired
    private OrderService orderService;
	
	@Autowired
	private BrandService carBrandService;

	@Autowired
	private CarService carModelService;

	@Autowired
	private CityService cityService;

	@Autowired
	private TransferOrderService transferOrderService;

	
	@Override
	protected BaseMapper<TransportOrder> getMapper() {
		return transportordermapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransportOrder transportorder) {
		StringBuilder s = new StringBuilder();
		String transportOrderCode = Optional.ofNullable(transportorder.getTransportOrderCode()).orElse(DateUtil.dateToStringCode(new Date()));
		if("".equals(transportOrderCode)){
			transportorder.setTransportOrderCode(DateUtil.dateToStringCode(new Date()));
		}else{
			transportorder.setTransportOrderCode(transportOrderCode);
		}
		transportorder.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", transportorder.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "jobType", "作业类型:0=长途,1=市内,2=短驳,3=回程", transportorder.getJobType(), 10));
        s.append(CheckUtils.checkInteger("NO", "examination", "车检标志", transportorder.getExamination(), 10));
        s.append(CheckUtils.checkLong("YES", "salesmanId", "业务员", transportorder.getSalesmanId(),20));
        s.append(CheckUtils.checkDate("YES", "loadingDate", "装车日期", transportorder.getLoadingDate()));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "订单状态:0=制单,1=审核,2=关闭", transportorder.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "createDate", "制单日期", transportorder.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", transportorder.getCheckDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportOrder transportorder) {
        StringBuilder s = checkNewRecord(transportorder);
        s.append(CheckUtils.checkPrimaryKey(transportorder.getTransportOrderId()));
		return s;
	}
	@Transactional
	@Override
	public void add(List<TransportOrder> transportorders) {
		log.debug("addBatch: {}",transportorders);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < transportorders.size(); i++) {
				TransportOrder transportorder = transportorders.get(i);
				StringBuilder check = checkNewRecord(transportorder);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}

			getMapper().insert(transportorders);
            for (TransportOrder transportOrder : transportorders) {
                List<TransportOrderDetail> details = Optional.ofNullable(transportOrder.getDetails()).orElse(new ArrayList<TransportOrderDetail>());
                details.forEach(detail -> {
                    detail.setTransportOrderId(transportOrder.getTransportOrderId());
                    detail.setRowStatus(1);
                });
                transportOrderDetailService.adds(transportOrder.getDetails(),transportOrder);
                //维护运单子表车辆状态
                updateCarStatus(transportOrder.getTransportOrderId(), 3, 0);
                //维护运单结算信息
                transportSettlementService.add(transportOrder);
            }
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else {
				throw e;
			}
		}
	}

	@Transactional
	@Override
    public void add(TransportOrder transportOrder) {
        log.debug("add: {}",transportOrder);
        StringBuilder s;
        Environment env = Environment.getEnv();
        TransportSettlement transportSettlement;
        /*User driver = null;*/
        try {
            s = checkNewRecord(transportOrder);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            try {
            	 transporterService.queryById(transportOrder.getTransporterId());
				User driver = Optional.ofNullable(driverService.queryById(transportOrder.getDriverId())).orElse(new User());
			} catch (Exception e) {
		        if(e instanceof BusinessException){
		        	if(e.getMessage().indexOf("轿运车") != -1) {
		        		 throw new BusinessException("请选择有效的轿运车,此轿运车信息无效！");
		        	}else if(e.getMessage().indexOf("用户") != -1) {
		        		 throw new BusinessException("请选择有效的用户,用户信息无效！");
		        	}
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
			User driver = Optional.ofNullable(driverService.queryById(transportOrder.getDriverId())).orElse(new User());

			//通过司机类型维护结算模式:'0=承包模式,1=非承包模式','司机类型 0=未指定 1=承包司机 2=非承包司机',
            if(driver.getDriverType() == 1) {
            	transportOrder.setSettleType(0);
            }else if(driver.getDriverType() == 2) {
            	transportOrder.setSettleType(1);
            }else {
				transportOrder.setSettleType(1);
			}
            transportOrder.setCorpId(env.getCorp().getCorpId());
            transportOrder.setCreator(env.getUser().getUserId());
            getMapper().insertSelective(transportOrder);
            transportOrderDetailService.adds(transportOrder.getDetails(),transportOrder);
            //维护运单子表车辆状态
            updateCarStatus(transportOrder.getTransportOrderId(), 3, 0);
            //维护运单结算信息
            transportSettlementService.add(transportOrder);
        } catch (Exception e) {
        	log.error("{}", e);
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
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
		    		s.append(CheckUtils.checkInteger("NO", "data", "运单主ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		    queryById(Long.valueOf(id));
		   		    //维护商品车库存表：车辆状态
				    updateCarStatus(Long.valueOf(id), 0, 0);
	    		}
	        	transportordermapper.logicDeleteByPrimaryKeyList(ids);
	        	transportOrderDetailService.logicDeleteByTransportOrderIdList(ids);
	        	//维护运单结算信息
	        	transportSettlementService.deleteByTransportOrderId(ids);
	        	//仓库装车计划、预约装车计划
			    warehouseLoadingPlanService.logicDeleteByTransportOrderId(ids);
			    loadingReservationService.logicDeleteByTransportOrderId(ids);
        	}else {
        		throw new BusinessException("9001","运单主ID");
        	}
        	
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
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单主ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

   		    //维护商品车库存表：车辆状态
		    updateCarStatus(id, 0, 0);
   		    TransportOrderDetail transportOrderDetail = null;
		    if(transportOrderDetail == null) {
		    	transportOrderDetail = new TransportOrderDetail();
		    	transportOrderDetail.setTransportOrderId(id);
				transportOrderDetailService.logicDeleteByTransportOrderId(transportOrderDetail);
		    }
		    //维护运单结算信息
		    transportSettlementService.deleteByTransportOrderId(id);
		    if(queryById(id).getBillStatus().intValue() <  TransportOrderBillStatus.ROUTE.getIndex()) {
		    	transportordermapper.logicDeleteByPrimaryKey(id);
		    }else {
		    	throw new BusinessException("该运单已在途中，请联系管理员取消！");
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
	public List<TransportOrder> queryByIds(List<Long> transportOrderIds) {
		log.debug("queryByIds: {}",transportOrderIds);
		try {
			List<TransportOrder> transportOrders = new ArrayList<>();
			if(transportOrderIds.size() > 0){
				transportOrders = transportordermapper.selectByTransportOrderIds(transportOrderIds);
			}
			for (TransportOrder transportOrder : transportOrders) {
				Map<String,Object> map = new HashMap<>();
				map.put("transportOrderId", transportOrder.getTransportOrderId());
				map.put("driverId", transportOrder.getDriverId());
				transportOrder.setDetails(transportOrderDetailService.queryList(map));
			}
			return transportOrders;
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new RuntimeException("操作失败！", e);
		}
	}

	@Override
    public TransportOrder queryById(Long id) {
        log.debug("query: {}",id);
        TransportOrder transportOrder = null;
        Map<String, Object> map = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单主ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	transportOrder = getMapper().selectByPrimaryKey(id);
        	if(transportOrder == null) {
        		throw new BusinessException("9008","运单主ID");
        	}
        	 map = new HashMap<>();
        	 map.put("transportOrderId", id);
        	 map.put("driverId", transportOrder.getDriverId());
        	transportOrder.setDetails(transportOrderDetailService.queryList(map));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportOrder;
    }
	
	/**
	 * 维护运单审核后仓库装车计划信息
	 * @param transportOrder
	 */
	private void MaintainUpdateStatus(TransportOrder transportOrder) {
		Map<String,Object> map = null;
        WarehouseLoadingPlan warehouseLoadingPlan = null;
        List<WarehouseLoadingPlan> warehouseLoadingPlans = null;
        List<LoadingReservation> loadingReservations = null;
        List<LoadingReservationDetail> loadingReservationDetails = null;
        LoadingReservation loadingReservation = null;
        LoadingReservationDetail loadingReservationDetail = null;
        List<TransportOrderDetail> transportOrderDetails = null;
        List<Loading> Loadings = null;
        try {
        	 map = new HashMap<>();
        	 transportordermapper.updateByPrimaryKeySelective(transportOrder);
        	 map.put("transportOrderId", transportOrder.getTransportOrderId());
         	 transportOrder =  queryById(transportOrder.getTransportOrderId());
	    	 transportOrderDetails = transportOrder.getDetails();
             if(transportOrder.getBillStatus().intValue()  == TransportOrderBillStatus.SEND.getIndex()) {
            	 warehouseLoadingPlans = new ArrayList<WarehouseLoadingPlan>();
 	    		for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
 		        	//仓库装车计划
 		        	warehouseLoadingPlan = new WarehouseLoadingPlan();
 		        	warehouseLoadingPlan.setPlanDate(transportOrder.getLoadingDate());
 		        	warehouseLoadingPlan.setUnitId(transportOrderDetail.getSendUnitId());
 		        	warehouseLoadingPlan.setCarId(transportOrderDetail.getCarId());
 		        	warehouseLoadingPlan.setCarVin(transportOrderDetail.getCarVin());
 		        	//默认为 0=待定
 		        	warehouseLoadingPlan.setBillStatus(0);
 		        	warehouseLoadingPlan.setDriverId(transportOrder.getDriverId());
 		        	warehouseLoadingPlan.setTransporterId(transportOrder.getTransporterId());
 		        	warehouseLoadingPlan.setLoadingDate(transportOrderDetail.getClaimLoadingDate());
 		        	warehouseLoadingPlan.setTransportOrderId(transportOrder.getTransportOrderId());
 		        	warehouseLoadingPlan.setTransportOrderCode(transportOrder.getTransportOrderCode());
 		        	warehouseLoadingPlan.setCorpId(transportOrder.getCorpId());
 		        	warehouseLoadingPlan.setPlanDate(new Date());
 		        	warehouseLoadingPlans.add(warehouseLoadingPlan);
 		        	
 	    		}
 	    		warehouseLoadingPlanService.add(warehouseLoadingPlans);
             }else if(transportOrder.getBillStatus().intValue()  == TransportOrderBillStatus.ORDERRECEIVING.getIndex()) {
            	 transportOrder =  queryById(transportOrder.getTransportOrderId());
            	 Loadings = transportOrderDetailService.queryLoadingCount(map,0); 
            	 loadingReservations = new ArrayList<LoadingReservation>();
            	 loadingReservationDetails = new ArrayList<LoadingReservationDetail>();
            	 //维护仓库装车计划信息 1=申请
  				/*warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
  				for (WarehouseLoadingPlan warehouseLoading : warehouseLoadingPlans) {
  					warehouseLoading.setBillStatus(1);
  					warehouseLoadingPlanService.updateSelective(warehouseLoading);
  				}*/
        		 //单据状态:0=未预约 1=已预约 2=已审核
            	 for (Loading loading : Loadings) {
                	 //维护预约装车主表数据
                	 loadingReservation = new LoadingReservation();
                	 loadingReservation.setTransportOrderId(transportOrder.getTransportOrderId());
                	 loadingReservation.setTransportOrderCode(transportOrder.getTransportOrderCode());
                	 loadingReservation.setUnitId(loading.getSendUnitId());
                	 loadingReservation.setDriverId(transportOrder.getDriverId());
                	 loadingReservation.setTransporterId(transportOrder.getTransporterId());
                	 loadingReservation.setCarLoadingNumber(loading.getCarCount());
                	 if(transportOrder.getLoadingDate() == null) {
                		 loadingReservation.setLoadingTime(new Date());
                	 }else {
                		 loadingReservation.setLoadingTime(transportOrder.getLoadingDate());
                	 }
                	 loadingReservation.setBillStatus(0);
                	 loadingReservation.setCreateDate(new Date());
                	 loadingReservation.setFlag(0);
                	 loadingReservationService.add(loadingReservation);
                	 for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
                		 if(loading.getSendUnitId().intValue() == transportOrderDetail.getSendUnitId().intValue()) {
	                		 loadingReservationDetail = new LoadingReservationDetail();
	                		 //维护预约装车子表数据
	                		 loadingReservationDetail.setLoadingReservationId(loadingReservation.getLoadingReservationId());
	                		 loadingReservationDetail.setTransportOrderId(loadingReservation.getTransportOrderId());
	                		 loadingReservationDetail.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
	                		 loadingReservationDetail.setCarVin(transportOrderDetail.getCarVin());
	                		 loadingReservationDetail.setCarId(transportOrderDetail.getCarId());
	                		 loadingReservationDetail.setRowStatus(1);
	                		 loadingReservationDetail.setCorpId(loadingReservation.getCorpId());
	                		 loadingReservationDetailService.add(loadingReservationDetail);
                		 }
					}
                	/* loadingReservationDetailService.add(loadingReservationDetails);*/
				}
            	/* loadingReservationService.add(loadingReservations);*/
             }
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
    public void updateSelective(TransportOrder record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        List<TransportOrderDetail> transportOrderDetails = null;
        List<TransportOrderDetail> details = null;
        TransportOrder judge;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "transportOrderId", "", record.getTransportOrderId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    judge = queryById(record.getTransportOrderId());
   		    if(judge.getBillStatus()  == TransportOrderBillStatus.CHECK.getIndex()) {
   		    	throw new BusinessException("运单只有在制单状态才能修改，请先将运单状态回退为制单状态！");
   		    }
   		    /*driverService.queryById(record.getDriverId());*/
   		    transportOrderDetails = new ArrayList<>(); 
   		    if(record.getDetails() != null ) {
   		     transportOrderDetails = record.getDetails();
   		     MaintainUpdateStatus(record);
   		    }else if(record.getBillStatus() != null) {
   		    	MaintainUpdateStatus(record);
   		    }
   		    if(transportOrderDetails != null ) {
   		    	details = transportOrderDetails.stream().filter(detail ->
   		    	detail.getRowStatus().intValue() != 0 && detail.getRowStatus().intValue() != 3).collect(Collectors.toList());
   		    	/*if(detail.getRowStatus() == null) {
   		    		
   		    	}*/
   		    	//	维护发货单位
                Map<Long, TransportOrderDetail> detailMap = new HashMap<>();
                details.stream().forEach(detail -> {
                	detailMap.put(detail.getSendUnitId(), detail);
                });
                if(detailMap.size() != 1 && detailMap.size() != 0) {
                	 throw new BusinessException("请统一发货单位！");
                } 
                //运单子表
	            for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
	            	if(transportOrderDetail.getRowStatus() != null) {
		            	//0=UNCHANGE、1=NEW、2=EDIT、3=DELETE
						if(0 ==  transportOrderDetail.getRowStatus().intValue()) {
							
						}else if(1 ==  transportOrderDetail.getRowStatus().intValue()) {
							transportOrderDetail.setTransportOrderId(record.getTransportOrderId());
							transportOrderDetail.setTransportOrderCode(record.getTransportOrderCode());
							transportOrderDetail.setDriverId(record.getDriverId());
							transportOrderDetail.setCorpId(record.getCorpId());
							transportOrderDetailService.add(transportOrderDetail);
						}else if(2 ==  transportOrderDetail.getRowStatus().intValue()) {
							transportOrderDetail.setTransportOrderId(record.getTransportOrderId());
							transportOrderDetail.setTransportOrderCode(record.getTransportOrderCode());
							transportOrderDetail.setDriverId(record.getDriverId());
							transportOrderDetailService.updateSelective(transportOrderDetail);
						}else if(3 ==  transportOrderDetail.getRowStatus().intValue()) {
							transportOrderDetail.setTransportOrderId(record.getTransportOrderId());
							transportOrderDetail.setTransportOrderCode(record.getTransportOrderCode());
							transportOrderDetail.setDriverId(record.getDriverId());
							transportOrderDetailService.logicDeleteByTransportOrderId(transportOrderDetail);
						}
	            	}
				}
   		    }
            transportordermapper.updateByPrimaryKeySelective(record);
           
        } catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
    }

	@Override
	public TransportOrder queryById(Map<String, Object> map, int pageNum, int pageSize) {
    	log.debug("queryById: {}, page: {},{}", map,pageNum,pageSize);
        StringBuilder s;
        TransportOrder transportOrder = null;
        PageInfo<TransportOrderDetail> pageInfo;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkString("NO", "transportOrderId", "运单主表Id", map.get("transportOrderId").toString(), 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
        	transportOrder = getMapper().selectByPrimaryKey(Long.valueOf(map.get("transportOrderId").toString()));
        	pageInfo = transportOrderDetailService.queryList(map, pageNum, pageSize);
        	transportOrder.setDetails((pageInfo.getList()));
        	transportOrder.setRefDetailTotal((pageInfo.getTotal()));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return transportOrder;
    }

    @Override
    public PageInfo<TransportOrder> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageInfo<TransportOrder> transportOrders = null;
        List<TransportOrderDetail> detail = null;
        Environment env = Environment.getEnv();
		PageHelper.startPage(pageNum, pageSize);
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("corpId", env.getCorp().getCorpId());
			transportOrders = new PageInfo<TransportOrder>(transportordermapper.selectList(map));
        	for (TransportOrder transportOrder : transportOrders.getList()) {
        		map = new HashMap<>();
        		map.put("transportOrderId", transportOrder.getTransportOrderId());
        		map.put("driverId", transportOrder.getDriverId());
        		detail = transportOrderDetailService.queryList(map);
        		transportOrder.setDetails(detail);
			}
        	
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
        return transportOrders;
    }
    
    @Override
    public List<TransportOrder> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        List<TransportOrder> transportOrders = null;
        List<TransportOrderDetail> detail = null;
        Environment env = Environment.getEnv();
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("corpId", env.getCorp().getCorpId());
			transportOrders = getMapper().selectList(map);
        	for (TransportOrder transportOrder : transportOrders) {
        		map = new HashMap<>();
        		map.put("transportOrderId", transportOrder.getTransportOrderId());
        		map.put("driverId", transportOrder.getDriverId());
        		detail = transportOrderDetailService.queryList(map);
        		transportOrder.setDetails(detail);
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
        return transportOrders;
    }
	
	@Override
	public PageInfo<TransportOrder> queryRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageInfo<TransportOrder> transportOrders = null;
        Environment env = Environment.getEnv();
		PageHelper.startPage(pageNum, pageSize);
		try {
			if(data == null) {
				data = new HashMap<>();
        	}
			data.put("corpId", env.getCorp().getCorpId());
			transportOrders =  new  PageInfo<TransportOrder> (transportordermapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return transportOrders;
	}

	
	/**
	 * 生成运输计划表
	 * @param frontLoading	收发货前信息
	 * @param loading	收发货信息
	 * @param transportOrder	运单表头信息
	 * @param sequence	顺序号
	 * @param type 0=发货单位第一条 1=发货单位 2=收货单位 3=收货单位第一条
	 * @return
	 */
	private TransportPlan createPlan(Loading frontLoading,Loading loading,TransportOrder transportOrder,int sequence,int type) {
		log.debug("createPlan:{},{}",frontLoading,transportOrder);
		Unit sendUnit = null,receiveUnit = null; 
		Trailer trailer = null;
		Environment env = Environment.getEnv();
		TransportPlan transportPlan = new TransportPlan();
		try {
			int maxQuantity = 0;
			if(type == 0) {
				receiveUnit = unitService.queryById(loading.getSendUnitId());
			}else if(type == 1) {
        		sendUnit = unitService.queryById(frontLoading.getSendUnitId());
            	receiveUnit = unitService.queryById(loading.getSendUnitId());
        	}else if(type == 2) {
        		sendUnit = unitService.queryById(frontLoading.getReceiveUnitId());
            	receiveUnit = unitService.queryById(loading.getReceiveUnitId());
        	}else if(type == 3) {
        		sendUnit = unitService.queryById(frontLoading.getSendUnitId());
            	receiveUnit = unitService.queryById(loading.getReceiveUnitId());
        	}
			transportPlan.setTransportOrderId(transportOrder.getTransportOrderId());
	    	transportPlan.setTransportOrderCode(transportOrder.getTransportOrderCode());
	    	transportPlan.setTransporterId(transportOrder.getTransporterId());
	    	transportPlan.setDriverId(transportOrder.getDriverId());
	    	transportPlan.setCorpId(env.getCorp().getCorpId());
	    	transportPlan.setHandingCount(0);
	    	transportPlan.setCarCount(0);
	    	transportPlan.setSequence(sequence);
	    	transportPlan.setLoadWeight(new Double(0.00));
	    	if(loading.getCarCount() != null) {
	    		transportPlan.setCarCount(loading.getCarCount());
	    		maxQuantity += loading.getCarCount();
	    	}
        	if(loading.getHandingCount() != null) {
        		transportPlan.setHandingCount(loading.getHandingCount());
        		maxQuantity -= loading.getHandingCount();
        	}
	       	if(sendUnit == null || receiveUnit == null) {
				//距离
	        	transportPlan.setDriving(0.00);
	        	if(sendUnit != null) {
					transportPlan.setStartUnitId(sendUnit.getUnitId());
	            	transportPlan.setStartLng(sendUnit.getLng());
	            	transportPlan.setStartLat(sendUnit.getLat());
				}else if(receiveUnit != null) {
					transportPlan.setEndUnitId(receiveUnit.getUnitId());
	            	transportPlan.setEndLng(receiveUnit.getLng());
	            	transportPlan.setEndLat(receiveUnit.getLat());
				}
			}else {
				transportPlan.setStartUnitId(sendUnit.getUnitId());
	        	transportPlan.setStartLng(sendUnit.getLng());
	        	transportPlan.setStartLat(sendUnit.getLat());
	        	transportPlan.setEndUnitId(receiveUnit.getUnitId());
	        	transportPlan.setEndLng(receiveUnit.getLng());
	        	transportPlan.setEndLat(receiveUnit.getLat());
	        	//距离
	        	transportPlan.setDriving(GaodeApi.getDistance(GaodeApi.doubleChangeString(sendUnit.getLng(),sendUnit.getLat()),GaodeApi.doubleChangeString(receiveUnit.getLng(),receiveUnit.getLat())));
			}
	       	if(transporterService.queryById(transportPlan.getTransporterId()) != null) {
	       		if(transporterService.queryById(transportPlan.getTransporterId()).getTrailerId() != null ) {
	       			trailer = trailerService.queryById(transporterService.queryById(transportPlan.getTransporterId()).getTrailerId());
	       		}
        	}
	       	if(trailer != null) {
	    		//0=满载 1=非满载 2=空载
	        	if(maxQuantity == trailer.getMaxQuantity()) {
	        		transportPlan.setLoadType(0);
	        	}else if(maxQuantity == 0) {
	        		transportPlan.setLoadType(2);
	        	}else {
	        		transportPlan.setLoadType(1);
	        	}	
	    	}else {
	    		//0=满载 1=非满载 2=空载
	        	if(maxQuantity == 7) {
	        		transportPlan.setLoadType(0);
	        	}else if(maxQuantity == 0) {
	        		transportPlan.setLoadType(2);
	        	}else {
	        		transportPlan.setLoadType(1);
	        	}
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
	
	@Transactional
	@Override
	public void updateCheck(TransportOrder transportOrder) {
		log.debug("updateCheck:{}",transportOrder);
		StringBuilder s ;
		Environment env = Environment.getEnv();
		List<TransportPlan> transportPlans = null;
		TransportPlan transportPlan = null;
		List<Loading> startLoadings = null,endLoadings = null;
		TransportOrder updateTransportOrder = null;
		int sequence = 0;
		try {
			s = new StringBuilder();
			transportPlans = new ArrayList<>();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrder.getTransportOrderId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			updateTransportOrder = new TransportOrder();
			updateTransportOrder.setBillStatus(TransportOrderBillStatus.CHECK.getIndex());
			updateTransportOrder.setTransportOrderId(transportOrder.getTransportOrderId());
			transportOrder = queryById(transportOrder.getTransportOrderId());
			updateTransportOrder.setChecker(env.getUser().getUserId());
			updateTransportOrder.setCheckDate(new Date());
			if (null == transportOrder.getDriverId() ){
                throw new BusinessException("该运单尚未选择主驾司机不支持审核，请先维护好运单号："+transportOrder.getTransportOrderCode()+"的主驾司机！");
            }
			if(transportOrder.getBillStatus().intValue() == TransportOrderBillStatus.CHECK.getIndex() && transportOrder.getBillStatus().intValue() == updateTransportOrder.getBillStatus().intValue()
					|| transportOrder.getBillStatus().intValue() > TransportOrderBillStatus.CHECK.getIndex() ) {
				throw new BusinessException("该运单已审核通过,请勿重复审核！");
			}//TransportOrder(transportOrderId=136, transportOrderCode=20180524090634247,
			Unit sendUnit = null,receiveUnit = null; 
			Map<String, Object> map = null;
			map = new HashMap<>();
			map.put("transportOrderId", transportOrder.getTransportOrderId());
			//根据发货单位来生成运输计划表数据
			startLoadings = transportOrderDetailService.queryLoadingCount(map,0);
			for (int i = 0; i < startLoadings.size(); i++) {
				if(i == 0) {
					transportPlan = createPlan(null, startLoadings.get(i), transportOrder, sequence, 0);
					sequence++;
					transportPlans.add(transportPlan);
				}else {
					transportPlan = createPlan(startLoadings.get(i-1), startLoadings.get(i), transportOrder, sequence, 1);
					sequence++;
					transportPlans.add(transportPlan);
				}
			}
			//根据收货单位来生成运输计划表数据
			endLoadings = transportOrderDetailService.queryLoadingCount(map,1);
			if(transportPlans.size() > 0) {
				if(transportPlans.get(transportPlans.size()-1).getStartUnitId() != null) {
					sendUnit = unitService.queryById(transportPlans.get(transportPlans.size()-1).getStartUnitId());	
				}
				if(transportPlans.get(transportPlans.size()-1).getEndUnitId() != null) {
					sendUnit = unitService.queryById(transportPlans.get(transportPlans.size()-1).getEndUnitId());	
				}
			}
			for (int i = 0; i < endLoadings.size(); i++) {
				receiveUnit = unitService.queryById(endLoadings.get(i).getReceiveUnitId());
	        	//距离
				endLoadings.get(i).setDriving(GaodeApi.getDistance(GaodeApi.doubleChangeString(
	        			      sendUnit.getLng(),sendUnit.getLat()),GaodeApi.doubleChangeString(receiveUnit.getLng(),receiveUnit.getLat())));
			}
			endLoadings = Loading.sort(endLoadings);
			for (int i = 0; i < endLoadings.size(); i++) {
				if(i == 0) {
					transportPlan = createPlan(startLoadings.get(startLoadings.size()-1), endLoadings.get(i), transportOrder, sequence, 3);
					sequence++;
					transportPlans.add(transportPlan);
				}else {
					transportPlan = createPlan(endLoadings.get(i-1), endLoadings.get(i), transportOrder, sequence, 2);
					sequence++;
					transportPlans.add(transportPlan);
				}
			}
			Double predictKm = new Double(0.00);
			for (TransportPlan plan : transportPlans) {
				predictKm += plan.getDriving();
			}
			updateTransportOrder.setPredictKm(predictKm);
			//维护运单子表车辆状态
			updateCarStatus(updateTransportOrder.getTransportOrderId(),4,0);
			//维护运单结算主表信息
			transportSettlementService.check(updateTransportOrder,0);
			transportPlanService.add(transportPlans);
			transportordermapper.updateCheck(updateTransportOrder);
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
	public void updateCancelCheck(TransportOrder transportOrder) {
		log.debug("updateCheck:{}",transportOrder);
		StringBuilder s ;
		Map<String, Object> map;
		TransportOrder updateTransportOrder = null;
		List<Integer> dels = null;
		try {
			s = new StringBuilder();
			map = new HashMap<>();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrder.getTransportOrderId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			dels = new ArrayList<>();
			transportOrder = queryById(transportOrder.getTransportOrderId());
			if(transportOrder.getBillStatus() == TransportOrderBillStatus.UNCHECKED.getIndex()) {
				throw new BusinessException("该运单还未审核,请先审核！");
			}else if(transportOrder.getBillStatus() > TransportOrderBillStatus.CHECK.getIndex()) {
				throw new BusinessException("该运单已下达，请先取消下达！");
			}
			updateTransportOrder = new TransportOrder();
			updateTransportOrder.setBillStatus(TransportOrderBillStatus.UNCHECKED.getIndex());
			updateTransportOrder.setTransportOrderId(transportOrder.getTransportOrderId());
			updateTransportOrder.setChecker(null);
			updateTransportOrder.setCheckDate(null);
			updateTransportOrder.setPracticalKm(0.00);
			transportordermapper.updateCheck(updateTransportOrder);
			//维护运单结算主表信息
			transportSettlementService.check(updateTransportOrder,1);
			//维护运单子表车辆状态
			updateCarStatus(updateTransportOrder.getTransportOrderId(),CarStatus.Advance.getIndex(),1);
			//维护运输计划表
			map.put("transportOrderId", updateTransportOrder.getTransportOrderId());
			for (TransportPlan transportPlan : transportPlanService.querySequenceList(map)) {
				dels.add(Integer.valueOf(transportPlan.getTransportPlanId().toString()));
			}
			Integer[] array = dels.toArray(new Integer[dels.size()]);
			if(array.length > 0 ) {
				transportPlanService.logicDeleteByIdList(array);
			}
			
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
	public void updateTransmit(Long transportOrderId) {
		log.debug("updateTransmit:{}",transportOrderId);
		TransportOrder transportOrder = null;
		TransportOrder updateTransportOrder = null;
		StringBuilder s;
		Environment env = Environment.getEnv();
		Map<String, Object> map;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrderId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transportOrder = queryById(transportOrderId);
			if(transportOrder.getBillStatus() == TransportOrderBillStatus.CHECK.getIndex()) {
				transportOrder.setBillStatus(TransportOrderBillStatus.SEND.getIndex());
			}else if(transportOrder.getBillStatus()  == TransportOrderBillStatus.UNCHECKED.getIndex()){
				throw new BusinessException("请先审核再下达！");
			}else if(transportOrder.getBillStatus()  > TransportOrderBillStatus.CHECK.getIndex()) {
				throw new BusinessException("您已经下过单了，请勿重复执行下达！");
			}
			updateTransportOrder = new TransportOrder();
			updateTransportOrder.setTransportOrderId(transportOrder.getTransportOrderId());
			updateTransportOrder.setSalesmanId(env.getUser().getUserId());
			updateTransportOrder.setTransmitDate(new Date());
		 	transportordermapper.updateByTransmit(updateTransportOrder);
		 	//维护运单结算主表信息
			/*transportSettlementService.transmit(updateTransportOrder,0);*/
			 MaintainUpdateStatus(transportOrder);
	       	 map = new HashMap<>();
	       	 map.put("transportOrderId", transportOrder.getTransportOrderId());
	       	 map.put("driverId", transportOrder.getDriverId());
			//维护运单子表车辆状态
			updateCarStatus(updateTransportOrder.getTransportOrderId(),5,0);
			//维护极光推送
			Push push = new Push();
			JSONObject json = new JSONObject();
			push.setPlatformType(PlatformType.ANDROID.getIndex());
			push.setAudienceType(AudienceType.TAG.getIndex());
			JSONArray tag = new JSONArray();
			tag.add(transportOrder.getDriverId().toString());
			json.put("tag", tag);
			push.setAudience(json.toJSONString());
			push.setNotificationType(0);
			push.setRecipient(transportOrder.getDriverId());
			push.setContent("您有新的运单，请注意查收！");
			push.setTitle("您有新的运单，请注意查收！");
			push.setPushCount(1);
			push.setPushType(0);
			push.setFlag(0);
			pushService.addPush(push);
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
	public void updateCancelTransmit(Long transportOrderId) {
		log.debug("updateCancelTransmit:{}",transportOrderId);
		TransportOrder transportOrder = null,updateTransportOrder;
		List<WarehouseLoadingPlan> warehouseLoadingPlans = null;
		StringBuilder s;
		Map<String, Object> map ;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrderId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			updateTransportOrder = new TransportOrder();
			transportOrder = queryById(transportOrderId);
			updateTransportOrder.setTransportOrderId(transportOrderId);
			updateTransportOrder.setTransmitDate(null);
			updateTransportOrder.setSalesmanId(null);
			updateTransportOrder.setBillStatus(TransportOrderBillStatus.CHECK.getIndex());
			if(transportOrder.getBillStatus() == TransportOrderBillStatus.CHECK.getIndex()) {
				throw new BusinessException("此运单并未下达，无需取消下达！");
			}
			transportordermapper.updateByTransmit(updateTransportOrder);
		 	//维护运单结算主表信息
			/*transportSettlementService.transmit(updateTransportOrder,0);*/
			//维护运单子表车辆状态
			updateCarStatus(updateTransportOrder.getTransportOrderId(),4,0);
			//维护仓库装车计划数据:状态为0=待定
			map = new HashMap<>();
			map.put("transportOrderId", updateTransportOrder.getTransportOrderId());
			warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
			for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
				warehouseLoadingPlan.setBillStatus(0);
				warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
			}
			if(transportOrder.getBillStatus().intValue() >= TransportOrderBillStatus.SEND.getIndex()){
				//维护极光推送
				Push push = new Push();
				JSONObject json = new JSONObject();
				push.setPlatformType(PlatformType.ANDROID.getIndex());
				push.setAudienceType(AudienceType.TAG.getIndex());
				JSONArray tag = new JSONArray();
				tag.add(transportOrder.getDriverId().toString());
				json.put("tag", tag);
				push.setAudience(json.toString());
				push.setNotificationType(0);
				push.setRecipient(transportOrder.getDriverId());
				push.setContent("您好，您所拥有得运单被取消，请联系业务员");
				push.setTitle("您好，您所拥有得运单被取消，请联系业务员");
				push.setPushCount(1);
				push.setPushType(0);
				push.setFlag(0);
				pushService.addPush(push);
			}

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
	public void updateBill(Long transportOrderId) {
		log.debug("updateBill:{}",transportOrderId);
		StringBuilder s;
		TransportOrder transportOrder;
		List<WarehouseLoadingPlan> warehouseLoadingPlans;
		List<LoadingReservation> loadingReservations;
		Map<String, Object> map;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrderId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
	       	updateCarStatus(transportOrderId,0,0);
			//维护仓库装车计划数据
			map = new HashMap<>();
			map.put("transportOrderId", transportOrderId);
			warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
			for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
				warehouseLoadingPlan.setBillStatus(0);
				warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
			}
			//维护预约
			loadingReservations = loadingReservationService.queryList(map);
			for (LoadingReservation loadingReservation : loadingReservations) {
				loadingReservation.setBillStatus(0);
				loadingReservationService.updateSelective(loadingReservation);
			}
	       	transportOrder = queryById(transportOrderId);
	    	//维护运单结算主表信息
			/*transportSettlementService.bill(transportOrder);*/
	        //维护运单状态:5=取消计划
			Integer billStatus = transportOrder.getBillStatus();
			transportOrder.setBillStatus(TransportOrderBillStatus.CANCELPLAN.getIndex());
			updateSelective(transportOrder);
			if(billStatus >= TransportOrderBillStatus.SEND.getIndex()) {
				//维护极光推送
				Push push = new Push();
				JSONObject json = new JSONObject();
				push.setPlatformType(PlatformType.ANDROID.getIndex());
				push.setAudienceType(AudienceType.TAG.getIndex());
				JSONArray tag = new JSONArray();
				tag.add(transportOrder.getDriverId().toString());
				json.put("tag", tag);
				push.setAudience(json.toString());
				push.setNotificationType(0);
				push.setRecipient(transportOrder.getDriverId());
				push.setContent("您好，您所拥有得运单被取消，请联系业务员!");
				push.setTitle("您好，您所拥有得运单被取消，请联系业务员!");
				push.setPushCount(1);
				push.setPushType(0);
				push.setFlag(0);
				pushService.addPush(push);
			}
			//刪除交接单
			transferOrderService.deleteByTransportOrderId(transportOrderId);
		} catch (Exception e) {
		    if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public List<TransportOrder> queryByDriverId(Map<String, Object> map) {
		log.debug("queryByDriverId:{}",map);
		StringBuilder s;
		List<TransportOrder> transportOrders = null;
		Long driverId = null;
		try {
			 s = new StringBuilder(); 
			 if(map.get("driverId") != null) {
				 driverId = Long.valueOf(map.get("driverId").toString());
				 s.append(CheckUtils.checkLong("NO", "driverId", "司机Id",driverId, 20));
		            if (!StringUtils.isBlank(s)) {
		                 throw new BusinessException("数据检查失败：" + s.toString());
		            } 
	    		driverService.queryById(driverId);
				transportOrders = transportordermapper.queryByDriverId(map);
			 }else {
				 throw new BusinessException("传入参数不正确！");
			 }
	
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		
		return transportOrders;
	}

	@Override
	public List<TransportOrder> querySettlementByDriverId(Map<String, Object> map) {
		log.debug("queryByDriverId:{}",map);
		StringBuilder s;
		List<TransportOrder> transportOrders = null;
		Long driverId = null;
		try {
			s = new StringBuilder();
			if(map.get("driverId") != null) {
				driverId = Long.valueOf(map.get("driverId").toString());
				s.append(CheckUtils.checkLong("NO", "driverId", "司机Id",driverId, 20));
				if (!StringUtils.isBlank(s)) {
					throw new BusinessException("数据检查失败：" + s.toString());
				}
				transportOrders = transportordermapper.queryByDriverId(map);
				transportOrders.forEach(transportOrder ->
						transportOrder.setDetails( queryById(transportOrder.getTransportOrderId()).getDetails()));
			}else {
				throw new BusinessException("传入参数不正确！");
			}

		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transportOrders;
	}

	@Override
	public void updateRrriveDateBesides(List<TransportOrderDetail> transportOrderDetails) {
		log.debug("updateRrriveDateBesides:{}",transportOrderDetails);
		TransportOrderDetail transportOrderDetail = null;
		try {
			for (TransportOrderDetail detail : transportOrderDetails) {
				transportOrderDetail = new TransportOrderDetail();
				transportOrderDetail.setTransportOrderDetailId(detail.getTransportOrderDetailId());
				transportOrderDetail.setTransportOrderId(detail.getTransportOrderId());
				/*if(detail.getArriveDateBesides() == null ) {
					throw new BusinessException("请输入有效的到店时间!");
				}else if(detail.getArriveDateBesides() != null){
					transportOrderDetail.setArriveDateBesides(detail.getArriveDateBesides());
					transportOrderDetailService.updateSelective(transportOrderDetail);
				}*/
				transportOrderDetail.setArriveDateBesides(detail.getArriveDateBesides());
				transportOrderDetailService.updateSelective(transportOrderDetail);
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
	public void updateCarStatus(Long transportOrderId,Integer carStatus,Integer type) {
		log.debug("updateCarStatus:{}",transportOrderId);
		List<TransportOrderDetail> details = new ArrayList<>();
		List<TransportOrderDetail> transportOrderDetails = null;
		TransportOrder transportOrder = null;
		StockCarTrans stockCarTrans;
		StringBuilder error;
		List<StockCarTrans> addStockCarTrans = new ArrayList<StockCarTrans>();
		List<StockCar> updateCarStatusStockCars = new ArrayList<StockCar>();
		StockCar stockCar = null;
		OrderDetail orderDetail = null;
		try {
			error = new StringBuilder();
			error.append(CheckUtils.checkLong("NO", "transportOrderId", "运单主表Id", transportOrderId, 20));
			if (!StringUtils.isBlank(error)) {
				throw new BusinessException("数据检查失败：" + error.toString());
			}
			transportOrder = queryById(transportOrderId);
			transportOrderDetails = Optional.ofNullable(transportOrder.getDetails()).orElse(new ArrayList<TransportOrderDetail>());
			Long carriageCorpId = transportOrder.getCarriageCorpId();
			//维护商品车库存表:要将车辆状态修改为预排确认。
			for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
				stockCar = new StockCar();
				if(carStatus.intValue() != transportOrderDetail.getCarStatus().intValue()){
					transportOrderDetail.setCarStatus(carStatus);
					transportOrderDetail.setRowStatus(2);
					/*transportOrderDetailService.updateSelective(transportOrderDetail);*/
					details.add(transportOrderDetail);
				}
				stockCar.setCarVin(transportOrderDetail.getCarVin());
				//商品车库存表所在单位等于运单子表的发货单位
				stockCar.setCarrierId(transportOrder.getCarriageCorpId());
				stockCar = Optional.ofNullable(stockCarService.queryByStockCar(stockCar))
						.orElseThrow(() -> new BusinessException("运单号：" + transportOrderDetail.getTransportOrderCode() + "查询商品车库存数据失败!请维护好运单子表的承运商ID：" + carriageCorpId + "车架号:" + transportOrderDetail.getCarVin() + "!"));
				/*//维护订单车辆状态
				orderDetail = new OrderDetail();*/
				if(carStatus.intValue() == CarStatus.Confirm.getIndex()) {
					stockCarTrans = new StockCarTrans();
					if(stockCar != null) {
						stockCar.setCarStatus(CarStatus.Confirm.getIndex());
						updateCarStatusStockCars.add(stockCar);
						/*stockCarService.updateCarStatus(stockCar, CarStatus.In.getIndex());*/
		/*				orderDetail.setOrderDetailId(stockCar.getOrderDetailId());
						orderDetail.setCarStatus(carStatus);
						orderDetailService.updateSelective(orderDetail);*/
						//维护商品车运输记录表：若商品车库存id重复则更新，没有则新增。
						stockCarTrans.setStockCarId(stockCar.getStockCarId());
						stockCarTrans.setCarVin(stockCar.getCarVin());
						stockCarTrans.setTransportOrderCode(transportOrder.getTransportOrderCode());
						stockCarTrans.setTransportOrderId(transportOrder.getTransportOrderId());
						stockCarTrans.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
						stockCarTrans.setJobType(transportOrder.getJobType());
						addStockCarTrans.add(stockCarTrans);
						/*stockCarTransService.add(stockCarTrans);*/
						if(type == 1) {
							//维护商品车运输记录表：删除
							/*stockCarTransService.logicDeleteByStockCarId(stockCar.getStockCarId());*/
						}
					}else {
						throw new BusinessException("运单号："+transportOrderDetail.getTransportOrderCode()+"查询商品车库存数据失败!请维护好运单子表的承运商ID："+transportOrder.getCarriageCorpId()+"车架号:"+transportOrderDetail.getCarVin()+"!");
					}
				}else if(carStatus.intValue() == CarStatus.Advance.getIndex() || carStatus.intValue() == CarStatus.In.getIndex()
						|| carStatus.intValue() >= CarStatus.ToDriver.getIndex() && carStatus.intValue() <= CarStatus.Arrived.getIndex()) {
					if(stockCar != null) {
						stockCarTrans = new StockCarTrans();
						stockCar.setCarStatus(carStatus);
						updateCarStatusStockCars.add(stockCar);
						/*stockCarService.updateCarStatus(stockCar, CarStatus.In.getIndex());*/
/*						orderDetail.setOrderDetailId(stockCar.getOrderDetailId());
						orderDetail.setCarStatus(carStatus);
						orderDetailService.updateSelective(orderDetail);*/
						//维护商品车运输记录表：若商品车库存id重复则更新，没有则新增。
						stockCarTrans.setStockCarId(stockCar.getStockCarId());
						stockCarTrans.setCarVin(stockCar.getCarVin());
						stockCarTrans.setTransportOrderCode(transportOrder.getTransportOrderCode());
						stockCarTrans.setTransportOrderId(transportOrder.getTransportOrderId());
						stockCarTrans.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
						stockCarTrans.setJobType(transportOrder.getJobType());
						addStockCarTrans.add(stockCarTrans);
						/*stockCarTransService.add(stockCarTrans);*/
						if(type == 1) {
							//维护商品车运输记录表：删除
							stockCarTransService.logicDeleteByStockCarId(stockCar.getStockCarId());
						}
					}else {
						throw new BusinessException("运单号："+transportOrderDetail.getTransportOrderCode()+"查询商品车库存数据失败!请维护好运单子表的承运商ID："+transportOrder.getCarriageCorpId()+"车架号:"+transportOrderDetail.getCarVin()+"!");
					}
				}
			}
			transportOrderDetailService.updateSelective(details);
			stockCarService.updateCarStatus(updateCarStatusStockCars,0);
			stockCarTransService.add(addStockCarTrans);
		} catch (Exception e) {
			log.debug("e:",e.getMessage());
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void updateExamination(TransportOrder transportOrder) {
		try {
			transportordermapper.updateByPrimaryKeySelective(transportOrder);
		} catch (Exception e) {
			log.debug("e:",e.getMessage());
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		
	}

	@Transactional
	@Override
	public void updateCancelTransmit(List<String> carVins) {
		log.debug("updateCancelTransmit:{}",carVins);
		List<TransportOrderDetail> transportOrderDetails = null;
		List<TransportOrder> transportOrders = null;
		TransportOrder transportOrder = null;
		List<WarehouseLoadingPlan> warehouseLoadingPlans = null;
		Map<String, Object> map ,mapCarVIn,mapOrder;
		StockCar stockCar;
		StockCarTrans stockCarTrans;
		try {
			if (StringUtils.isBlank(carVins.toString())) {
				throw new BusinessException("数据检查失败:车架号为"+carVins);
			}
			mapCarVIn = new HashMap<>();
			transportOrderDetails = new ArrayList<>();
			for (String carVin : carVins) {
				mapCarVIn.put("carVin", carVin);
				if(transportOrderDetailService.queryList(mapCarVIn) != null){
					transportOrderDetails.addAll(transportOrderDetailService.queryList(mapCarVIn));
				}
			}
			mapOrder = new HashMap<>();
			for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
				mapOrder.put("transportOrderDetailId", transportOrderDetail.getTransportOrderDetailId());
				stockCar = new StockCar();
				transportOrderDetail.setCarStatus(4);
				transportOrderDetail.setRowStatus(2);
				transportOrderDetailService.updateSelective(transportOrderDetail);
				stockCar.setCarVin(transportOrderDetail.getCarVin());
				transportOrders = queryList(mapOrder);
				if(transportOrders.size() > 0) {
					transportOrder = new TransportOrder();
					transportOrder.setTransportOrderId(transportOrders.get(0).getTransportOrderId());
					transportOrder.setCarriageCorpId(transportOrders.get(0).getCarriageCorpId());
					transportOrder.setJobType(transportOrders.get(0).getJobType());
					transportOrder.setTransportOrderCode(transportOrders.get(0).getTransportOrderCode());
					//商品车库存表所在单位等于运单子表的发货单位
					stockCar.setCarrierId(transportOrder.getCarriageCorpId());
					stockCar = stockCarService.queryByStockCar(stockCar);
					//维护订单车辆状态
					stockCarTrans = new StockCarTrans();
					if(stockCar != null) {
						stockCar.setCarStatus(4);
						stockCarService.updateCarStatus(stockCar, 0);
						//维护商品车运输记录表：若商品车库存id重复则更新，没有则新增。
						stockCarTrans.setStockCarId(stockCar.getStockCarId());
						stockCarTrans.setCarVin(stockCar.getCarVin());
						stockCarTrans.setTransportOrderCode(transportOrder.getTransportOrderCode());
						stockCarTrans.setTransportOrderId(transportOrder.getTransportOrderId());
						stockCarTrans.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
						stockCarTrans.setJobType(transportOrder.getJobType());
						stockCarTransService.add(stockCarTrans);
					}else {
						throw new BusinessException("查询商品车库存数据失败!请维护好运单子表的承运商ID："+transportOrder.getCarriageCorpId()+"车架号:"+transportOrderDetail.getCarVin()+"!");
					}
				}
			}
			map = new HashMap<>();
			transportOrderDetails = transportOrderDetails.stream().collect(collectingAndThen( toCollection(() -> new TreeSet<>(comparingLong(TransportOrderDetail::getTransportOrderId))), ArrayList::new));
			for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
				map.put("transportOrderId", transportOrderDetail.getTransportOrderId());
				warehouseLoadingPlans = warehouseLoadingPlanService.queryList(map);
				for (WarehouseLoadingPlan warehouseLoadingPlan : warehouseLoadingPlans) {
					warehouseLoadingPlan.setBillStatus(4);
					warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
				}
				//维护极光推送
				Push push = new Push();
				JSONObject json = new JSONObject();
				push.setPlatformType(PlatformType.ANDROID.getIndex());
				push.setAudienceType(AudienceType.TAG.getIndex());
				JSONArray tag = new JSONArray();
				tag.add(transportOrder.getDriverId().toString());
				json.put("tag", tag);
				push.setAudience(json.toString());
				push.setNotificationType(0);
				push.setRecipient(transportOrder.getDriverId());
				push.setContent("您好，您所拥有得车架号"+ "\""+transportOrderDetail.getCarVin()+"\""+"被取消，请联系业务员");
				push.setTitle("您好，您所拥有得车架号"+ "\""+transportOrderDetail.getCarVin()+"\""+"被取消，请联系业务员");
				push.setPushCount(1);
				push.setPushType(0);
				push.setFlag(0);
				pushService.addPush(push);
			}

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
	public void importAssembly(MultipartFile file) throws Exception {
		log.debug("importAssembly:{}",file);
		String filePath;
		try {
			filePath = FileUtil.uploadFile(file, "OutsourcingReconciliation");
			Workbook workbook = ExcelUtil.getWorkbook(filePath);
			parseExcelForTransportOrder(workbook);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}


	}

	TransportOrderDetail setDetail(TransportOrderDetail detail){
		Environment env = Environment.getEnv();
		Integer examination = Optional.ofNullable(detail.getExamination()).orElse(0);
		Integer urgent = Optional.ofNullable(detail.getUrgent()).orElse(0);
		Integer flag = Optional.ofNullable(detail.getFlag()).orElse(0);
		Integer carStatus = Optional.ofNullable(detail.getCarStatus()).orElse(CarStatus.Advance.getIndex());
		detail.setCarStatus(carStatus);
		detail.setCorpId(env.getCorp().getCorpId());
		detail.setFlag(flag);
		detail.setUrgent(urgent);
		detail.setExamination(examination);
		return  detail;
	}

	TransportOrder setTransportOrder(TransportOrder transportOrder){
		Environment env = Environment.getEnv();
		transportOrder.setCorpId(env.getCorp().getCorpId());
		Integer jobType = Optional.ofNullable(transportOrder.getJobType()).orElse(0);
		Integer examination = Optional.ofNullable(transportOrder.getExamination()).orElse(0);
		Integer urgent = Optional.ofNullable(transportOrder.getUrgent()).orElse(0);		//加急标志：0=否 1=是
		Double predictKm = Optional.ofNullable(transportOrder.getPredictKm()).orElse(0.00);	//	预计公里数
		Double practicalKm = Optional.ofNullable(transportOrder.getPracticalKm()).orElse(0.00);  //实际公里数
		Integer billStatus  = Optional.ofNullable(transportOrder.getBillStatus()).orElse(0);  //运单状态:0=制单 1=审核 2=已发送 3=已接单 4=在途 5=取消计划 6=关闭
		Long creator = Optional.ofNullable(transportOrder.getCreator()).orElse(env.getUser().getUserId());   //制单人
		Integer settleType = Optional.ofNullable(transportOrder.getSettleType()).orElse(0);   	//结算模式
		Integer settleStatus  = Optional.ofNullable(transportOrder.getSettleStatus()).orElse(0);	//结算状态(0=未结算,1=已结算)
        transportOrder.setJobType(jobType);
        transportOrder.setExamination(examination);
        transportOrder.setUrgent(urgent);
        transportOrder.setPredictKm(predictKm);
        transportOrder.setPracticalKm(practicalKm);
        transportOrder.setBillStatus(billStatus);
        transportOrder.setCreator(creator);
        transportOrder.setSettleType(settleType);
        transportOrder.setSettleStatus(settleStatus);
        transportOrder.setReceivingDate(new Date());
		transportOrder.setCreateDate(new Date());
		return  transportOrder;
	}
	public  void  parseExcelForTransportOrder (Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheetAt(0);
		Map<String,Object> queryMap = new HashMap<>();
        List<OrderDetail> orderDetails = orderDetailService.queryList(queryMap);	//	订单子表信息
        List<Order> orders = orderService.queryList(queryMap);		//	订单主表信息
        List<TransportOrder> transportOrders = new ArrayList<TransportOrder>();	//	运单主表
        List<TransportOrderDetail> details = new ArrayList<TransportOrderDetail>();	//	运单子表
        Map<String,OrderDetail> orderDetailMap = new HashMap<>();
        Map<Long,Order> orderMap = new HashMap<>();
        Map<String,StockCar> stockCarMap = new HashMap<>();
        orderDetails.forEach(orderetail -> orderDetailMap.put(orderetail.getCarVin(),orderetail));
        orders.forEach(order -> orderMap.put(order.getOrderId(),order));
        int colCount = 14;
        int rowCount = sheet.getLastRowNum();

        Integer number = null;
        String phoneNumber = null;

		Set<String> unitNames = new HashSet<>();
		Set<String> carNames = new HashSet<>();
		Set<String> brandNames = new HashSet<>();

        for (int i = 0; i <= rowCount; i++) {

			if ("序号".equals(sheet.getRow(i).getCell(0).getStringCellValue())) {
				number = NumberUtils.toInt(sheet.getRow(i + 1).getCell(0).getStringCellValue());
				phoneNumber = sheet.getRow(i + 1).getCell(0).getStringCellValue();
				continue;
			}

			Cell[] cells = new Cell[colCount + 1];
			for (int j = 0; j <= colCount; j++) {
				cells[j] = sheet.getRow(i).getCell(j);
				cells[j].setCellType(CellType.STRING);
			}


			String productionNumber = cells[1].getStringCellValue();
			String carBrandName     = cells[2].getStringCellValue();
			String carTypeName      = cells[3].getStringCellValue();
			String carVin           = cells[4].getStringCellValue();
			String dealerName       = cells[5].getStringCellValue();
			Date sendDate           = DateUtils.parseDate(cells[6].getStringCellValue(), "yyyy-MM-dd");
			String province         = cells[7].getStringCellValue();
			String city             = cells[8].getStringCellValue();
			String truckNumber      = cells[9].getStringCellValue();
			String driverInfo       = cells[10].getStringCellValue();
			String dock             = cells[12].getStringCellValue();
			Integer count           = NumberUtils.toInt(cells[13].getStringCellValue());
			String remark           = cells[14].getStringCellValue();
			TransportOrderDetail detail = new TransportOrderDetail();
			OrderDetail orderDetail = Optional.ofNullable(orderDetailMap.get(carVin))
					.orElseThrow(() -> new BusinessException("商品车车架号：" + carVin + "在订单明细没有找到数据，请维护订单明细！"));
			Order order = Optional.ofNullable(orderMap.get(orderDetail.getOrderId()))
					.orElseThrow(() -> new BusinessException("商品车车架号：" + carVin + "在订单主表没有找到数据，请维护订单主表！"));
			StockCar stockCar = Optional.ofNullable(stockCarMap.get(carVin))
					.orElseThrow(() -> new BusinessException("商品车车架号：" + carVin + "在商品车库存档案没有相关车架号找到数据，请维护商品车库存档案！"));

			detail.setSendCityId(orderDetail.getSendCityId());
			detail.setSendUnitId(orderDetail.getSendUnitId());
			detail.setReceiveCityId(orderDetail.getReceiveCityId());
			detail.setReceiveUnitId(orderDetail.getReceiveUnitId());
			detail.setCarId(orderDetail.getCarId());
			detail.setCarVin(carVin);
			detail.setClaimLoadingDate(sendDate);
			detail.setTransportOrderId(Long.valueOf(number));
			detail.setClaimDeliveryDate(DateUtil.getDate(detail.getClaimLoadingDate(),7));
			detail = setDetail(detail);
			details.add(detail);
		}
        Map<Long,TransportOrderDetail> detailMap = new HashMap<>();
        details.forEach(transportOrderDetail -> detailMap.put(transportOrderDetail.getTransportOrderId(),transportOrderDetail));
        detailMap.entrySet().forEach(entry ->{
            TransportOrder transportOrder = new TransportOrder();
            transportOrder.setTransportOrderId(entry.getKey());
            transportOrder.setTransportOrderCode(DateUtil.dateToStringCode(new Date()));
            StockCar stockCar1 = Optional.ofNullable(stockCarMap.get(entry.getValue().getCarVin()))
                    .orElseThrow(() -> new BusinessException("商品车车架号：" + entry.getValue().getCarVin() + "在商品车库存档案没有找到承运公司数据，请维护商品车库存档案！"));
            transportOrder.setCarriageCorpId(stockCar1.getCarrierId());
            List<TransportOrderDetail> collect = details.stream().filter(transportOrderDetail -> entry.getKey().equals(transportOrderDetail.getTransportOrderId())).collect(Collectors.toList());
            transportOrder.setDetails(collect);
            transportOrder = setTransportOrder(transportOrder);
            transportOrders.add(transportOrder);
        });
        add(transportOrders);
    }

	/**
     * 填充运单明细数据的客户报价金额，字段：CustomerPrice
     *
     * @since 1.0.0
     * @param order     运单对象，包含运单明细
     * @return void
     */
    @Override
	public void getCustomerPrice(TransportOrder order){
		if(order == null){
			throw new BusinessException("运单为空！");
		}
		if(CollectionUtils.isEmpty(order.getDetails())){
			throw new BusinessException("运单明细为空！");
		}
        List<TransportOrderDetail> details = order.getDetails();
		List<String> vins = new ArrayList<>();
        details.forEach( detail -> vins.add(detail.getCarVin()));
        // 获取库存信息
        List<StockCar> stockCars = stockCarService.queryByVins(vins,order.getCarriageCorpId());
        List<Long> orderDetailIds = new ArrayList<>();
        stockCars.forEach( stockCar -> orderDetailIds.add(stockCar.getOrderDetailId()));
        //获取入库单
        List<OrderDetail> orderDetails = orderService.queryDetailByDetailIds(orderDetailIds);
        // 填充报价
        Map<String,OrderDetail> map = new HashMap<>();
        orderDetails.forEach( orderDetail -> map.put(orderDetail.getCarVin(),orderDetail));
        for (TransportOrderDetail detail : details) {
            detail.setCustomerPrice(map.get(detail.getCarVin()).getCustomerQuotedPrice());
        }
    }

	@Override
	@Transactional
	public PageInfo<TransportOrder> getByOrderId(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("getByOrderId: {}, page: {},{}", map,pageNum,pageSize);
		try {
			
			PageHelper.startPage(pageNum, pageSize);
			if (map.get("orderId")==null) {
				return new PageInfo<TransportOrder>(null);
			}
			long orderId=Long.parseLong(map.get("orderId").toString());
			Order order=orderService.queryById(orderId);
			if (order==null) {
				throw new BusinessException("订单不存在");
			}
			
			return new PageInfo<TransportOrder>(transportordermapper.getByOrderId(orderId));
		} catch (Exception e) {
			throw e;
		}
	}




}
