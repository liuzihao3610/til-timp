package com.tilchina.timp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * App 预约装车
 * @author Xiahong
 *
 */
@Service
@Slf4j
public class AppReservationServiceImpl  implements AppReservationService{
	
	private static String startTimeRange = PropertiesUtils.getString("startTimeRange");
	
	@Autowired
    private LoadingReservationService loadingReservationService;
	
	@Autowired
    private LoadingReservationDetailService loadingReservationDetailService;
	
	@Autowired
    private WarehouseLoadingPlanService warehouseLoadingPlanService;
	
	@Autowired
    private TransportOrderService transportOrderService;
	
	@Autowired
    private TransportOrderDetailService transportOrderDetailService;
	
	@Autowired
    private CarService  carService;
	
	@Transactional
	@Override
	public void appointment(LoadingReservation loadingReservation) {
		log.debug("appointment: {}",loadingReservation);
		List<LoadingReservationDetail>  loadingReservationDetails;
		WarehouseLoadingPlan warehouseLoadingPlan;
		TransportOrderDetail transportOrderDetail;
		Environment env = Environment.getEnv();
		StringBuilder s;
		try {
			s = new StringBuilder();
			//单据状态：预约
			loadingReservation.setBillStatus(1);
	       	s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单ID", loadingReservation.getTransportOrderId(), 20))
	       	 .append(CheckUtils.checkLong("NO", "loadingReservationId", "预约单ID", loadingReservation.getLoadingReservationId(), 20))
	       	 .append(CheckUtils.checkDate("NO", "loadingTime", "预约装车时间", loadingReservation.getLoadingTime()));
	   	    if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	        }
	   	    loadingReservationService.updateSelective(loadingReservation);
	   	    loadingReservationDetails = loadingReservationDetailService.queryList(loadingReservation.getLoadingReservationId());
	   	    //维护仓库装车计划
	   	    for (LoadingReservationDetail detail : loadingReservationDetails) {
	   	    	warehouseLoadingPlan = warehouseLoadingPlanService.queryByCarVin(detail.getCarVin(), detail.getTransportOrderId());
		   	 	if(warehouseLoadingPlan == null) {
		   	 		warehouseLoadingPlan = new WarehouseLoadingPlan();
		   	 		warehouseLoadingPlan.setBillStatus(2);
		   	 		transportOrderDetail = transportOrderDetailService.queryByCarVin(detail.getCarVin(),loadingReservation.getTransportOrderId());
		   	 		if(transportOrderDetail != null) {
		   	 			warehouseLoadingPlan.setPlanDate(transportOrderDetail.getClaimLoadingDate());
		   	 		}
		   	 		//缺少计划日期
		   	 		warehouseLoadingPlan.setTransportOrderId(loadingReservation.getTransportOrderId());
			   	 	warehouseLoadingPlan.setTransportOrderCode(loadingReservation.getTransportOrderCode());
				   	warehouseLoadingPlan.setUnitId(loadingReservation.getUnitId());
				   	warehouseLoadingPlan.setCarId(detail.getCarId());
				   	warehouseLoadingPlan.setCarVin(detail.getCarVin());
				   	warehouseLoadingPlan.setDriverId(loadingReservation.getDriverId());
				   	warehouseLoadingPlan.setTransporterId(loadingReservation.getTransporterId());
				   	warehouseLoadingPlan.setRemark(detail.getRemark());
				   	warehouseLoadingPlan.setCarId(env.getCorp().getCorpId());
		   	 	}else {
		   	    	warehouseLoadingPlan.setCarVin(detail.getCarVin());
		   	    	warehouseLoadingPlan.setTransportOrderId(detail.getTransportOrderId());
		   	    	warehouseLoadingPlan.setLoadingLocation(loadingReservation.getLoadingLocation());
		   	    	warehouseLoadingPlan.setBillStatus(1);
		   	    	warehouseLoadingPlanService.updateSelective(warehouseLoadingPlan);
		   	 	}
			}
			//维护运单车架号状态状态
			transportOrderService.updateCarStatus(loadingReservation.getTransportOrderId(), 7, 0);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
			
		}
	
