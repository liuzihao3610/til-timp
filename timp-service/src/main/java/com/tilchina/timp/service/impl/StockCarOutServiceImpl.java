package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.model.StockCarOut;
import com.tilchina.timp.service.CarService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.OrderDetailService;
import com.tilchina.timp.service.StockCarOutService;
import com.tilchina.timp.service.UnitService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.StockCarOutMapper;

/**
* 商品车出库记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class StockCarOutServiceImpl implements StockCarOutService {

	@Autowired
    private StockCarOutMapper stockcaroutmapper;
	
	@Autowired
    private OrderDetailService orderDetailService;
	
	@Autowired
    private CorpService corpService;
	
	@Autowired
    private CarService carService;
	
	@Autowired
    private UnitService unitService;
	
	protected StringBuilder checkNewRecord(StockCarOut stockcarout) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "orderCode", "出库单号", stockcarout.getOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "orderType", "订单类型", stockcarout.getOrderType(), 10));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", stockcarout.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("YES", "carStatus", "车辆状态(0=入库", stockcarout.getCarStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "orderBillStatus", "单据状态:0=有效单", stockcarout.getOrderBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "issuedDate", "下达时间", stockcarout.getIssuedDate()));
        s.append(CheckUtils.checkDate("YES", "rightBillType", "对账时间", stockcarout.getRightBillType()));
        s.append(CheckUtils.checkInteger("YES", "rightBillStatus", "对账状态:0=未结算,1=已结算", stockcarout.getRightBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "settleDate", "结算时间", stockcarout.getSettleDate()));
        s.append(CheckUtils.checkInteger("YES", "settleStatus", "结算状态:0=未结算,1=已结算", stockcarout.getSettleStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", stockcarout.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(StockCarOut stockcarout) {
        StringBuilder s = checkNewRecord(stockcarout);
        s.append(CheckUtils.checkPrimaryKey(stockcarout.getStockCarOutId()));
		return s;
	}
	
	@Override
    public StockCarOut queryById(Long id) {
        log.debug("query: {}",id);
        StockCarOut stockCarOut = null;
        try {
        	stockCarOut = stockcaroutmapper.selectByPrimaryKey(id);
        	if(stockCarOut == null) {
        		throw new BusinessException("9010","商品车出库记录表");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return stockCarOut;
    }

    @Override
    public PageInfo<StockCarOut> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<StockCarOut>(stockcaroutmapper.selectList(map));
    }
    
    @Override
    public List<StockCarOut> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return stockcaroutmapper.selectList(map);
    }

    @Override
    public void add(StockCarOut stockCarOut) {
        log.debug("add: {}",stockCarOut);
        StringBuilder s;
        try {
            s = checkNewRecord(stockCarOut);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            orderDetailService.queryById(stockCarOut.getOrderDetailId());
            corpService.queryById(stockCarOut.getCustomerId());
            corpService.queryById(stockCarOut.getCarrierId());
            corpService.queryById(stockCarOut.getCorpId());
            carService.queryById(stockCarOut.getCarId());
            unitService.queryById(stockCarOut.getCurrentUnitId());
            stockcaroutmapper.insertSelective(stockCarOut);
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
    public void add(List<StockCarOut> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	StockCarOut record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            stockcaroutmapper.insert(records);
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
    public void updateSelective(StockCarOut stockCarOut) {
        log.debug("updateSelective: {}",stockCarOut);
        StringBuilder s;
        StockCarOut updateStockCarOut = null;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "商品车出库记录表id", stockCarOut.getStockCarId(), 20));
        	 if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
        	 queryById(stockCarOut.getStockCarOutId());
        	 updateStockCarOut = new StockCarOut();
        	 //验证对账单、结算
    /*    	 if(stockCar.getTransferOrderId() != null) {
        		 transferOrderService.queryById(stockCar.getTransferOrderId());
        		 updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
        	 }
        	 if(stockCar.getTransferOrderId() != null) {
        		 transferOrderService.queryById(stockCar.getTransferOrderId());
        		 updateStockCar.setTransferOrderId(stockCar.getTransferOrderId());
        	 }*/
        	 updateStockCarOut.setStockCarId(stockCarOut.getStockCarOutId());
        	stockcaroutmapper.updateByPrimaryKeySelective(updateStockCarOut);
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
    public void update(StockCarOut record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = checkUpdate(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            stockcaroutmapper.updateByPrimaryKey(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void updateSelective(List<StockCarOut> outs){
		stockcaroutmapper.updateByPrimaryKeySelective(outs);
	}

    @Override
    public void update(List<StockCarOut> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	StockCarOut record = records.get(i);
				StringBuilder check = s.append(CheckUtils.checkLong("NO", "data", "商品车出库记录表id", record.getStockCarId(), 20));
               /* StringBuilder check = checkUpdate(record);*/
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
		/*	records.forEach(stockCarOut ->   stockcaroutmapper.up(stockCarOut));*/
            stockcaroutmapper.updateByPrimaryKeySelective(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("批量更新失败！", e);
            }
        }

    }

    @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        stockcaroutmapper.deleteByPrimaryKey(id);
    }
    
    
    @Override
	public void updateCarStatus(StockCarOut stockCarOut) {
    	StockCarOut updateStockCarOut = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "deptId", "商品车出库记录表ID", stockCarOut.getStockCarOutId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			updateStockCarOut = new StockCarOut();
			updateStockCarOut.setStockCarId(stockCarOut.getStockCarId());
			updateStockCarOut.setCarStatus(stockCarOut.getCarStatus());
			stockCarOut = queryById(stockCarOut.getStockCarId());
			//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
			//1.只有在途才能修改为已到店2.只有入库才能接单
			if(stockCarOut.getCarStatus() != 9 && updateStockCarOut.getCarStatus() == 10) {
				 throw new BusinessException("提示：车辆状态只有在途才能修改为已到店！");
			}else if(stockCarOut.getCarStatus() != 0 && updateStockCarOut.getCarStatus() == 6 && stockCarOut.getCarStatus() < 7){
				if(stockCarOut.getCarStatus() == 6) {
					throw new BusinessException("提示：车辆状态已经为接单状态！");
				}else {
					throw new BusinessException("提示：车辆状态只有入库才能接单！");
				}
			}else if(updateStockCarOut.getCarStatus() <= 10) {
				if(updateStockCarOut.getCarStatus() == 5) {
					updateStockCarOut.setIssuedDate(new Date());
				}
				stockcaroutmapper.updateByPrimaryKeySelective(updateStockCarOut);
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
	public void updateCancellationBill(Long id) {
	      StringBuilder s;
	      StockCarOut updateStockCarOut = null;
	      StockCarOut stockCarOut = null;
	        try {
	        	s = new StringBuilder();
	        	s.append(CheckUtils.checkLong("NO", "data", "商品车出库记录表ID",id, 20));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	   		updateStockCarOut = new StockCarOut();
	   		updateStockCarOut.setStockCarOutId(id);
	   		updateStockCarOut.setOrderBillStatus(1);
	   		stockCarOut = queryById(id);
	   		if(stockCarOut.getCarStatus() == 9) {
	   			throw new BusinessException("该商品车车辆已在途,请联系对应工作人员进行调整。此条记录不能作废！");
	   		}else if(stockCarOut.getCarStatus() == 10){
	   			throw new BusinessException("该商品车车辆已到店,请联系对应工作人员进行调整。此条记录不能作废！");
	   		}else if(stockCarOut.getCarStatus() == 8){
	   			throw new BusinessException("该商品车车辆已装车完成,请联系对应工作人员进行调整。此条记录不能作废！");
	   		}
	   		stockcaroutmapper.updateByPrimaryKeySelective(updateStockCarOut);
	        }catch (Exception e) {
	 			if(e instanceof BusinessException){
	 					throw e;
	 			}else {
	 					throw new RuntimeException("操作失败！", e);
	 			}
	 	    }
	}

	@Override
	public void updateRecoverBill(Long id) {
	      StringBuilder s;
	      StockCarOut stockCarOut = null;
	      StockCarOut updateStockCarOut = null;
	        try {
	        	s = new StringBuilder();
	        	s.append(CheckUtils.checkLong("NO", "data", "商品车出库记录表ID",id, 20));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	   		updateStockCarOut = new StockCarOut();
		   	updateStockCarOut.setStockCarOutId(id);
		   	updateStockCarOut.setOrderBillStatus(0);
		   	stockCarOut = queryById(id);
		   	if(stockCarOut.getOrderBillStatus() == 0) {
		   		throw new BusinessException("此条记录为有效单据，请不要重复操作！");
		   	}
		   	stockcaroutmapper.updateByPrimaryKeySelective(updateStockCarOut);
	        }catch (Exception e) {
	 			if(e instanceof BusinessException){
	 					throw e;
	 			}else {
	 					throw new RuntimeException("操作失败！", e);
	 			}
	 	    }
	}

	@Override
	public List<StockCarOut> queryByStockIds(List<Long> outIds){
		return stockcaroutmapper.selectByStockIds(outIds);
	}
	
}
