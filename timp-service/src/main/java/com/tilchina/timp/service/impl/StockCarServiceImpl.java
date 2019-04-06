package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.CarStatus;
import com.tilchina.timp.enums.OrderType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.StockCarMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
* 商品车库存表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class StockCarServiceImpl implements StockCarService {

	@Autowired
    private StockCarMapper stockcarmapper;

	@Autowired
    private StockCarOutService stockCarOutService;
	
	@Autowired
    private TransportOrderDetailService transportOrderDetailService;
	
	@Autowired
    private TransportOrderService transportOrderService;

	@Autowired   
    private OrderService orderService;

	@Autowired
    private OrderDetailService orderDetailService;
	
	@Autowired
    private TransferOrderService transferOrderService;
	
	@Autowired
    private StockCarHisService stockCarHisService;
	
	@Autowired
    private DriverService driverService;
	
	@Autowired
	private WarehouseLoadingPlanService warehouseLoadingPlanService;

	@Autowired
	private AccountingClosureService accountingClosureService;
	
	protected BaseMapper<StockCar> getMapper() {
		return stockcarmapper;
	}

	protected StringBuilder checkNewRecord(StockCar stockcar) {
		StringBuilder s = new StringBuilder();
		stockcar.setStockCarCode(DateUtil.dateToStringCode(new Date()));
		s.append(CheckUtils.checkString("NO", "stockcarCode", "商品车库存单号", stockcar.getStockCarCode(), 20));
        s.append(CheckUtils.checkString("NO", "orderCode", "客户订单号", stockcar.getOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "orderType", "订单类型", stockcar.getOrderType(), 10));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", stockcar.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("YES", "carStatus", "车辆状态(0=入库", stockcar.getCarStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "receivingDate", "接单时间", stockcar.getReceivingDate()));
        s.append(CheckUtils.checkInteger("YES", "transferOrderStatus", "交接单状态(0=未签收", stockcar.getTransferOrderStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "shutBillDate", "关账时间", stockcar.getShutBillDate()));
        s.append(CheckUtils.checkInteger("YES", "shutBillStatus", "关帐状态:0=未结算,1=已结算", stockcar.getShutBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "rightBillType", "对账时间", stockcar.getRightBillType()));
        s.append(CheckUtils.checkInteger("YES", "rightBillStatus", "对账状态:0=未结算,1=已结算", stockcar.getRightBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "settleDate", "结算时间", stockcar.getSettleDate()));
        s.append(CheckUtils.checkInteger("YES", "settleStatus", "结算状态:0=未结算,1=已结算", stockcar.getSettleStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", stockcar.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(StockCar stockcar) {
        StringBuilder s = checkNewRecord(stockcar);
        s.append(CheckUtils.checkPrimaryKey(stockcar.getStockCarId()));
		return s;
	}
	
	@Override
    public StockCar queryById(Long id) {
        log.debug("query: {}",id);
        StockCar stockCar = null;
        try {
        	stockCar = getMapper().selectByPrimaryKey(id);
        	if(stockCar == null) {
        		throw new BusinessException("9010","商品车库存表");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return stockCar;
    }

    @Override
    public PageInfo<StockCar> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<StockCar>(getMapper().selectList(map));
    }
    
    @Override
    public List<StockCar> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }

    @Override
    public void add(StockCar stockCar) {
        log.debug("add: {}",stockCar);
        StringBuilder s;
        try {
            s = checkNewRecord(stockCar);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            stockCar.setTransferOrderStatus(0);
            stockCar.setShutBillStatus(0);
            stockCar.setSettleStatus(0);
            getMapper().insertSelective(stockCar);
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
    public void add(List<StockCar> stockCars) {
        log.debug("addBatch: {}",stockCars);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        Environment env = Environment.getEnv();
        try {
            for (int i = 0; i < stockCars.size(); i++) {
            	StockCar stockCar = stockCars.get(i);
                StringBuilder check = checkNewRecord(stockCar);
                stockCar.setCorpId(env.getCorp().getCorpId());
                stockCar.setFlag(0);
                stockCar.setStockCarCode(DateUtil.dateToStringCode(new Date()));
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().insert(stockCars);
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
    public void updateSelective(StockCar stockCar) {
        log.debug("updateSelective: {}",stockCar);
        StringBuilder s;
        StockCar updateStockCar = null;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "商品车库存表id", stockCar.getStockCarId(), 20));
        	 if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
        	 queryById(stockCar.getStockCarId());
        	 updateStockCar = new StockCar();
        	 //验证交接单、关账单、对账单、结算
        	 if(stockCar.getTransferOrderId() != null) {
		         transferOrderService.queryById(stockCar.getTransferOrderId());
		         updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
	         }
	         if(stockCar.getShutBillId() != null) {
		         accountingClosureService.queryById(stockCar.getShutBillId());
		         updateStockCar.setShutBillId(stockCar.getShutBillId());
		         updateStockCar.setShutBillStatus(1);
		         updateStockCar.setShutBillDate(stockCar.getShutBillDate());
	         }

    /*    	 if(stockCar.getTransferOrderId() != null) {
        		 transferOrderService.queryById(stockCar.getTransferOrderId());
        		 updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
        	 }
        	 if(stockCar.getTransferOrderId() != null) {
        		 transferOrderService.queryById(stockCar.getTransferOrderId());
        		 updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
        	 }
        	 if(stockCar.getTransferOrderId() != null) {
        		 transferOrderService.queryById(stockCar.getTransferOrderId());
        		 updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
        	 }*/
        	updateStockCar.setStockCarId(stockCar.getStockCarId());
            getMapper().updateByPrimaryKeySelective(updateStockCar);
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
    public void update(StockCar stockCar) {
        log.debug("update: {}",stockCar);
        StringBuilder s;
        try {
            s = checkUpdate(stockCar);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().updateByPrimaryKey(stockCar);
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
    public void update(List<StockCar> stockCars) {
        log.debug("updateBatch: {}",stockCars);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < stockCars.size(); i++) {
            	StockCar stockCar = stockCars.get(i);
                StringBuilder check = 	new StringBuilder(CheckUtils.checkLong("NO", "data", "商品车库存表id", stockCar.getStockCarId(), 20));
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			stockcarmapper.updateByPrimaryKeySelective(stockCars);
			/*stockCars.forEach(stockCar ->  getMapper().updateByPrimaryKeySelective(stockCar));*/
            /*getMapper().update(stockCars);*/
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
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        getMapper().deleteByPrimaryKey(id);
    }

	@Override
	public void updateCarStatus(List<StockCar> stockCars,Integer orderBillStatus) {
		log.debug("updateCarStatus: {}",stockCars);
		StockCar updateStockCar = null;
		/*StockCar stockCar = null;*/
		StockCarHis stockCarHis = null;
		Environment env = Environment.getEnv();
		Map<String, Object> map = null;
		StockCarOut stockCarOut = null;
		List<StockCarHis> stockCarHiss = new ArrayList<StockCarHis>();
		List<StockCar> updateStockCars = new ArrayList<StockCar>();
		List<StockCarOut> addStockCarOuts = new ArrayList<StockCarOut>();
		List<StockCarOut> updateStockCarOuts = new ArrayList<StockCarOut>();
		StringBuilder s;
		try {
			s = new StringBuilder();
			Boolean checkResult = true;
			for (int i = 0; i < stockCars.size(); i++) {
				StockCar stockCar = stockCars.get(i);
				StringBuilder check = new StringBuilder();
				check.append(CheckUtils.checkLong("NO", "stockCarId", "商品车库存表ID", stockCar.getStockCarId(), 20));
				stockCar.setCorpId(env.getCorp().getCorpId());
				stockCar.setFlag(0);
				stockCar.setStockCarCode(DateUtil.dateToStringCode(new Date()));
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			for (StockCar stockCar : stockCars) {
				updateStockCar = new StockCar();
			/*	updateStockCar = stockCar;*/
				updateStockCar.setStockCarId(stockCar.getStockCarId());
				updateStockCar.setCarStatus(stockCar.getCarStatus());
				updateStockCar.setCurrentUnitId(stockCar.getCurrentUnitId());
				stockCar = queryById(stockCar.getStockCarId());
				//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
				//1.只有在途才能修改为已到店2.只有入库才能接单
				if(updateStockCar.getCarStatus().intValue() == CarStatus.Arrived.getIndex()) {
					throw new BusinessException("提示：车架号："+stockCar.getCarVin()+"的车辆状态只有在途才能修改为已到店！");
				}
				else if(stockCar.getCarStatus().intValue() != CarStatus.In.getIndex() && updateStockCar.getCarStatus().intValue() == CarStatus.Accept.getIndex() && stockCar.getCarStatus().intValue() < CarStatus.Appointed.getIndex()){
					if(stockCar.getCarStatus().intValue() == CarStatus.Accept.getIndex()) {
						throw new BusinessException("提示：车架号："+stockCar.getCarVin()+"车辆状态已经为接单状态！");
					}else if(updateStockCar.getCarStatus().intValue() == CarStatus.Accept.getIndex() && stockCar.getCarStatus().intValue() == CarStatus.ToDriver.getIndex()){
						updateStockCar.setCarStatus(updateStockCar.getCarStatus());
						updateStockCar.setReceivingDate(new Date());
					}else{
						throw new BusinessException("提示：车架号："+stockCar.getCarVin()+"车辆状态只有入库才能接单！");
					}
				}
				else if(updateStockCar.getCarStatus().intValue() <= CarStatus.Arrived.getIndex()) {
					if(updateStockCar.getCarStatus().intValue() == CarStatus.Accept.getIndex()) {
						updateStockCar.setReceivingDate(new Date());
					}
					if(updateStockCar.getCarStatus().intValue() == CarStatus.Out.getIndex()) {
						//维护商品车出库记录表
						stockCarOut = new StockCarOut();
						stockCarOut.setStockCarId(stockCar.getStockCarId());
						stockCarOut.setCarStatus(updateStockCar.getCarStatus());
						stockCarOut.setCorpId(env.getCorp().getCorpId());

						stockCarOut.setCarId(stockCar.getCarId());
						stockCarOut.setCarVin(stockCar.getCarVin());
						stockCarOut.setFlag(0);
						stockCarOut.setOrderId(stockCar.getOrderId());
						stockCarOut.setOrderCode(stockCar.getOrderCode());
						stockCarOut.setOrderDetailId(stockCar.getOrderDetailId());
						stockCarOut.setOrderType(stockCar.getOrderType());
						stockCarOut.setCurrentUnitId(stockCar.getCurrentUnitId());
						stockCarOut.setCustomerId(stockCar.getCustomerId());
						stockCarOut.setCarrierId(stockCar.getCarrierId());
						//0=有效 1=无效
						stockCarOut.setOrderBillStatus(orderBillStatus);
						addStockCarOuts.add(stockCarOut);
						/*stockCarOutService.add(stockCarOut);*/
					}
					//维护商品车变更记录表信息
				/*	stockcarmapper.updateByPrimaryKeySelective(updateStockCar);*/
					updateStockCars.add(updateStockCar);
					stockCarHis = new StockCarHis();
					stockCarHis.setStockCarId(updateStockCar.getStockCarId());
					stockCarHis.setCarStatus(updateStockCar.getCarStatus());
					stockCarHis.setCorpId(env.getCorp().getCorpId());

					stockCarHis.setCarVin(stockCar.getCarVin());
					stockCarHis.setFlag(0);
					stockCarHiss.add(stockCarHis);
					/*stockCarHisService.add(stockCarHis);*/
				}else if(updateStockCar.getCarStatus().intValue() == CarStatus.Closed.getIndex()) {
					//维护商品车变更记录表信息
					updateStockCars.add(updateStockCar);
					/*stockcarmapper.updateByPrimaryKeySelective(updateStockCar);*/
					stockCarHis = new StockCarHis();
					stockCarHis.setStockCarId(updateStockCar.getStockCarId());
					stockCarHis.setCarStatus(updateStockCar.getCarStatus());
					stockCarHis.setCorpId(env.getCorp().getCorpId());
					stockCarHis.setCarVin(stockCar.getCarVin());
					stockCarHis.setFlag(0);
					stockCarHiss.add(stockCarHis);
					/*stockCarHisService.add(stockCarHis);*/
					map = new HashMap<>();
					map.put("carVin", updateStockCar.getCarVin());
					map.put("stockCarId", updateStockCar.getStockCarId());
					map.put("orderId", updateStockCar.getOrderId());
					map.put("orderDetailId", updateStockCar.getOrderDetailId());
					if(stockCarOutService.queryList(map).size() > 0) {
						//维护商品车出库记录
						stockCarOut = stockCarOutService.queryList(map).get(0);
						stockCarOut.setCarStatus(CarStatus.Closed.getIndex());
						updateStockCarOuts.add(stockCarOut);
						/*stockCarOutService.updateSelective(stockCarOut);*/
					}else {
						stockCarOut = new StockCarOut();
						stockCarOut.setStockCarId(stockCar.getStockCarId());
						stockCarOut.setCarStatus(CarStatus.Closed.getIndex());
						stockCarOut.setCorpId(env.getCorp().getCorpId());
						stockCarOut.setCarId(stockCar.getCarId());
						stockCarOut.setCarVin(stockCar.getCarVin());
						stockCarOut.setFlag(0);
						stockCarOut.setOrderId(stockCar.getOrderId());
						stockCarOut.setOrderCode(stockCar.getOrderCode());
						stockCarOut.setOrderDetailId(stockCar.getOrderDetailId());
						stockCarOut.setOrderType(stockCar.getOrderType());
						stockCarOut.setCurrentUnitId(stockCar.getCurrentUnitId());
						stockCarOut.setCustomerId(stockCar.getCustomerId());
						stockCarOut.setCarrierId(stockCar.getCarrierId());
						//0=有效 1=无效
						stockCarOut.setOrderBillStatus(orderBillStatus);
						addStockCarOuts.add(stockCarOut);
						/*stockCarOutService.add(stockCarOut);*/
					}
				}
			}
			stockCarHisService.add(stockCarHiss);
			update(updateStockCars);
			stockCarOutService.update(updateStockCarOuts);
			stockCarOutService.add(addStockCarOuts);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}


	@Override
	public void updateCarStatus(StockCar stockCar,Integer orderBillStatus) {
		 log.debug("updateCarStatus: {}",stockCar);
		StockCar updateStockCar = null;
		StockCarHis stockCarHis = null;
		Environment env = Environment.getEnv();
		Map<String, Object> map = null;
		StockCarOut stockCarOut = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "stockCarId", "商品车库存表ID", stockCar.getStockCarId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			updateStockCar = new StockCar();
			updateStockCar.setStockCarId(stockCar.getStockCarId());
			updateStockCar.setCarStatus(stockCar.getCarStatus());
			updateStockCar.setCurrentUnitId(stockCar.getCurrentUnitId());
			stockCar = queryById(stockCar.getStockCarId());
			//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
			//1.只有在途才能修改为已到店2.只有入库才能接单
			if(updateStockCar.getCarStatus().intValue() == CarStatus.Arrived.getIndex()) {
				 throw new BusinessException("提示：车辆状态只有在途才能修改为已到店！");
			}
	else if(stockCar.getCarStatus() != 0 && updateStockCar.getCarStatus() == 6 && stockCar.getCarStatus() < 7){
				if(stockCar.getCarStatus() == 6) {
					throw new BusinessException("提示：车辆状态已经为接单状态！");
				}else if(updateStockCar.getCarStatus() == 6 && stockCar.getCarStatus() == 5){
					updateStockCar.setCarStatus(updateStockCar.getCarStatus());
					updateStockCar.setReceivingDate(new Date());
				}else{
					throw new BusinessException("提示：车辆状态只有入库才能接单！");
				}
			}
	else if(updateStockCar.getCarStatus().intValue() <= CarStatus.Arrived.getIndex()) {
				if(updateStockCar.getCarStatus().intValue() == CarStatus.Accept.getIndex()) {
					updateStockCar.setReceivingDate(new Date());
				}
				if(updateStockCar.getCarStatus().intValue() == CarStatus.Out.getIndex()) {
					//维护商品车出库记录表
					 stockCarOut = new StockCarOut();
					stockCarOut.setStockCarId(stockCar.getStockCarId());
					stockCarOut.setCarStatus(updateStockCar.getCarStatus());
					stockCarOut.setCorpId(env.getCorp().getCorpId());

					stockCarOut.setCarId(stockCar.getCarId());
					stockCarOut.setCarVin(stockCar.getCarVin());
					stockCarOut.setFlag(0);
					stockCarOut.setOrderId(stockCar.getOrderId());
					stockCarOut.setOrderCode(stockCar.getOrderCode());
					stockCarOut.setOrderDetailId(stockCar.getOrderDetailId());
					stockCarOut.setOrderType(stockCar.getOrderType());
					stockCarOut.setCurrentUnitId(stockCar.getCurrentUnitId());
					stockCarOut.setCustomerId(stockCar.getCustomerId());
					stockCarOut.setCarrierId(stockCar.getCarrierId());
					//0=有效 1=无效
					stockCarOut.setOrderBillStatus(orderBillStatus);
					stockCarOutService.add(stockCarOut);
				}
				//维护商品车变更记录表信息
				stockcarmapper.updateByPrimaryKeySelective(updateStockCar);
				stockCarHis = new StockCarHis();
				stockCarHis.setStockCarId(updateStockCar.getStockCarId());
				stockCarHis.setCarStatus(updateStockCar.getCarStatus());
				stockCarHis.setCorpId(env.getCorp().getCorpId());

				stockCarHis.setCarVin(stockCar.getCarVin());
				stockCarHis.setFlag(0);
				stockCarHisService.add(stockCarHis);
			}else if(updateStockCar.getCarStatus().intValue() == CarStatus.Closed.getIndex()) {
				//维护商品车变更记录表信息
				stockcarmapper.updateByPrimaryKeySelective(updateStockCar);
				stockCarHis = new StockCarHis();
				stockCarHis.setStockCarId(updateStockCar.getStockCarId());
				stockCarHis.setCarStatus(updateStockCar.getCarStatus());
				stockCarHis.setCorpId(env.getCorp().getCorpId());
				stockCarHis.setCarVin(stockCar.getCarVin());
				stockCarHis.setFlag(0);
				stockCarHisService.add(stockCarHis);
				map = new HashMap<>();
				map.put("carVin", updateStockCar.getCarVin());
				map.put("stockCarId", updateStockCar.getStockCarId());
				map.put("orderId", updateStockCar.getOrderId());
				map.put("orderDetailId", updateStockCar.getOrderDetailId());
				if(stockCarOutService.queryList(map).size() > 0) {
					//维护商品车出库记录
					stockCarOut = stockCarOutService.queryList(map).get(0);
					stockCarOut.setCarStatus(CarStatus.Closed.getIndex());
					stockCarOutService.updateSelective(stockCarOut);
				}else {
					stockCarOut = new StockCarOut();
					stockCarOut.setStockCarId(stockCar.getStockCarId());
					stockCarOut.setCarStatus(CarStatus.Closed.getIndex());
					stockCarOut.setCorpId(env.getCorp().getCorpId());
					stockCarOut.setCarId(stockCar.getCarId());
					stockCarOut.setCarVin(stockCar.getCarVin());
					stockCarOut.setFlag(0);
					stockCarOut.setOrderId(stockCar.getOrderId());
					stockCarOut.setOrderCode(stockCar.getOrderCode());
					stockCarOut.setOrderDetailId(stockCar.getOrderDetailId());
					stockCarOut.setOrderType(stockCar.getOrderType());
					stockCarOut.setCurrentUnitId(stockCar.getCurrentUnitId());
					stockCarOut.setCustomerId(stockCar.getCustomerId());
					stockCarOut.setCarrierId(stockCar.getCarrierId());
					//0=有效 1=无效
					stockCarOut.setOrderBillStatus(orderBillStatus);
					stockCarOutService.add(stockCarOut);
				}
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
	public StockCar queryByCarVin(String carVin) {
		 log.debug("queryByCarVin: {}",carVin);
		 StockCar stockCar = null;
		 try {
			 stockCar = stockcarmapper.selectByCarVin(carVin);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
		}else {
				throw new RuntimeException("操作失败！", e);
		}
    }
		return stockCar;
	}


	@Override
	public StringBuilder shutOrder(Long orderId,Long corpId,Integer shutType,String carVIn) {
		log.debug("orderCheckStockCar: {}", orderId);
		StockCar stockCar;
		StringBuilder hint,extend;
		Map<String, Object> map;
    	try {
			hint = new StringBuilder();
			extend = new StringBuilder();
			Order order;
			List<OrderDetail> ods;
			map = new HashMap<>();
			User driver;
			order = null;//orderService.queryById(orderId);
			ods = order.getOds();
			hint.append("订单号:"+order.getOrderCode()+"中");
			//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店 11=取消下达/取消订单/取消计划)
			//取消下达:根据orderId删除全部的商品车库存 = 0
			//取消订单：根据orderId删除全部的 = 1
			//关闭计划：传入车架号，订单表头id删除有关于车架号的商品车库存 = 2
			if(shutType == 0 || shutType == 1) {
				for (int j = 0; j < ods.size(); j++) {
					OrderDetail orderDetail = ods.get(j);
					stockCar = new StockCar();
					stockCar.setOrderDetailId(orderDetail.getOrderDetailId());
					stockCar = queryByStockCar(stockCar);
					stockCar.setCarStatus(0);
					if(stockCar.getCarStatus() > 8 ) {
						hint.append("车架号:"+stockCar.getCarVin()+",");
						map.put("carVin", stockCar.getCarVin());
						for (TransportOrderDetail transportOrderDetail : transportOrderDetailService.queryList(map)) {
							driver = driverService.queryById(transportOrderDetail.getDriverId());
							extend.append("司机联系信息:司机电话："+driver.getPhone()+",司机姓名："+driver.getUserName());
						}
					}else {
						stockCar.setCarStatus(11);
						//维护商品库存表
						updateCarStatus(stockCar, 1);
					}
				}
			}else if(shutType == 2) {
				stockCar = new StockCar();
				stockCar.setOrderId(orderId);
				stockCar.setCarVin(carVIn);
				//检查商品车库存表此批车架号是否可以入库
				stockCar = queryByStockCar(stockCar);
				if(stockCar.getCarStatus() > 8 ) {
					hint.append("车架号:"+stockCar.getCarVin()+",");
					map.put("carVin", stockCar.getCarVin());
					for (TransportOrderDetail transportOrderDetail : transportOrderDetailService.queryList(map)) {
						driver = driverService.queryById(transportOrderDetail.getDriverId());
						extend.append("司机联系信息:司机电话："+driver.getPhone()+",司机姓名："+driver.getUserName());
					}
				}else {
					stockCar.setCarStatus(11);
					//维护商品库存表
					updateCarStatus(stockCar, 1);
				}
			}
			if(hint.indexOf("车架号") != -1) {
				hint.append("以在途或到店，不能取消订单。请联系相关业务人员！");
				hint.append(extend);
				throw new BusinessException(hint.toString());
				
			}
			return hint;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void logicDeleteByOrderId(Long OrderId) {
		log.debug("logicDeleteById: {}", OrderId);
		StringBuilder s;
		Map<String, Object> map;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "订单主表ID", OrderId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    map = new HashMap<>();
   		    map.put("orderId", OrderId);
			if(queryList(map).size() < 0 ) {
				throw new BusinessException("9008","订单主表ID");
			}
			stockcarmapper.logicDeleteByOrderId(OrderId);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		
	}


	@Override
	public PageInfo<StockCar> stockCarRefer(Map<String, Object> map,int pageNum, int pageSize) {
        log.debug("orderStockCorRefer: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<StockCar> pageInfo;
      //库存表车架号参照: 传递参数corpId,返回所属公司是corpId,车架号状态是在库的
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	if(map.get("corpId") != null) {
        		map.put("customerId", map.get("corpId"));
        	}
        	map.put("carStatus", "0");
        	pageInfo = new PageInfo<StockCar>(stockcarmapper.selectstockCarRefer(map));
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
	public StockCar queryByTransOrderDetailId(Long transDetailOrderId){
        StockCar car = stockcarmapper.selectByTransOrderDetailId(transDetailOrderId);
        if(car == null){
            throw new BusinessException("运单子表id："+transDetailOrderId+"，商品车库存表找不到记录！");
        }
        return car;
    }

	@Override
	public StockCar queryByStockCar(StockCar stockCar) {
		 log.debug("queryByStockCar: {}",stockCar);
		 List<StockCar> stockCars;
		 try {
			 if(StringUtils.isNoneBlank(stockCar.getCarVin())) {
				 CheckUtils.checkString("NO", "carVin", "车架号",stockCar.getCarVin(), 20);	 
				 stockCars = stockcarmapper.selectListByStockCar(stockCar);
				 if(stockCars.size() > 1) {
					 throw new BusinessException("商品车库存表查询到车架号:["+stockCar.getCarVin()+"]重复，请添加过滤条件！");
				 }else if(stockCars.size() == 1){
					 return stockCars.get(0);
				 }else {
					 throw new BusinessException("商品车库存表查询到车架号:["+stockCar.getCarVin()+"]没有有效数据！");
				 }
			 }else {
				 CheckUtils.checkLong("NO", "orderDetailId", "子订单ID",stockCar.getOrderDetailId(), 20);
			 }
			 stockCar = stockcarmapper.selectByStockCar(stockCar);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
		}else {
				throw new RuntimeException("操作失败！", e);
		}
   }
		return stockCar;
	}

	@Override
	public PageInfo<StockCar> transportOrderRefer(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("orderStockCorRefer: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<StockCar> pageInfo;
        //库存表车架号参照: 传递参数corpId,返回所属公司是corpId,车架号状态是在库的
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("carStatus", "0");
        	if(map.get("corpId") != null) {
        		map.put("corpId", map.get("corpId"));
        	}
        	if(map.get("carrierId") != null) {
        		map.put("carrierId", map.get("carrierId"));
        	}
        	pageInfo = new PageInfo<StockCar>(stockcarmapper.transportOrderRefer(map));
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
	public List<StockCar> batchSelectCarvin(Map<String, Object> map) {
        log.debug("orderStockCorRefer: {}", map);
        List<StockCar> stockCars;
        List<String> carVins = null;
        //库存表车架号参照: 传递参数corpId,返回所属公司是corpId,车架号状态是在库的
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("carStatus", "0");
        	if(map.get("corpId") != null) {
        		map.put("corpId", map.get("corpId"));
        	}
        	if(map.get("carrierId") != null) {
        		map.put("carrierId", map.get("carrierId"));
        	}
        	stockCars = new ArrayList<StockCar>();
        	if(map.get("carVin") != null) {
        		carVins = CheckUtil.checkcarVins(map.get("carVin").toString());
        		for (String carVin : carVins) {
            		map.put("carVin", carVin);
            		stockCars.addAll(stockcarmapper.transportOrderRefer(map));
    			}
        	}else {
        		stockCars.addAll(stockcarmapper.transportOrderRefer(map));
        	}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
        return stockCars;
	}
	@Override
	public List<StockCar> queryByVins(List<String> vins, Long corpId){
		Map<String,Object> pm = new HashMap<>();
		pm.put("corpId",corpId);
		pm.put("vins",vins);
		return stockcarmapper.selectByVins(pm);
	}

	/**
     *
     * @since 1.0.0
     * @param order     订单信息
     * @param isReverse   逆向操作，true逆向，false正向
     * @return void
     */
	@Override
    @Transactional
    public void stockForOrder(Order order, boolean isReverse){
        List<OrderDetail> details = orderService.queryDetailByOrderId(order.getOrderId());
        List<StockCar> stockCars = new ArrayList<>();
        // 原始订单/转包订单/分段订单-下达：运输公司入
        if(order.getBillStatus() == 2 && !isReverse){
            stockCars = stockForIn(order.getCorpCarrierId(),order,details);
        }

        // 转包订单/分段订单-下达：生成出库记录
		if((OrderType.Out.getIndex() == order.getOrderType() || OrderType.Section.getIndex() == order.getOrderType())
				&& order.getBillStatus() == 2 && !isReverse){
            // 获取当前订单车架号的库存记录
            List<String> vins = new ArrayList<>();
            Map<String,OrderDetail> detailMap = new HashMap<>();
            for (OrderDetail detail : details) {
                vins.add(detail.getCarVin());
                detailMap.put(detail.getCarVin(),detail);
            }

            List<StockCar> stocks = queryByVins(vins,order.getCorpCustomerId());
            for (StockCar stock : stocks) {
                stock.setOrderDetail(detailMap.get(stock.getCarVin()));
            }

            stockOut(stocks,order);
		}

        // 原始订单/转包订单/分段订单-取消下达：运输公司关闭
        if(order.getBillStatus() == 1 && isReverse){
            stockCars = stockForClose(order.getCorpCarrierId(),order,details);
        }

        // 转包订单/分段订单-取消下达：关闭出库记录
		if((OrderType.Out.getIndex() == order.getOrderType() || OrderType.Section.getIndex() == order.getOrderType())
				&& order.getBillStatus() == 1 && isReverse){
            // 获取当前订单车架号的库存记录
            Set<String> vins = new HashSet<>();
            Map<String,OrderDetail> detailMap = new HashMap<>();
            for (OrderDetail detail : details) {
                vins.add(detail.getCarVin());
                detailMap.put(detail.getCarVin(),detail);
            }
            Map<String,Object> pm = new HashMap<>();
            pm.put("corpId",order.getCorpCustomerId());
            pm.put("vins",vins);
            List<StockCar> stocks = stockcarmapper.selectByVins(pm);
            for (StockCar stock : stocks) {
                stock.setOrderDetail(detailMap.get(stock.getCarVin()));
            }
			stockOutReverse(stocks);
		}

        // 转包订单/分段订单-审核：客户公司出
        if((OrderType.Out.getIndex() == order.getOrderType() || OrderType.Section.getIndex() == order.getOrderType())
                && order.getBillStatus() == 1 && !isReverse){
            stockCars = stockForOut(order.getCorpCustomerId(),order,details);
            // 更新入库单车架号状态为出库
            List<OrderDetail> inDetails = new ArrayList<>();
            for (StockCar stockCar : stockCars) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailId(stockCar.getOrderDetailId());
                d.setCarStatus(CarStatus.Out.getIndex());
                inDetails.add(d);
            }
            orderService.updateDetailSelective(inDetails);
        }

        // 转包或分段订单-取消审核：客户公司入
        if((OrderType.Out.getIndex() == order.getOrderType() || OrderType.Section.getIndex() == order.getOrderType())
                && order.getBillStatus() == 0 && isReverse){
            stockCars = stockForIn(order.getCorpCustomerId(),order,details);
            // 更新入库单车架号状态为入库
            List<OrderDetail> inDetails = new ArrayList<>();
            for (StockCar stockCar : stockCars) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailId(stockCar.getOrderDetailId());
                d.setCarStatus(CarStatus.In.getIndex());
                inDetails.add(d);
            }
            orderService.updateDetailSelective(inDetails);
        }

        // 写入历史记录
        List<StockCarHis> his = new ArrayList<>();
        Date d = new Date();
        for (StockCar st : stockCars) {
            StockCarHis h = new StockCarHis();
            h.setStockCarId(st.getStockCarId());
            h.setCarVin(st.getCarVin());
            h.setCarStatus(st.getCarStatus());
            h.setChangeDate(d);
            h.setOrderBillId(st.getOrderDetailId());
            h.setCorpId(st.getCorpId());
            h.setFlag(0);
            his.add(h);
        }
        if(CollectionUtils.isNotEmpty(his)) {
            stockCarHisService.add(his);
        }
    }

    @Override
	@Transactional
    public void stockOut(List<StockCar> stocks, Order order){
        // 汇总库存ID
        List<Long> outIds = new ArrayList<>();
        for (StockCar stock : stocks) {
            outIds.add(stock.getStockCarId());
        }

        // 获取已存在的出库记录
        List<StockCarOut> outs = stockCarOutService.queryByStockIds(outIds);
        Map<Long,StockCarOut> outMap = new HashMap<>();
        for (StockCarOut out : outs) {
            outMap.put(out.getStockCarId(),out);
        }

        List<StockCarOut> adds = new ArrayList<>();
        List<StockCarOut> updates = new ArrayList<>();
        for (StockCar stock : stocks) {
            OrderDetail detail = stock.getOrderDetail();
            StockCarOut out = new StockCarOut();
            out.setStockCarId(stock.getStockCarId());
            out.setOrderId(detail.getOrderId());
            out.setOrderCode(detail.getOrderCode());
            out.setOrderDetailId(detail.getOrderDetailId());
            out.setOrderType(order.getOrderType());
            out.setCustomerId(order.getCorpCustomerId());
            out.setCarrierId(order.getCorpCarrierId());
            out.setCarVin(stock.getCarVin());
            out.setCarId(stock.getCarId());
            out.setCarStatus(CarStatus.Out.getIndex());
            out.setCurrentUnitId(detail.getSendUnitId());
            out.setOrderBillStatus(0);
            out.setIssuedDate(order.getTransmitDate());
            out.setCorpId(stock.getCorpId());
            out.setFlag(0);
            out.setRightBillStatus(0);
            out.setSettleStatus(0);
            if(outMap.get(stock.getStockCarId()) == null){
                adds.add(out);
            }else{
                StockCarOut old = outMap.get(stock.getStockCarId());
                out.setStockCarOutId(old.getStockCarOutId());
                updates.add(out);
            }
        }
        if(CollectionUtils.isNotEmpty(adds)) {
            stockCarOutService.add(adds);
        }
        if(CollectionUtils.isNotEmpty(updates)) {
            stockCarOutService.updateSelective(updates);
        }
    }

    @Override
	@Transactional
    public void stockOutReverse(List<StockCar> stocks){
        // 汇总库存ID
        List<Long> outIds = new ArrayList<>();
        for (StockCar stock : stocks) {
            outIds.add(stock.getStockCarId());
        }

        // 获取已存在的出库记录
        List<StockCarOut> outs = stockCarOutService.queryByStockIds(outIds);
        Map<Long,StockCarOut> outMap = new HashMap<>();
        for (StockCarOut out : outs) {
            outMap.put(out.getStockCarId(),out);
        }

        // 变更为关闭状态
        for (StockCar stock : stocks) {
			StockCarOut out = outMap.get(stock.getStockCarId());
			out.setCarStatus(CarStatus.Closed.getIndex());
			out.setOrderBillStatus(1);
        }
        if(CollectionUtils.isNotEmpty(outs)) {
            stockCarOutService.updateSelective(outs);
        }

    }

    @Override
	@Transactional
    public List<StockCar> stockForClose(Long corpId, Order order, List<OrderDetail> details){
        return updateStock(corpId,order,details,CarStatus.Closed);
    }

    @Override
	@Transactional
    public List<StockCar> stockForOut(Long corpId, Order order, List<OrderDetail> details){
        return updateStock(corpId,order,details,CarStatus.Out);
    }

    @Override
	@Transactional
    public List<StockCar> stockForIn(Long corpId, Order order, List<OrderDetail> details){
        // 汇总车架号
        List<String> vins = new ArrayList<>();
        for (OrderDetail detail : details) {
            vins.add(detail.getCarVin());
        }
        // 获取车架号的在库信息
        Map<String,Object> map = new HashMap<>();
        map.put("corpId",corpId);
        map.put("vins",vins);
        List<StockCar> stocks = stockcarmapper.selectByVins(map);
        Map<String,StockCar> stockCarMap = new HashMap<>();
        for (StockCar stock : stocks) {
            stockCarMap.put(stock.getCarVin(),stock);
        }

        List<StockCar> adds = new ArrayList<>();
        List<StockCar> updates = new ArrayList<>();
        for (OrderDetail detail : details) {
            StockCar s = new StockCar();
            s.setOrderId(detail.getOrderId());
            s.setOrderCode(detail.getOrderCode());
            s.setOrderDetailId(detail.getOrderDetailId());
            s.setOrderType(order.getOrderType());
            s.setCustomerId(order.getCorpCustomerId());
            s.setCarrierId(order.getCorpCarrierId());
            s.setCarVin(detail.getCarVin());
            s.setCarId(detail.getCarId());
            s.setCarStatus(CarStatus.In.getIndex());
            s.setCurrentUnitId(detail.getSendUnitId());
            s.setReceivingDate(order.getOrderDate());
            s.setCorpId(corpId);
            s.setFlag(0);
            if(stockCarMap.get(detail.getCarVin()) == null){
                s.setStockCarCode(getCode());
                adds.add(s);
            }else{
                StockCar old = stockCarMap.get(detail.getCarVin());
                // 已关闭的车架号无法通过取消审核恢复入库
                if(order.getBillStatus() == 0 && CarStatus.Closed == CarStatus.get(old.getCarStatus())){
                    continue;
                }
                if(CarStatus.In == CarStatus.get(old.getCarStatus())){
                    throw new BusinessException("车架号["+detail.getCarVin()+"]已入库，禁止重复入库。");
                }
	            old.setCarStatus(CarStatus.In.getIndex());
                updates.add(old);
            }
        }
        if(CollectionUtils.isNotEmpty(adds)) {
            stockcarmapper.insert(adds);
        }
        if(CollectionUtils.isNotEmpty(updates)) {
            stockcarmapper.updateByPrimaryKey(updates);
        }

        adds.addAll(updates);
        return adds;
    }

    @Override
	@Transactional
    public List<StockCar> updateStock(Long corpId, Order order, List<OrderDetail> details, CarStatus status){
        // 汇总车架号
        List<String> vins = new ArrayList<>();
        for (OrderDetail detail : details) {
            vins.add(detail.getCarVin());
        }
        // 获取车架号的在库信息
        Map<String,Object> map = new HashMap<>();
        map.put("corpId",corpId);
        map.put("vins",vins);
        List<StockCar> stocks = stockcarmapper.selectByVins(map);
        Map<String,StockCar> stockCarMap = new HashMap<>();
        for (StockCar stock : stocks) {
            stockCarMap.put(stock.getCarVin(),stock);
        }

        // 更新库存表
        List<StockCar> updates = new ArrayList<>();
        for (OrderDetail detail : details) {
            StockCar old = stockCarMap.get(detail.getCarVin());
            if(CarStatus.Out == status) {
                if (old == null && CarStatus.In != CarStatus.get(old.getCarStatus())) {
                    throw new BusinessException("车架号[" + detail.getCarVin() + "]不是在库状态，无法出库。");
                }
            }
            old.setCarStatus(status.getIndex());
            old.setOrderDetail(detail);
            updates.add(old);
        }
        if(CollectionUtils.isNotEmpty(updates)) {
            stockcarmapper.updateByPrimaryKey(updates);
        }
        return updates;
    }

    private String getCode(){
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sf.format(new Date());
    }

	@Override
	public void updateTransStatus(StockCar stockCar) {
		try {
			stockcarmapper.updateTransStatus(stockCar);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void closeCar(List<StockCar> stockCars){
		if(CollectionUtils.isNotEmpty(stockCars)){
			List<StockCar> newStocks = new ArrayList<>();
			for (StockCar stock : stockCars) {
				StockCar newStock = new StockCar();
				newStock.setStockCarId(stock.getStockCarId());
				newStock.setCarStatus(CarStatus.Closed.getIndex());
				newStocks.add(newStock);
			}
			updateBySelective(newStocks);
		}

	}

	@Override
	public StockCar queryByOrderDetailId(Long orderDetailId){
    	return stockcarmapper.selectByOrderDetailId(orderDetailId);
	}

	@Override
	public void updateBySelective(List<StockCar> stockCars){
		stockcarmapper.updateByPrimaryKeySelective(stockCars);
	}

	@Override
	public void updateBySelective(StockCar stockCar){
		stockcarmapper.updateByPrimaryKeySelective(stockCar);
	}

	@Override
	public PageInfo<StockCar> getVinList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("getVinList: {}",map);
		PageHelper.startPage(pageNum, pageSize);
		try {
			
			Environment environment=Environment.getEnv();
			if (map==null) {
				map=new HashMap<String, Object>();
			}
			map.put("corpId", environment.getCorp().getCorpId());
			return new PageInfo<StockCar>(stockcarmapper.getVinList(map));
		} catch (Exception e) {
			throw e;
		}
	}


}
