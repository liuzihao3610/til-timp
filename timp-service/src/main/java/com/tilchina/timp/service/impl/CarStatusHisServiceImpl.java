package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.CarStatusHis;
import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.service.CarStatusHisService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CarStatusHisMapper;

/**
* 商品车历史明细表
*
* @version 1.0.0
* @author LiShuqi
*/
@Service
@Slf4j
public class CarStatusHisServiceImpl extends BaseServiceImpl<CarStatusHis> implements CarStatusHisService {

	@Autowired
    private CarStatusHisMapper carstatushismapper;
	
	@Override
	protected BaseMapper<CarStatusHis> getMapper() {
		return carstatushismapper;
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

	@Override
	protected StringBuilder checkNewRecord(CarStatusHis carstatushis) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "tractorVin", "车架号VIN", carstatushis.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("NO", "urgentStatus", "加急状态(0=一般", carstatushis.getUrgentStatus(), 10));
        s.append(CheckUtils.checkString("NO", "carTypeName", "车型名称", carstatushis.getCarTypeName(), 40));
        s.append(CheckUtils.checkString("NO", "brandName", "品牌名称", carstatushis.getBrandName(), 40));
        s.append(CheckUtils.checkString("NO", "categoryName", "类别名称", carstatushis.getCategoryName(), 40));
        s.append(CheckUtils.checkInteger("NO", "orderStatus", "当前订单状态", carstatushis.getOrderStatus(), 10));
        s.append(CheckUtils.checkString("NO", "deliveryUnitName", "当前发货单位名称", carstatushis.getDeliveryUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "receptionUnitName", "当前收货单位名称", carstatushis.getReceptionUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "customerCorpName", "客户名称", carstatushis.getCustomerCorpName(), 40));
        s.append(CheckUtils.checkDate("NO", "orderBillDate", "客户订单日期", carstatushis.getOrderBillDate()));
        s.append(CheckUtils.checkString("NO", "originalDeliveryUnitName", "发货单位名称", carstatushis.getOriginalDeliveryUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "originalReceptionUnitName", "收货单位名称", carstatushis.getOriginalReceptionUnitName(), 40));
        s.append(CheckUtils.checkDate("NO", "destinedLoadDate", "指定装车日期", carstatushis.getDestinedLoadDate()));
        s.append(CheckUtils.checkDate("NO", "destinedReceptionDate", "指定交车日期", carstatushis.getDestinedReceptionDate()));
        s.append(CheckUtils.checkString("NO", "driverName", "司机姓名", carstatushis.getDriverName(), 20));
        s.append(checkBigDecimal("YES", "customerQuotedPrice", "客户报价", carstatushis.getCustomerQuotedPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "freightPrice", "运价", carstatushis.getFreightPrice(), 10, 2));
        s.append(CheckUtils.checkInteger("YES", "carStatusStatus", "状态(0=未入库", carstatushis.getCarStatusStatus(), 10));
		return s;
	}
	
	@Override
	protected StringBuilder checkUpdate(CarStatusHis carstatushis) {
        StringBuilder s = checkNewRecord(carstatushis);
        s.append(CheckUtils.checkPrimaryKey(carstatushis.getCarStatusHisId()));
		return s;
	}
	
	
	
	@Override
    public void add(CarStatusHis record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCorpId(env.getCorp().getCorpId());
            carstatushismapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		carstatushismapper.deleteList(data);
		
	}
	

	
	@Override
 	@Transactional
    public void update(List<CarStatusHis> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		CarStatusHis record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
                carstatushismapper.updateByPrimaryKeySelective(record);
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }

    }

		@Override
		public void deleteByVin(String carVin) {
			if (null==carVin || 0==carVin.length()) {
				throw new BusinessException(LanguageUtil.getMessage("9999"));
			}
			carstatushismapper.deleteByVin(carVin);
			
		}		
		
		
		//部分更新
		@Override
	    public void updateSelective(CarStatusHis record) {
	        log.debug("updateSelective: {}",record);
	        try {
	        	StringBuilder s = new StringBuilder();
	            try {
	                s = s.append(CheckUtils.checkLong("NO", "carStatusHisId", "商品历史明细表ID", record.getCarStatusHisId(), 20));
	                if (!StringUtils.isBlank(s)) {
	                    throw new BusinessException("数据检查失败：" + s.toString());
	                }
	                CarStatusHis carStatusHis=carstatushismapper.selectByPrimaryKey(record.getCarStatusHisId());
	                if (carStatusHis==null) {
	                	throw new BusinessException("这条记录不存在!");
					}
	                carstatushismapper.updateByPrimaryKeySelective(record);
	            } catch (Exception e) {
	            	throw new BusinessException(e.getMessage());
	            }
	            
	        } catch (Exception e) {
	        	if (e.getMessage().indexOf("IDX_") != -1) {
	                throw new BusinessException("数据重复，请查证后重新提交！",e);
	            } else if(e instanceof BusinessException){
	                throw e;
	            }else {
	                throw new RuntimeException("操作失败");
	            }
	        }
	    }
		
		//通过ID查询
		@Override
	    public CarStatusHis queryById(Long id) {
	        log.debug("query: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "carStatusHisId", "商品历史明细表ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            return carstatushismapper.selectByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	    }
		//通过ID删除
		 @Override
	    public void deleteById(Long id) {
	        log.debug("delete: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "carStatusHisId", "商品历史明细表ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            carstatushismapper.deleteByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	        
	    }
}
