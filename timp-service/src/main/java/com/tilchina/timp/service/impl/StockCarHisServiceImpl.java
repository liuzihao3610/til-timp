package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.StockCarHis;
import com.tilchina.timp.service.OrderService;
import com.tilchina.timp.service.StockCarHisService;
import com.tilchina.timp.service.StockCarService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.StockCarHisMapper;

/**
* 商品车状态变更记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class StockCarHisServiceImpl implements StockCarHisService {

	@Autowired
    private StockCarHisMapper stockcarhismapper;
	
	@Autowired
    private StockCarService stockCarService;
	
	@Autowired
    private OrderService orderService;
	
	protected BaseMapper<StockCarHis> getMapper() {
		return stockcarhismapper;
	}

	protected StringBuilder checkNewRecord(StockCarHis stockcarhis) {
		StringBuilder s = new StringBuilder();
		stockcarhis.setChangeDate(new Date());
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", stockcarhis.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("YES", "carStatus", "车辆状态(0=入库", stockcarhis.getCarStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "changeDate", "变更时间", stockcarhis.getChangeDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", stockcarhis.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(StockCarHis stockcarhis) {
        StringBuilder s = checkNewRecord(stockcarhis);
        s.append(CheckUtils.checkPrimaryKey(stockcarhis.getStockCarHisId()));
		return s;
	}
	 @Override
    public StockCarHis queryById(Long id) {
        log.debug("query: {}",id);
        return getMapper().selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<StockCarHis> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        map.put("orderByClause", "CHANGE_DATE");
        return new PageInfo<StockCarHis>(getMapper().selectList(map));
    }
    
    @Override
    public List<StockCarHis> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        map.put("orderByClause", "CHANGE_DATE");
        return getMapper().selectList(map);
    }

    @Override
    public void add(StockCarHis stockCarHis) {
        log.debug("add: {}",stockCarHis);
        StringBuilder s;
        try {
            s = checkNewRecord(stockCarHis);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            stockCarService.queryById(stockCarHis.getStockCarId());
            getMapper().insertSelective(stockCarHis);
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
    public void add(List<StockCarHis> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	StockCarHis record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().insert(records);
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
    public void updateSelective(StockCarHis record) {
        log.debug("updateSelective: {}",record);
        try {
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

    @Override
    public void update(StockCarHis record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = checkUpdate(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().updateByPrimaryKey(record);
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
    public void update(List<StockCarHis> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	StockCarHis record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().update(records);
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
	public void updateCarStatus(StockCarHis stockCarHis) {
    	StockCarHis updateStockCarHis = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "deptId", "商品车出库记录表ID", stockCarHis.getStockCarHisId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			updateStockCarHis = new StockCarHis();
			updateStockCarHis.setStockCarId(stockCarHis.getStockCarId());
			updateStockCarHis.setCarStatus(stockCarHis.getCarStatus());
			stockCarHis = queryById(stockCarHis.getStockCarId());
			//车辆状态(0=入库 1=分段 2=出库 3=预排 4=预排确认 5=下达给司机 6=司机接单 7=已预约 8=已装车 9=在途 10=已到店)
			//1.只有在途才能修改为已到店2.只有入库才能接单
			if(stockCarHis.getCarStatus() != 9 && updateStockCarHis.getCarStatus() == 10) {
				 throw new BusinessException("提示：车辆状态只有在途才能修改为已到店！");
			}else if(stockCarHis.getCarStatus() != 0 && updateStockCarHis.getCarStatus() == 6 && stockCarHis.getCarStatus() < 7){
				if(stockCarHis.getCarStatus() == 6) {
					throw new BusinessException("提示：车辆状态已经为接单状态！");
				}else {
					throw new BusinessException("提示：车辆状态只有入库才能接单！");
				}
			}else if(updateStockCarHis.getCarStatus() <= 10) {
				updateStockCarHis.setChangeDate(new Date());
				getMapper().updateByPrimaryKeySelective(updateStockCarHis);
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
			stockcarhismapper.logicDeleteByOrderId(OrderId);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		
	}
	    
}