	@Override
	public List<LoadingReservation> queryList() {
		Map<String, Object> mapOrder,mapReq;
		List<TransportOrder> listOrder = null;
		LoadingReservationDetail detail = null;
		List<LoadingReservation> reservations = null,loadingReservations = null;
		List<LoadingReservationDetail> loadingReservationDetails = null;
		LoadingReservation loadingReservation;
		List<Car> cars = null;
		Car car= null;
		Environment env = Environment.getEnv();
		try {
			mapOrder = new HashMap<>();
			/*mapReq.put("appBillStatus", "get");*/
			mapOrder.put("appBillStatus", "get");
			mapOrder.put("driverId",env.getUser().getUserId());
			/*mapOrder.put("driverId", 587);*/
			//运单主表
			listOrder = transportOrderService.queryByDriverId(mapOrder);
			reservations = new ArrayList<>();
			for (TransportOrder transportOrder : listOrder) {
				mapReq = new HashMap<>();
				mapReq.put("transportOrderId",transportOrder.getTransportOrderId());
				mapReq.put("driverId",env.getUser().getUserId());
				/*mapReq.put("driverId", 587);*/
				//根据运单id查询预约装车主表
				loadingReservations = loadingReservationService.queryList(mapReq);
				for (int j = 0; j < loadingReservations.size(); j++) {
					loadingReservation = loadingReservations.get(j);
					mapReq.put("loadingReservationId",loadingReservation.getLoadingReservationId());
					//预约装车主表 id 查询满足条件的子表
					loadingReservationDetails = loadingReservationDetailService.queryList(mapReq);
					cars = new ArrayList<Car>();
					//预约装车子表 carId 查询满足条件的商品车信息
					for (int i = 0; i < loadingReservationDetails.size(); i++) {
						detail = loadingReservationDetails.get(i);
						if(detail.getLoadingReservationId().equals(loadingReservation.getLoadingReservationId())) {
							car = carService.queryById(detail.getCarId());
							car.setAppCarVin(detail.getCarVin());
							cars.add(car);
							if( i == loadingReservationDetails.size()-1) {
								detail.setCars(cars);
								loadingReservation.setSendUnits(loadingReservationDetails);
								reservations.add(loadingReservation);
								}
							}
						}
					}
				}
		} catch (Exception e) {

			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("预约记录查询失败！", e);
			}
		}
		return reservations;
	}
	
	@Override
	public List<LoadingReservation> queryList(Map<String, Object> mapOrder) {
		log.debug("queryList: {}",mapOrder);
		Map<String, Object> mapReq;
		List<TransportOrder> listOrder = null;
		List<LoadingReservation> reservations = null,loadingReservations = null;
		List<LoadingReservationDetail> loadingReservationDetails = null;
		LoadingReservationDetail detail = null;
		List<Car> cars = null;
		Car car = null;
		LoadingReservation loadingReservation;
		Environment env = Environment.getEnv();
		try {
			mapReq = new HashMap<>();
			if(mapOrder.get("startTimeRange") == null || mapOrder.get("endTimeRange") == null) {
				mapReq.put("startTimeRange",DateUtil.getPastDate(14));
				mapReq.put("endTimeRange", DateUtil.getPastDate(0));
			}else {
				mapReq.put("startTimeRange", mapOrder.get("startTimeRange"));
				mapReq.put("endTimeRange", mapOrder.get("endTimeRange"));
			}
			//当前司机
			mapOrder.put("driverId",env.getUser().getUserId());
			/*mapOrder.put("driverId",587);*/
			mapOrder.put("appBillStatus","query");
			//运单主表
			listOrder = transportOrderService.queryByDriverId(mapOrder);
			reservations = new ArrayList<>();
			for (TransportOrder transportOrder : listOrder) {
				mapReq.put("transportOrderId",transportOrder.getTransportOrderId());
				mapReq.put("driverId",env.getUser().getUserId());
				/*mapReq.put("driverId", 587);*/
				//根据运单id查询预约装车主表
				loadingReservations = loadingReservationService.queryList(mapReq);
				for (int j = 0; j < loadingReservations.size(); j++) {
					loadingReservation = loadingReservations.get(j);
					//预约装车主表 id 查询满足条件的子表
					loadingReservationDetails = loadingReservationDetailService.queryByReservationId(loadingReservation.getLoadingReservationId());
					cars = new ArrayList<Car>();
					//预约装车子表 carId 查询满足条件的商品车信息
					for (int i = 0; i < loadingReservationDetails.size(); i++) {
						detail = loadingReservationDetails.get(i);
						if(detail.getLoadingReservationId().equals(loadingReservation.getLoadingReservationId())) {
							car = carService.queryById(detail.getCarId());
							car.setAppCarVin(detail.getCarVin());
							cars.add(car);
							if( i == loadingReservationDetails.size()-1) {
								detail.setCars(cars);
								loadingReservation.setSendUnits(loadingReservationDetails);
								reservations.add(loadingReservation);
								}
							}
						}
					}
				}
		} catch (Exception e) {

			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("历史预约记录查询失败！", e);
			}
		
			
		}
		return reservations;
	}

}
