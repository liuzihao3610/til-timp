package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Deduction;
import com.tilchina.timp.model.TransportSettlement;
import com.tilchina.timp.model.TransportSubsidy;
import com.tilchina.timp.service.DeductionService;
import com.tilchina.timp.service.TransportSettlementService;
import com.tilchina.timp.service.TransportSubsidyService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportSubsidyMapper;

/**
* 运单结算子表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class TransportSubsidyServiceImpl implements TransportSubsidyService {

	@Autowired
    private TransportSubsidyMapper transportsubsidymapper;
	
	@Autowired
    private TransportSettlementService transportSettlementService;
	
	@Autowired
    private DeductionService deductionService;
    
	protected BaseMapper<TransportSubsidy> getMapper() {
		return transportsubsidymapper;
	}
	
	private static boolean isNullAble(String nullable) {
        if ("YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase())) {
            return true;
        }
        return false;
    }
	
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment+"["+attName+"]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value "+decimal+ " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value "+decimal+ " scale out of range [" + scale + "].";
        }
        return "";
    }
	
	protected StringBuilder checkNewRecord(TransportSubsidy transportsubsidy) {
		StringBuilder s = new StringBuilder();
        s.append(checkBigDecimal("YES", "subsidyPrice", "金额(元)", transportsubsidy.getSubsidyPrice(), 10, 2));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transportsubsidy.getRemark(), 200));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建日期", transportsubsidy.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transportsubsidy.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(TransportSubsidy transportsubsidy) {
        StringBuilder s = checkNewRecord(transportsubsidy);
        s.append(CheckUtils.checkPrimaryKey(transportsubsidy.getTransportSubsidtId()));
		return s;
	}
	
	 @Override
	    public TransportSubsidy queryById(Long id) {
	        log.debug("query: {}",id);
	        return getMapper().selectByPrimaryKey(id);
	    }

	    @Override
	    public PageInfo<TransportSubsidy> queryList(Map<String, Object> map, int pageNum, int pageSize) {
	        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        PageHelper.startPage(pageNum, pageSize);
	        return new PageInfo<TransportSubsidy>(getMapper().selectList(map));
	    }
	    
	    @Override
	    public List<TransportSubsidy> queryList(Map<String, Object> map) {
	        log.debug("queryList: {}", map);
	        return getMapper().selectList(map);
	    }

	    @Override
	    public void add(TransportSubsidy transportSubsidy) {
	        log.debug("add: {}",transportSubsidy);
	        Environment env = Environment.getEnv();
	        StringBuilder s;
	        try {
	        	transportSubsidy.setCreateDate(new Date());
				transportSubsidy.setCreator(env.getUser().getUserId());
	            s = checkNewRecord(transportSubsidy);
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	            getMapper().insertSelective(transportSubsidy);
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
	    public void add(List<TransportSubsidy> records) {
	        log.debug("addBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	            for (int i = 0; i < records.size(); i++) {
	                TransportSubsidy record = records.get(i);
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
	    public void updateSelective(TransportSubsidy record) {
	        log.debug("updateSelective: {}",record);
	        try {
	            getMapper().updateByPrimaryKeySelective(record);
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
	    public void update(TransportSubsidy record) {
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
	                throw new RuntimeException("数据重复，请查证后重新提交！", e);
	            } else {
	                throw new RuntimeException("更新失败！", e);
	            }
	        }
	    }

	    @Override
	    public void update(List<TransportSubsidy> records) {
	        log.debug("updateBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	            for (int i = 0; i < records.size(); i++) {
	                TransportSubsidy record = records.get(i);
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
	        try {
	        	 getMapper().deleteByPrimaryKey(id);
			} catch (Exception e) {
		         if(e instanceof BusinessException){
						throw e;
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
	       
	    }

		@Override
		public List<TransportSubsidy> queryBySettlementId(Long transportSettlementId) {
	        log.debug("queryBySettlementIdId: {}", transportSettlementId);
	        List<TransportSubsidy> subsidys;
	        Deduction deduction;
	        try {
	        	subsidys = transportsubsidymapper.selectBySettlementIdList(transportSettlementId);
    			for (TransportSubsidy subsidy : subsidys) {
    				deduction = deductionService.queryById(subsidy.getDeductionId());
    				if(deduction != null) {
    					subsidy.setDeductionName(deduction.getDeductionName());
    				}
				}
			} catch (Exception e) {
		        if(e instanceof BusinessException){
						throw e;
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
	        return subsidys;
	    }

		@Override
		public List<TransportSubsidy> queryByTransportOrderId(Long transportOrderId) {
	        log.debug("queryByTransportOrderId: {}", transportOrderId);
	        List<TransportSubsidy> subsidys = null;
	        Deduction deduction;
	        TransportSettlement transportSettlement;
	        try {
	        	transportSettlement = transportSettlementService.queryByTransportOrderId(transportOrderId);
	        	subsidys = new ArrayList<>();
	        	if(transportSettlement != null) {
	        		if(transportSettlement.getTransportOrderId() != null) {
	        			subsidys = transportsubsidymapper.selectBySettlementIdList(transportSettlement.getTransportSettlementId());
	        			for (TransportSubsidy subsidy : subsidys) {
	        				deduction = deductionService.queryById(subsidy.getDeductionId());
	        				if(deduction != null) {
	        					subsidy.setDeductionName(deduction.getDeductionName());
	        				}
						}
	        		}
	        	}
			} catch (Exception e) {
		        if(e instanceof BusinessException){
						throw e;
				}else {
						throw new RuntimeException("操作失败！", e);
				}
		    }
	        return subsidys;
	    }

}
