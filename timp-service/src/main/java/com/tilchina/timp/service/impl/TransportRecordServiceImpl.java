package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.dialect.helper.HsqldbDialect;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.Car;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.model.TransportRecord;
import com.tilchina.timp.model.TransportRecordOuter;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.service.CarService;
import com.tilchina.timp.service.TransportOrderDetailService;
import com.tilchina.timp.service.TransportOrderService;
import com.tilchina.timp.service.TransportRecordOuterService;
import com.tilchina.timp.service.TransportRecordService;
import com.tilchina.timp.service.TransporterService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportRecordMapper;

/**
* 运输记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransportRecordServiceImpl extends BaseServiceImpl<TransportRecord> implements TransportRecordService {

	@Autowired
    private TransportRecordMapper transportrecordmapper;
	
	@Autowired
    private TransportRecordOuterService transportRecordOuterService;
	
	@Autowired
	private TransportOrderService transportOrderService;
	
	@Autowired
	private TransportOrderDetailService transportOrderDetailService;
	
	@Autowired
	private CarService carService;
	
	@Autowired
	private TransporterService transporterService;
	
	
	@Override
	protected BaseMapper<TransportRecord> getMapper() {
		return transportrecordmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransportRecord transportrecord) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "transportOrderCode", "运单号", transportrecord.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkDate("NO", "reportDate", "上报时间", transportrecord.getReportDate()));
        s.append(CheckUtils.checkInteger("NO", "carNumber", "商品车数量", transportrecord.getCarNumber(), 10));
        s.append(CheckUtils.checkInteger("NO", "transportStatus", "状态:0=行驶,1=停靠", transportrecord.getTransportStatus(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportRecord transportrecord) {
        StringBuilder s = checkNewRecord(transportrecord);
        s.append(CheckUtils.checkPrimaryKey(transportrecord.getTransportRecordId()));
		return s;
	}
	
	@Transactional
	@Override
    public void add(TransportRecord record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().insertSelective(record);
            //运输记录表（外）
        	/*
        	 TransportRecordOuter transportRecordOuter = null;
        	transportRecordOuter = new TransportRecordOuter();
        	transportRecordOuter.setTransportOrderId(record.getTransportOrderId());
        	transportRecordOuter.setTransportOrderCode(record.getTransportOrderCode());
        	transportRecordOuter.setDriverId(record.getDriverId());
        	transportRecordOuter.setReportDate(record.getReportDate());
        	transportRecordOuter.setCarNumber(record.getCarNumber());
        	transportRecordOuter.setTransportStatus(record.getTransportStatus());
        	transportRecordOuter.setCorpId(record.getCorpId());
        	transportRecordOuterService.add(transportRecordOuter);*/
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
	public PageInfo<TransportRecord> queryListByCode(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryListByCode: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TransportRecord> pageInfo = null;
        try {
        	if(map.isEmpty()) {
        		throw new BusinessException("9001","传入参数");
        	}
        	pageInfo = new PageInfo<TransportRecord>(transportrecordmapper.selectList(map));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return pageInfo;
	}
	
	//上传位置
	@Override
	@Transactional
	public void upLoad(Map<String, Object> map) {
		log.debug("upLoad{}",map);
		try {
			if (map==null || map.size()==0) {
				throw new BusinessException("参数为空");
			} 
			Map<String, Object> mapDriverId = new HashMap<>();
			
			TransportRecord transportRecord=new TransportRecord();
			Environment environment=Environment.getEnv();
			long driverId=environment.getUser().getUserId();
			transportRecord.setCorpId(environment.getCorp().getCorpId());
			Transporter transporter=transporterService.queryByDriverId(driverId);
			transportRecord.setTransporterId(transporter.getTransporterId());
			map.put("driverId", driverId);
			List<TransportOrder> transportOrders = transportOrderService.queryByDriverId(mapDriverId);
			transportRecord.setDriverId(driverId);
			transportRecord.setReportDate(new Date());
			transportRecord.setLng(Double.parseDouble( map.get("lng").toString()));
			transportRecord.setLat(Double.parseDouble( map.get("lat").toString()));
			if (map.get("speed")!= null && Integer.parseInt(map.get("speed").toString()) != 0) {
				transportRecord.setTransportStatus(0);
			}else if (map.get("speed") == null || Integer.parseInt(map.get("speed").toString()) == 0) {
				transportRecord.setTransportStatus(1);
			}
			if (transportRecord.getTransportStatus().intValue()==0) {
				transportRecord.setCumulativeKm(Double.parseDouble( map.get("cumulativeKm").toString()));
				transportRecord.setSpeed(Double.parseDouble( map.get("speed").toString()));
				transportRecord.setTemperature(Double.parseDouble( map.get("temperature").toString()));
				transportRecord.setHumidity(Double.parseDouble( map.get("humidity").toString()));
				transportRecord.setStress(Double.parseDouble( map.get("stress").toString()));
			}
			
			
			if (transportOrders != null && transportOrders.size() != 0) {
				for (TransportOrder transportOrder : transportOrders) {
					transportRecord.setTransportOrderId(transportOrder.getTransportOrderId());
					transportRecord.setTransportOrderCode(transportOrder.getTransportOrderCode());
					Map<String , Object> m=new HashMap<String, Object>();
					m.put("transportOrderId", transportOrder.getTransportOrderId());
					List<TransportOrderDetail> transportOrderDetails=transportOrderDetailService.queryByOrderId(m);
					transportRecord.setCarNumber(transportOrderDetails.size());
					double currentLoadWeight=0;
					for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
						Car car=carService.queryById(transportOrderDetail.getCarId());
						currentLoadWeight=currentLoadWeight+car.getCarWeight();
					}
					transportRecord.setCurrentLoadWeight(currentLoadWeight);
					transportrecordmapper.insertSelective(transportRecord);
					
				}
			}else {
				transportrecordmapper.insertSelective(transportRecord);
			}
			
			
		} catch (Exception e) {
			throw e;
		}
		
	}


}
