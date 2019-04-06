package com.tilchina.timp.service.impl;

import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportRecordMapper;

/**
* app:运输记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class AppTransportRecordrServiceImpl implements AppTransportRecordService {
	
	private static String driverspeed = PropertiesUtils.getString("driverspeed");
	
	@Autowired
    private TransportRecordMapper transportrecordmapper;
	
	@Autowired
    private TransportRecordOuterService transportRecordOuterService;

	@Autowired
	private CarService carService;
	
	@Autowired
    private TransportPlanService transportPlanService;
    
	protected BaseMapper<TransportRecord> getMapper() {
		return transportrecordmapper;
	}
	@Autowired
    private TransportOrderService transportOrderService;

	protected StringBuilder checkNewRecord(TransportRecord transportrecord) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "transportOrderCode", "运单号", transportrecord.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkDate("NO", "reportDate", "上报时间", transportrecord.getReportDate()));
        s.append(CheckUtils.checkInteger("NO", "carNumber", "商品车数量", transportrecord.getCarNumber(), 10));
        s.append(CheckUtils.checkInteger("NO", "transportStatus", "状态:0=行驶,1=停靠", transportrecord.getTransportStatus(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(TransportRecord transportrecord) {
        StringBuilder s = checkNewRecord(transportrecord);
        s.append(CheckUtils.checkPrimaryKey(transportrecord.getTransportRecordId()));
		return s;
	}
	

    @Override
    public void add(TransportRecord transportRecord) {
        log.debug("add: {}",transportRecord);
        StringBuilder s;
        Environment env = Environment.getEnv();
        Map<String, Object> map;
        List<TransportOrder> transportOrders = null;
        List<TransportPlan> transportPlans;
        Integer carNumber = 0;
        try {
            transportRecord.setDriverId(env.getUser().getUserId());
            transportRecord.setCorpId(env.getCorp().getCorpId());
            transportRecord.setReportDate(new Date());
            if(transportRecord.getSpeed() > Double.valueOf(driverspeed)) {
            	transportRecord.setTransportStatus(0);
            }else {
            	transportRecord.setTransportStatus(1);
            }
            map = new HashMap<>();
			//驾驶员
			map.put("driverId", transportRecord.getDriverId());
			if(transportRecord.getTransporterId() == null) {
				 throw new BusinessException("没有轿运车信息");
			}
			//轿运车
			map.put("transporterId", transportRecord.getTransporterId());
			transportOrders = transportOrderService.queryList(map);
			//运单信息:运单id、运单编号
			for (TransportOrder transportOrder : transportOrders) {
				if(transportOrder.getBillStatus().intValue() == 4) {
					carNumber = 0;
					transportRecord.setCarNumber(carNumber);
					transportRecord.setTransportOrderId(transportOrder.getTransportOrderId());
					transportRecord.setTransportOrderCode(transportOrder.getTransportOrderCode());
					map.put("transportOrderId", transportRecord.getTransportOrderId());
					//装载商品车数量
					transportPlans = transportPlanService.queryList(map);
					for (TransportPlan transportPlan : transportPlans) {
						carNumber += transportPlan.getCarCount();
					}
					Double carWeight = new Double(0.00);
					 List<TransportOrderDetail> transportOrderDetails = Optional.ofNullable(transportOrder.getDetails()).orElse(new ArrayList<>());
					for (TransportOrderDetail transportOrderDetail : transportOrderDetails){
						carService.queryById(transportOrderDetail.getCarId());
						Car car = Optional.ofNullable(carService.queryById(transportOrderDetail.getCarId())).orElse(new Car());
						carWeight += car.getCarWeight();
					}
					s = checkNewRecord(transportRecord);
		            if (!StringUtils.isBlank(s)) {
		                throw new RuntimeException("数据检查失败：" + s.toString());
		            }
					transportRecord.setCarNumber(carNumber);
					transportRecord.setCurrentLoadWeight(carWeight);
		            getMapper().insertSelective(transportRecord);
				}
			}
			if(transportRecord.getTransportOrderId() == null && transportRecord.getTransportOrderCode() == null) {
				transportRecord.setCarNumber(carNumber);
				getMapper().insertSelective(transportRecord);
			}
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		
        }
    }

}
