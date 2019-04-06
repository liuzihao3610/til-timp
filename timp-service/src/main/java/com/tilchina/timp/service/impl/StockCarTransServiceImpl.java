package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.StockCarTrans;
import com.tilchina.timp.service.StockCarTransService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.StockCarTransMapper;

/**
* 商品车运输记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class StockCarTransServiceImpl extends BaseServiceImpl<StockCarTrans> implements StockCarTransService {

	@Autowired
    private StockCarTransMapper stockcartransmapper;
	
	@Override
	protected BaseMapper<StockCarTrans> getMapper() {
		return stockcartransmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(StockCarTrans stockcartrans) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", stockcartrans.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "jobType", "作业类型:0=长途,1=市内,2=短驳,3=回程", stockcartrans.getJobType(), 10));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", stockcartrans.getCarVin(), 20));
        s.append(CheckUtils.checkDate("YES", "shutBillDate", "关账时间", stockcartrans.getShutBillDate()));
        s.append(CheckUtils.checkInteger("YES", "shutBillStatus", "关帐状态:0=未结算,1=已结算", stockcartrans.getShutBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "settleDate", "结算时间", stockcartrans.getSettleDate()));
        s.append(CheckUtils.checkInteger("YES", "settleStatus", "结算状态:0=未结算,1=已结算", stockcartrans.getSettleStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", stockcartrans.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(StockCarTrans stockcartrans) {
        StringBuilder s = checkNewRecord(stockcartrans);
        s.append(CheckUtils.checkPrimaryKey(stockcartrans.getStockCarTransId()));
		return s;
	}
	
	  @Override
	    public StockCarTrans queryById(Long id) {
	        log.debug("query: {}",id);
	        return getMapper().selectByPrimaryKey(id);
	    }

	    @Override
	    public PageInfo<StockCarTrans> queryList(Map<String, Object> map, int pageNum, int pageSize) {
	        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        PageHelper.startPage(pageNum, pageSize);
	        return new PageInfo<StockCarTrans>(getMapper().selectList(map));
	    }
	    
	    @Override
	    public List<StockCarTrans> queryList(Map<String, Object> map) {
	        log.debug("queryList: {}", map);
	        return getMapper().selectList(map);
	    }

	    @Override
	    public void add(StockCarTrans stockCarTrans) {
	        log.debug("add: {}",stockCarTrans);
	        StringBuilder s;
	        Environment env = Environment.getEnv();
	        try {
	        	stockCarTrans.setFlag(0);
	        	stockCarTrans.setCorpId(env.getCorp().getCorpId());
	            s = checkNewRecord(stockCarTrans);
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	            if( examineStockCarTrans(stockCarTrans.getTransportOrderDetailId()) != null) {
	            	 getMapper().updateByPrimaryKey(stockCarTrans);
	            }else {
	            	getMapper().insertSelective(stockCarTrans);
	            }
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

	    @Override
	    public void add(List<StockCarTrans> records) {
	        log.debug("addBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
			List<StockCarTrans> adds = new ArrayList<StockCarTrans>();
			List<StockCarTrans> updates = new ArrayList<StockCarTrans>();
	        try {
	            for (int i = 0; i < records.size(); i++) {
	            	StockCarTrans stockCarTrans = records.get(i);
					Integer flag = Optional.ofNullable(stockCarTrans.getFlag()).orElse(0);
					stockCarTrans.setFlag(flag);
	                StringBuilder check = checkNewRecord(stockCarTrans);
	                if(!StringUtils.isBlank(check)){
	                    checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
					StockCarTrans stockCarTrans1 = examineStockCarTrans(stockCarTrans.getTransportOrderDetailId());
					if( stockCarTrans1 != null) {
						/*getMapper().updateByPrimaryKey(stockCarTrans);*/
						updates.add(stockCarTrans1);
					}else {
						/*getMapper().insertSelective(stockCarTrans);*/
						Integer shutBillStatus = Optional.ofNullable(stockCarTrans.getShutBillStatus()).orElse(0);
						Integer settlestatus = Optional.ofNullable(stockCarTrans.getSettleStatus()).orElse(0);
						stockCarTrans.setSettleStatus(settlestatus);
						stockCarTrans.setShutBillStatus(shutBillStatus);
						stockCarTrans.setCorpId(Environment.getEnv().getCorp().getCorpId());
						adds.add(stockCarTrans);
					}
	            }
	            if (!checkResult) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	            update(updates);
	            getMapper().insert(adds);
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

	    @Override
	    public void updateSelective(StockCarTrans record) {
	        log.debug("updateSelective: {}",record);
	        try {
	            getMapper().updateByPrimaryKeySelective(record);
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

	    @Override
	    public void update(StockCarTrans record) {
	        log.debug("update: {}",record);
	        StringBuilder s;
	        try {
	            s = checkUpdate(record);
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            getMapper().updateByPrimaryKey(record);
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

	    @Override
	    public void update(List<StockCarTrans> records) {
	        log.debug("updateBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	            for (int i = 0; i < records.size(); i++) {
	            	StockCarTrans record = records.get(i);
	         		StringBuilder check = new StringBuilder(CheckUtils.checkLong("NO", "stockCarTransId", "商品车运输记录Id", record.getStockCarTransId(), 10));

					if(!StringUtils.isBlank(check)){
	                    checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
	            }
	            if (!checkResult) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
				records.forEach(stockCarTrans -> getMapper().updateByPrimaryKeySelective(stockCarTrans));
	           /* getMapper().update(records);*/
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

	    @Override
	    public void deleteById(Long id) {
	        log.debug("delete: {}",id);
	        getMapper().deleteByPrimaryKey(id);
	    }

		@Override
		public void logicDeleteByStockCarId(Long id) {
			log.debug("logicDeleteById: {}",id);
			StringBuilder s;
	        try {
	        	s = new StringBuilder();
	        	s.append(CheckUtils.checkLong("NO", "data", "商品车库存表ID", id, 20));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	   		    queryById(id);
	   		    stockcartransmapper.logicDeleteByStockCarId(id);
			} catch (Exception e) {
		        if(e instanceof BusinessException){
						throw e;
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
		}

		@Override
		public StockCarTrans examineStockCarTrans(Long transportOrderDetailId) {
			log.debug("examineStockCarTrans: {}",transportOrderDetailId);
			StockCarTrans trans;
			try {
				trans = stockcartransmapper.examineStockCarTrans(transportOrderDetailId);
			} catch (Exception e) {
		        if(e instanceof BusinessException){
						throw e;
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
			return trans;
		}

		@Override
		public List<StockCarTrans> queryByDetailIds(List<Long> detailIds){
			if(CollectionUtils.isEmpty(detailIds)){
				return null;
			}
			return stockcartransmapper.selectByDetailIds(detailIds);
		}

}
