package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CarStatusMapper;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.CarStatusHis;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.CarStatusHisService;
import com.tilchina.timp.service.CarStatusService;
import com.tilchina.timp.service.UnitService;
import com.tilchina.timp.service.WarehouseLoadingPlanService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* 商品车明细表
*
* @version 1.0.0
* @author LiShuqi
*/
@Service
@Slf4j
public class CarStatusServiceImpl extends BaseServiceImpl<CarStatus> implements CarStatusService {

	@Autowired
    private CarStatusMapper carstatusmapper;
	
	@Autowired 
	private CarStatusHisService carStatusHisService;
	
	@Autowired
	private CarStatusService carStatusService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private WarehouseLoadingPlanService warehouseLoadingPlanService;
	
	@Override
	protected BaseMapper<CarStatus> getMapper() {
		return carstatusmapper;
	}
	
	private static boolean isNullAble(String nullable) {
		return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
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
	protected StringBuilder checkNewRecord(CarStatus carstatus) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "carStatusId", "商品明细表ID", carstatus.getCarStatusId(), 11));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号VIN", carstatus.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("NO", "urgentStatus", "加急状态", carstatus.getUrgentStatus(), 10));
        s.append(CheckUtils.checkString("NO", "carTypeName", "车型名称", carstatus.getCarTypeName(), 40));
        s.append(CheckUtils.checkString("YES", "brandName", "品牌名称", carstatus.getBrandName(), 40));
        s.append(CheckUtils.checkString("YES", "categoryName", "类别名称", carstatus.getCategoryName(), 40));
        s.append(CheckUtils.checkInteger("NO", "orderStatus", "当前订单状态", carstatus.getOrderStatus(), 10));
        s.append(CheckUtils.checkString("NO", "deliveryUnitName", "当前发货单位名称", carstatus.getDeliveryUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "receptionUnitName", "当前收货单位名称", carstatus.getReceptionUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "customerCorpName", "客户名称", carstatus.getCustomerCorpName(), 40));
        s.append(CheckUtils.checkDate("NO", "orderCreateTime", "客户订单日期", carstatus.getOrderCreateTime()));
        s.append(CheckUtils.checkString("NO", "originalDeliveryUnitName", "发货单位名称", carstatus.getOriginalDeliveryUnitName(), 40));
        s.append(CheckUtils.checkString("NO", "originalReceptionUnitName", "收货单位名称", carstatus.getOriginalReceptionUnitName(), 40));
        s.append(CheckUtils.checkDate("NO", "destinedLoadDate", "指定装车日期", carstatus.getDestinedLoadDate()));
        s.append(CheckUtils.checkDate("NO", "destinedReceptionDate", "指定交车日期", carstatus.getDestinedReceptionDate()));
        s.append(CheckUtils.checkString("NO", "driverName", "司机姓名", carstatus.getDriverName(), 20));
        s.append(checkBigDecimal("YES", "customerQuotedPrice", "客户报价", carstatus.getCustomerQuotedPrice(), 20, 2));
        s.append(checkBigDecimal("YES", "freightPrice", "运价", carstatus.getFreightPrice(), 10, 2));
        s.append(CheckUtils.checkInteger("YES", "carStatusStatus", "状态(0=未入库", carstatus.getCarStatusStatus(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CarStatus carstatus) {
        StringBuilder s = checkNewRecord(carstatus);
        s.append(CheckUtils.checkPrimaryKey(carstatus.getCarStatusId()));
		return s;
	}
	
	
	@Override
	@Transactional
    public void add(CarStatus carStatus) {
        log.debug("add: {}",carStatus);
        StringBuilder s;
        try {
            s = checkNewRecord(carStatus);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            carStatus.setCorpId(env.getCorp().getCorpId());
            carstatusmapper.insertSelective(carStatus);
            
            CarStatusHis carStatusHis=new CarStatusHis();
            carStatusHis.setCarStatusId(carStatus.getCarStatusId());
            carStatusHis.setCarId(carStatus.getCarId());
            carStatusHis.setCarVin(carStatus.getCarVin());
            carStatusHis.setUrgentStatus(carStatus.getUrgentStatus());
            carStatusHis.setCarTypeId(carStatus.getCarTypeId());
            carStatusHis.setCarTypeName(carStatus.getCarTypeName());
            carStatusHis.setBrandId(carStatus.getBrandId());
            carStatusHis.setBrandName(carStatus.getBrandName());
            carStatusHis.setCategoryId(carStatus.getCategoryId());
            carStatusHis.setCategoryName(carStatus.getCategoryName());
            carStatusHis.setOrderId(carStatus.getOrderId());
            carStatusHis.setOrderDetailId(carStatus.getOrderDetailId());
            carStatusHis.setOrderStatus(carStatus.getOrderStatus());
            carStatusHis.setDeliveryUnitId(carStatus.getDeliveryUnitId());
            carStatusHis.setDeliveryUnitName(carStatus.getDeliveryUnitName());
            carStatusHis.setReceptionUnitId(carStatus.getReceptionUnitId());
            carStatusHis.setReceptionUnitName(carStatus.getReceptionUnitName());
            carStatusHis.setOriginalOrderId(carStatus.getOriginalOrderId());
            carStatusHis.setOriginalOrderDetailId(carStatus.getOrderDetailId());
            carStatusHis.setCustomerCorpId(carStatus.getCustomerCorpId());
            carStatusHis.setCustomerCorpName(carStatus.getCustomerCorpName());
            carStatusHis.setOrderBillDate(carStatus.getOrderCreateTime());
            carStatusHis.setOriginalDeliveryUnitId(carStatus.getOriginalDeliveryUnitId());
            carStatusHis.setOriginalDeliveryUnitName(carStatus.getOriginalDeliveryUnitName());
            carStatusHis.setOriginalReceptionUnitId(carStatus.getOriginalReceptionUnitId());
            carStatusHis.setOriginalReceptionUnitName(carStatus.getOriginalReceptionUnitName());
            carStatusHis.setDestinedLoadDate(carStatus.getDestinedLoadDate());
            carStatusHis.setDestinedReceptionDate(carStatus.getDestinedReceptionDate());
            carStatusHis.setTransportOrderId(carStatus.getTransportOrderId());
            carStatusHis.setDriverId(carStatus.getDriverId());
            carStatusHis.setDriverName(carStatus.getDriverName());
            carStatusHis.setCustomerQuotedPrice(carStatus.getCustomerQuotedPrice());
            carStatusHis.setFreightPrice(carStatus.getFreightPrice());
            carStatusHis.setCarStatusStatus(carStatus.getCarStatusStatus());
            carStatusHis.setSequence(carStatus.getSerialNumber());
            carStatusHis.setCorpId(carStatus.getCorpId());
            
            carStatusHisService.add(carStatusHis);
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
	@Transactional
    public void deleteById(Long id) {
		 log.debug("delete: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "carStatusId", "商品明细表ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	            carstatusmapper.deleteByPrimaryKey(id);
	            CarStatus carStatus=carstatusmapper.selectByPrimaryKey(id);
	            carStatusHisService.deleteByVin(carStatus.getCarVin());	            
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
        
        
    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		carstatusmapper.deleteList(data);
		for (int i = 0; i < data.length; i++) {
			CarStatus carStatus=carstatusmapper.selectByPrimaryKey((long) data[i]);
	        carStatusHisService.deleteByVin(carStatus.getCarVin());
		}
		
	}
	
	@Override
	@Transactional
    public void updateSelective(CarStatus carStatus) {
        log.debug("updateSelective: {}",carStatus);
        StringBuilder s = new StringBuilder();
        try {
        	s = checkNewRecord(carStatus);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            CarStatus cStatus=carstatusmapper.selectByPrimaryKey(carStatus.getCarStatusId());
            if (cStatus==null) {
            	throw new BusinessException("这条记录不存在!");
			}
            carstatusmapper.updateByPrimaryKeySelective(carStatus);
            
            CarStatusHis carStatusHis=new CarStatusHis();
            carStatusHis.setCarStatusId(carStatus.getCarStatusId());
            carStatusHis.setCarId(carStatus.getCarId());
            carStatusHis.setCarVin(carStatus.getCarVin());
            carStatusHis.setUrgentStatus(carStatus.getUrgentStatus());
            carStatusHis.setCarTypeId(carStatus.getCarTypeId());
            carStatusHis.setCarTypeName(carStatus.getCarTypeName());
            carStatusHis.setBrandId(carStatus.getBrandId());
            carStatusHis.setBrandName(carStatus.getBrandName());
            carStatusHis.setCategoryId(carStatus.getCategoryId());
            carStatusHis.setCategoryName(carStatus.getCategoryName());
            carStatusHis.setOrderId(carStatus.getOrderId());
            carStatusHis.setOrderDetailId(carStatus.getOrderDetailId());
            carStatusHis.setOrderStatus(carStatus.getOrderStatus());
            carStatusHis.setDeliveryUnitId(carStatus.getDeliveryUnitId());
            carStatusHis.setDeliveryUnitName(carStatus.getDeliveryUnitName());
            carStatusHis.setReceptionUnitId(carStatus.getReceptionUnitId());
            carStatusHis.setReceptionUnitName(carStatus.getReceptionUnitName());
            carStatusHis.setOriginalOrderId(carStatus.getOriginalOrderId());
            carStatusHis.setOriginalOrderDetailId(carStatus.getOrderDetailId());
            carStatusHis.setCustomerCorpId(carStatus.getCustomerCorpId());
            carStatusHis.setCustomerCorpName(carStatus.getCustomerCorpName());
            carStatusHis.setOrderBillDate(carStatus.getOrderCreateTime());
            carStatusHis.setOriginalDeliveryUnitId(carStatus.getOriginalDeliveryUnitId());
            carStatusHis.setOriginalDeliveryUnitName(carStatus.getOriginalDeliveryUnitName());
            carStatusHis.setOriginalReceptionUnitId(carStatus.getOriginalReceptionUnitId());
            carStatusHis.setOriginalReceptionUnitName(carStatus.getOriginalReceptionUnitName());
            carStatusHis.setDestinedLoadDate(carStatus.getDestinedLoadDate());
            carStatusHis.setDestinedReceptionDate(carStatus.getDestinedReceptionDate());
            carStatusHis.setTransportOrderId(carStatus.getTransportOrderId());
            carStatusHis.setDriverId(carStatus.getDriverId());
            carStatusHis.setDriverName(carStatus.getDriverName());
            carStatusHis.setCustomerQuotedPrice(carStatus.getCustomerQuotedPrice());
            carStatusHis.setFreightPrice(carStatus.getFreightPrice());
            carStatusHis.setCarStatusStatus(carStatus.getCarStatusStatus());
            carStatusHis.setSequence(carStatus.getSerialNumber());
            carStatusHis.setCorpId(carStatus.getCorpId());
            
            carStatusHisService.add(carStatusHis);
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
	 	@Transactional
	    public void update(List<CarStatus> records) {
	        log.debug("updateBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	        	if (null==records || 0==records.size()) {
	    			throw new BusinessException(LanguageUtil.getMessage("9999"));
	    		}
	            for (int i = 0; i < records.size(); i++) {
	            	CarStatus record = records.get(i);
	                StringBuilder check = checkUpdate(record);
	                if(!StringUtils.isBlank(check)){
	                	checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
	            	carStatusService.updateSelective(records.get(i));
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
		public CarStatus queryByVin(Map<String, Object> map) {
			
			return carstatusmapper.queryByVin(map);
		}	
		
		//通过ID查询
		@Override
	    public CarStatus queryById(Long id) {
	        log.debug("query: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "carStatusId", "商品明细表ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            return carstatusmapper.selectByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	    }

		@Override
		public List<Map<String , Object>> selectVinList(Map<String, Object> m) {
			
			return carstatusmapper.selectVinList(m);
		}

		@Override
		public List<CarStatus> getCarList(Map<String, Object> map) {
			 log.debug("updateSelective: {}",map);
		        try {
		            if (map==null || map.size()==0) {
		            	throw new BusinessException("经纬度为空!");
					}
		            
		            Unit unit=unitService.getSingleUnit(map);
		            if (unit==null) {
		            	throw new BusinessException("没有与之匹配的经销店!");
					}
		            List<CarStatus> carVins=carStatusService.queryByUnitId(unit.getUnitId());
		            return carVins;
		            
			} catch (Exception e) {
				throw e;
			}
			
		}

		@Override
		public List<CarStatus> queryByUnitId(Long unitId) {
			
			return carstatusmapper.queryByUnitId(unitId);
		}
		
		@Transactional
		@Override
		public void updateBillStatus(String carVin, String path) {
			
			CarStatus carStatus=carStatusService.selectByVin(carVin);
			if (carStatus==null) {
				throw new BusinessException("车架号"+carVin+"不存在");
			}
			if (path.equals("/s1/app/load/updateBillStatus")) {
				carStatusService.updateStatus(carVin);
				/*warehouseLoadingPlanService.updateCarVin(carVin,3);*/
			} else if (path.equals("/s1/app/arrival/updateBillStatus")) {
				carstatusmapper.updateBillStatus(carVin);
			}
			
		}

		@Override
		public CarStatus selectByVin(String carVin) {
			
			return carstatusmapper.selectByVin(carVin);
		}

	//扫描装车
	@Override
	public void updateStatus(String carVin) {
		carstatusmapper.updateStatus(carVin);

	}
		
	 	
	 	
	 	
}
