package com.tilchina.timp.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.vo.AppSettlementDetailVO;
import com.tilchina.timp.vo.AppSettlementVO;
import com.tilchina.timp.vo.DistributionDetailVO;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportSettlementMapper;

/**
* 运单结算主表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class TransportSettlementServiceImpl implements TransportSettlementService {

	@Autowired
    private TransportSettlementMapper transportsettlementmapper;
	
	@Autowired
    private TransportSubsidyService transportSubsidyService;
	
	@Autowired
    private TransportOrderService transportOrderService;
    
	@Autowired
    private StockCarService stockCarService;
	
	@Autowired
    private FreightService freightService;

	@Autowired
	private DriverSettlementService driverSettlementService;
	
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
	
	protected StringBuilder checkNewRecord(TransportSettlement transportsettlement) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", transportsettlement.getTransportOrderCode(), 20));
        s.append(checkBigDecimal("YES", "basicsPrice", "基础运价(元)", transportsettlement.getBasicsPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "subsidyPrice", "补贴金额(元)", transportsettlement.getSubsidyPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "allOrderPrice", "总运价(元)", transportsettlement.getAllOrderPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "grossMarginPrice", "毛利率(元)", transportsettlement.getGrossMarginPrice(), 10, 4));
        s.append(CheckUtils.checkDate("NO", "createDate", "制单日期", transportsettlement.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", transportsettlement.getCheckDate()));
		return s;
	}

	protected StringBuilder checkUpdate(TransportSettlement transportsettlement) {
        StringBuilder s = checkNewRecord(transportsettlement);
        s.append(CheckUtils.checkPrimaryKey(transportsettlement.getTransportSettlementId()));
		return s;
	}
	
	@Override
    public TransportSettlement queryById(Long transportSettlementId) {
        log.debug("TransportSettlement: {}",transportSettlementId);
        StringBuilder s;
        TransportSettlement settlement;
        StockCar stockCar;
        TransportOrder transportOrder;
		BigDecimal basicsPrice,allOrderPrice;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单接单主ID", transportSettlementId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    settlement =  transportsettlementmapper.selectByPrimaryKey(transportSettlementId);
   		    transportOrder = transportOrderService.queryById(settlement.getTransportOrderId());
			basicsPrice =  new BigDecimal(0.00);	//	基础运价(元)
   		    if(settlement != null) {
			    settlement.setTransport(transportOrder);
			    settlement.setSubsidys(transportSubsidyService.queryBySettlementId(settlement.getTransportSettlementId()));
			    if(settlement.getTransport() != null) {
					if(settlement.getTransport().getDetails() != null) {
						//	获取运价编号、运价类型
						freightService.getPrice(settlement.getTransport());
		        		for (TransportOrderDetail detail : settlement.getTransport().getDetails()) {
		        			//	设置运价编号、运价类型、最终价格
		        			detail.setFinalPrice(detail.getFreight().getFinalPrice());
		        			detail.setFreightCode(detail.getFreight().getFreightCode());
		        			detail.setFreightType(detail.getFreight().getFreightType());
							basicsPrice = basicsPrice.add(detail.getFreight().getFinalPrice());
		        			//	客户名称
		        			stockCar = new StockCar();
		    				stockCar.setCarrierId(settlement.getTransport().getCarriageCorpId());
		    				stockCar.setCarVin(detail.getCarVin());
		    				stockCar = stockCarService.queryByStockCar(stockCar);
		        			detail.setRefCustomerName(stockCar.getRefCustomerName());
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
        return settlement;
    }

    @Override
    public PageInfo<TransportSettlement> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        StockCar stockCar;
        TransportOrder transportOrder;
        PageInfo<TransportSettlement> pageInfo = null;
        try {
        	pageInfo = new PageInfo<TransportSettlement>(transportsettlementmapper.selectList(map));
        	for (TransportSettlement settlement : pageInfo.getList()) {
        		transportOrder = transportOrderService.queryById(settlement.getTransportOrderId());
        		settlement.setTransport(transportOrder);
        		if(settlement.getTransport() != null) {
        			if(settlement.getTransport().getDetails() != null) {
						//	获取运价编号、运价类型
						freightService.getPrice(transportOrder);
						transportOrderService.getCustomerPrice(transportOrder);
		        		for (TransportOrderDetail detail : settlement.getTransport().getDetails()) {
		        			// 设置运价编号、运价类型、最终价格
		        			detail.setFinalPrice(detail.getFreight().getFinalPrice());
		        			detail.setFreightCode(detail.getFreight().getFreightCode());
		        			detail.setFreightType(detail.getFreight().getFreightType());
		        			//	客户名称
		        			stockCar = new StockCar();
		    				stockCar.setCarrierId(transportOrder.getCarriageCorpId());
		    				stockCar.setCarVin(detail.getCarVin());
		    				stockCar = stockCarService.queryByStockCar(stockCar);
		        			detail.setRefCustomerName(stockCar.getRefCustomerName());
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
        return pageInfo;
    }
    
    @Override
    public List<TransportSettlement> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return transportsettlementmapper.selectList(map);
    }

    @Override
    public void add(TransportSettlement settlement) {
        log.debug("add: {}",settlement);
        StringBuilder s;
        try {
            s = checkNewRecord(settlement);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            transportsettlementmapper.insertSelective(settlement);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }

    @Override
    public void add(List<TransportSettlement> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	TransportSettlement record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            transportsettlementmapper.insert(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("批量保存失败！", e);
            }
        }
    }

    @Override
    public void updateSelective(TransportSettlement record) {

    }

    @Override
    public void update(TransportSettlement record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = checkUpdate(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            transportsettlementmapper.updateByPrimaryKey(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

    @Override
    public void update(List<TransportSettlement> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
            	TransportSettlement record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            transportsettlementmapper.update(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("批量更新失败！", e);
            }
        }

    }

    @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        transportsettlementmapper.deleteByPrimaryKey(id);
    }

	public Boolean exist(Long transportOrderId) {
		log.debug("TransportSettlement: {}",transportOrderId);
		StringBuilder s;
		TransportSettlement settlement;
		BigDecimal basicsPrice;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "运单主ID", transportOrderId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			settlement =  transportsettlementmapper.selectByTransportOrderId(transportOrderId);
			if(settlement == null){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Transactional
	@Override
	public TransportSettlement queryByTransportOrderId(Long transportOrderId) {
        log.debug("TransportSettlement: {}",transportOrderId);
        StringBuilder s;
        TransportSettlement settlement;
		BigDecimal basicsPrice;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "运单主ID", transportOrderId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			basicsPrice =  new BigDecimal(0.00);	//	基础运价(元)
   		    settlement =  transportsettlementmapper.selectByTransportOrderId(transportOrderId);
   		    if (settlement != null){
				settlement = queryById(settlement.getTransportSettlementId());
				settlement.setSubsidys(transportSubsidyService.queryBySettlementId(settlement.getTransportSettlementId()));
				if(settlement.getTransport() != null){
					if (settlement.getTransport().getDetails() != null){
						freightService.getPrice(settlement.getTransport());
						for (TransportOrderDetail detail : settlement.getTransport().getDetails()) {
							basicsPrice = basicsPrice.add(detail.getFreight().getFinalPrice());
						}
					}
				}
				if ( settlement.getBasicsPrice().compareTo(basicsPrice) != 0){
					programs(settlement.getTransportSettlementId(),settlement.getTransportOrderId());
				}
				BigDecimal grossMargin = Optional.ofNullable(settlement.getGrossMarginPrice()).orElse(BigDecimal.ZERO);
				grossMargin = grossMargin.multiply(new BigDecimal(100));
				settlement.setGrossMargin(grossMargin.toString()+"%");
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return settlement;
    }

	@Override
	public List<TransportSettlement> queryByTransportOrderId(List<Long> transportOrderIds) {
		log.debug("queryByTransportOrderId: {}",transportOrderIds);
		try {
			List<TransportSettlement> transportSettlements = new ArrayList<>();
			if(transportOrderIds.size() > 0){
				transportSettlements = transportsettlementmapper.selectByTransportOrderIds(transportOrderIds);
			}
			return transportSettlements;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void deleteByTransportOrderId(Long transportOrderId) {
		log.debug("deleteByTransportOrderId: {}",transportOrderId);
		try {
			if(!exist(transportOrderId)) {
				/*throw new BusinessException("请输入有效的运单id，此运单id："+transportOrderId+"无效！");*/
				transportsettlementmapper.deleteByTransportOrderId(queryByTransportOrderId(transportOrderId).getTransportSettlementId());
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
	public void deleteByTransportOrderId(int[] transportOrderIds) {
		log.debug("deleteByTransportOrderId: {}",transportOrderIds);
		StringBuilder s;
		try {
			s = new StringBuilder();
			for (int transportOrderId : transportOrderIds) {
				queryByTransportOrderId(Long.valueOf(transportOrderId));
				if(!exist(Long.valueOf(transportOrderId))) {
					s.append(transportOrderId+";");
				}
			}
			if(s.indexOf(";") != -1) {
				throw new BusinessException("请输入有效的运单id，此运单id："+s.toString()+"无效！");
			}
			transportsettlementmapper.deleteByTransportOrderIds(transportOrderIds);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Transactional
	@Override
	public void check(TransportOrder updateTransportOrder, int type) {
		log.debug("check: {},type{}",updateTransportOrder,type);
		TransportSettlement  querySettlement,updateSettlement;
		try {
			querySettlement = queryByTransportOrderId(updateTransportOrder.getTransportOrderId());
			updateSettlement = new TransportSettlement();
			if(type == 0) {
				updateSettlement.setTransportSettlementId(querySettlement.getTransportSettlementId());
				updateSettlement.setChecker(updateTransportOrder.getChecker());
				updateSettlement.setCheckDate(updateTransportOrder.getCheckDate());
			}else if(type == 1){
				updateSettlement.setTransportSettlementId(querySettlement.getTransportSettlementId());
				updateSettlement.setChecker(updateTransportOrder.getChecker());
				updateSettlement.setCheckDate(updateTransportOrder.getCheckDate());
			}
			transportsettlementmapper.check(updateSettlement);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Transactional
	@Override
	public void transmit(TransportOrder updateTransportOrder, int type) {
		log.debug("transmit: {},type{}",updateTransportOrder,type);
		try {
			TransportSettlement  querySettlement,updateSettlement;
			querySettlement = queryByTransportOrderId(updateTransportOrder.getTransportOrderId());
			if(type == 0) {
				updateSettlement = new TransportSettlement();
				updateSettlement.setTransportSettlementId(querySettlement.getTransportSettlementId());
				if(updateSettlement.getSubsidys() != null) {
					for (TransportSubsidy subsidy : updateSettlement.getSubsidys()) {
						subsidy.setFlag(0);
						transportSubsidyService.updateSelective(subsidy);
					}
				}
			}else if(type == 1){
				updateSettlement = new TransportSettlement();
				updateSettlement.setTransportSettlementId(querySettlement.getTransportSettlementId());
				if(updateSettlement.getSubsidys() != null) {
					for (TransportSubsidy subsidy : updateSettlement.getSubsidys()) {
						subsidy.setFlag(1);
						transportSubsidyService.updateSelective(subsidy);
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
	}
	
	@Transactional
	@Override
	public void bill(TransportOrder transportOrder) {
		log.debug("transmit: {}",transportOrder);
		try {
			TransportSettlement  querySettlement,updateSettlement;
			querySettlement = queryByTransportOrderId(transportOrder.getTransportOrderId());
			updateSettlement = new TransportSettlement();
			updateSettlement.setTransportSettlementId(querySettlement.getTransportSettlementId());
			if(updateSettlement.getSubsidys() != null) {
				for (TransportSubsidy subsidy : updateSettlement.getSubsidys()) {
					subsidy.setFlag(1);
					transportSubsidyService.updateSelective(subsidy);
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
	
	public void programs(Long transportSettlementId,Long transportSubsidyId) {
		TransportSettlement settlement;
		List<TransportSubsidy>  subsidys;
		TransportOrder transportOrder;
		List<TransportOrderDetail>  details;
		BigDecimal subsidyPrice,basicsPrice,allOrderPrice,grossMarginPrice,customerPrice,revenuePrice;
		try {
			settlement = queryById(transportSettlementId);
			transportOrder = transportOrderService.queryById(settlement.getTransportOrderId());
			details = transportOrder.getDetails();
			//	增加补贴项目后，需要重新计算结算信息主表相关价格。
			subsidyPrice = new BigDecimal(0.00);    //	补贴金额(元)
			basicsPrice = new BigDecimal(0.00);    //	基础运价(元)
			allOrderPrice = new BigDecimal(0.00);     //	总运价(元)
			grossMarginPrice = new BigDecimal(0.00);    //	毛利率(元)
			customerPrice = new BigDecimal(0.00);    //	客户报价
			/*revenuePrice = new BigDecimal(0.00);    //	销售收入*/
		    if(transportOrder.getDetails() != null && transportOrder.getDetails().size() > 0) {
				//	获取运价编号、运价类型
				freightService.getPrice(transportOrder);
				//	获取客户报价金额//	基础运价	客户报价
				transportOrderService.getCustomerPrice(transportOrder);
				for (TransportOrderDetail detail : transportOrder.getDetails()) {
					if (detail.getCustomerPrice() == null) {
						throw new BusinessException("获取客户报价金额失败！");
					}
					/*System.out.println(detail.getCarVin()+"======="+detail.getCustomerPrice());*/
					customerPrice = customerPrice.add(detail.getCustomerPrice());
					basicsPrice = basicsPrice.add(detail.getFreight().getFinalPrice());
				}
				if (transportSubsidyId != null) {
					transportSubsidyService.deleteById(transportSubsidyId);
				}
				//	查询补贴项目
				subsidys = transportSubsidyService.queryBySettlementId(settlement.getTransportSettlementId());
				for (TransportSubsidy subsidy : subsidys) {
					subsidyPrice = subsidyPrice.add(subsidy.getSubsidyPrice());
				}
				settlement.setBasicsPrice(basicsPrice);
				//	补贴金额(元)
				settlement.setSubsidyPrice(subsidyPrice);
				allOrderPrice = allOrderPrice.add(basicsPrice).add(subsidyPrice);
				//	总运价(元)
				settlement.setAllOrderPrice(allOrderPrice);
				//	毛利率(元)
			/*	revenuePrice = customerPrice.subtract(allOrderPrice);*/
				if (customerPrice.intValue() == 0) {
					grossMarginPrice = new BigDecimal(0.00);
				} else {
					grossMarginPrice = customerPrice.subtract(allOrderPrice).divide(customerPrice,4,BigDecimal.ROUND_HALF_DOWN).setScale(4, RoundingMode.HALF_DOWN);
				}
				settlement.setGrossMarginPrice(grossMarginPrice);
				updateSelective(settlement);
			}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Transactional
	@Override
	public void addPrograms(TransportSubsidy transportSubsidy) {
		log.debug("addPrograms: {}",transportSubsidy);
		try {
			//增加运单结算信息子表：各种补贴价格，新增时自动添加创建人、创建时间
			transportSubsidyService.add(transportSubsidy);
			programs(transportSubsidy.getTransportSettlementId(),null);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Transactional
	@Override
	public void delPrograms(Long transportSubsidyId) {
		log.debug("delPrograms: {}",transportSubsidyId);
		TransportSubsidy transportSubsidy;
		try {
			transportSubsidy = transportSubsidyService.queryById(transportSubsidyId);
			if(transportSubsidy == null) {
				throw new BusinessException("运单结算子表id:"+transportSubsidyId+"在运单结算子表中没有此条记录！");
			}
			programs(transportSubsidy.getTransportSettlementId(),transportSubsidyId);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void add(TransportOrder transportOrder) {
		log.debug("add: {}",transportOrder);
		TransportSettlement transportSettlement;
		BigDecimal basicsPrice,allOrderPrice,grossMarginPrice,customerPrice,revenuePrice;
		try {
			transportOrder = transportOrderService.queryById(transportOrder.getTransportOrderId());
			transportSettlement = new TransportSettlement();
            transportSettlement.setTransportOrderId(transportOrder.getTransportOrderId());
            transportSettlement.setCorpId(transportOrder.getCorpId());
            transportSettlement.setCreator(transportOrder.getCreator());
            transportSettlement.setCreateDate(transportOrder.getCreateDate());
            transportSettlement.setTransportOrderCode(transportOrder.getTransportOrderCode());
            transportSettlement.setTransport(transportOrder);
            //	基础运价、总运价、毛利率
            basicsPrice =  new BigDecimal(0.00);	//	基础运价(元)
            allOrderPrice = new BigDecimal(0.00);	 //	总运价(元)	
            grossMarginPrice =  new BigDecimal(0.00);	//	毛利率(元)
		    customerPrice =  new BigDecimal(0.00);	//	客户报价
		    revenuePrice =  new BigDecimal(0.00);	//	销售收入
			if(transportOrder.getDetails() != null && transportOrder.getDetails().size() > 0) {
				//	获取运价编号、运价类型
				freightService.getPrice(transportOrder);
				//	获取客户报价金额//	基础运价	客户报价
				transportOrderService.getCustomerPrice(transportOrder);
				for (TransportOrderDetail detail : transportOrder.getDetails()) {
					if (detail.getCustomerPrice() == null) {
						throw new BusinessException("获取客户报价金额失败！");
					}
					basicsPrice = basicsPrice.add(detail.getFreight().getFinalPrice());
					customerPrice = customerPrice.add(detail.getCustomerPrice());
				}
			}
            transportSettlement.setBasicsPrice(basicsPrice);
            allOrderPrice = allOrderPrice.add(basicsPrice);
            transportSettlement.setAllOrderPrice(allOrderPrice);
		    //	毛利率(元)
		    revenuePrice = customerPrice.subtract(allOrderPrice);
		    if(revenuePrice.intValue() == 0) {
			    grossMarginPrice = new BigDecimal(0.00);
		    }else {
				grossMarginPrice = customerPrice.subtract(allOrderPrice).divide(customerPrice,2,BigDecimal.ROUND_HALF_DOWN).setScale(4, RoundingMode.HALF_DOWN);
			}
		    transportSettlement.setGrossMarginPrice(grossMarginPrice);
            add(transportSettlement);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void updateSelective(Long transportOrderId){
		log.debug("updateSelective: {}",transportOrderId);
		TransportSettlement settlement;
		List<TransportSubsidy> subsidys;
		List<TransportOrderDetail> details;
		BigDecimal subsidyPrice,basicsPrice,allOrderPrice,grossMarginPrice,customerPrice,revenuePrice;
		TransportOrder transportOrder;
		try {
			settlement = queryByTransportOrderId(transportOrderId);
			if(settlement != null) {
			transportOrder = transportOrderService.queryById(transportOrderId);
			//	基础运价、总运价、毛利率
			basicsPrice =  new BigDecimal(0.00);	//	基础运价(元)
			allOrderPrice = new BigDecimal(0.00);	 //	总运价(元)
			grossMarginPrice =  new BigDecimal(0.00);	//	毛利率(元)
			subsidyPrice = new BigDecimal(0.00); 	//	补贴金额(元)
			customerPrice =  new BigDecimal(0.00);	//	客户报价
			revenuePrice =  new BigDecimal(0.00);	//	销售收入
			details = transportOrder.getDetails();
			if(details != null ) {
				//	获取运价编号、运价类型
				freightService.getPrice(transportOrder);
				//	获取客户报价金额//	基础运价	客户报价
				transportOrderService.getCustomerPrice(transportOrder);
				for (TransportOrderDetail detail : details) {
					if (detail.getCustomerPrice() == null) {
						throw new BusinessException("获取客户报价金额失败！");
					}
					basicsPrice = basicsPrice.add(detail.getFreight().getFinalPrice());
					customerPrice = customerPrice.add(detail.getCustomerPrice());
				}
			}
			//  查询补贴项目
			subsidys = transportSubsidyService.queryBySettlementId(settlement.getTransportSettlementId());
			for (TransportSubsidy subsidy : subsidys) {
				subsidyPrice = subsidyPrice.add(subsidy.getSubsidyPrice());
			}
			settlement.setBasicsPrice(basicsPrice);
			//	补贴金额(元)
			settlement.setSubsidyPrice(subsidyPrice);
			allOrderPrice = allOrderPrice.add(basicsPrice).add(subsidyPrice);
			//	总运价(元)
			settlement.setAllOrderPrice(allOrderPrice);
			//	毛利率(元)
			revenuePrice = customerPrice.subtract(allOrderPrice);
			if(revenuePrice.intValue() == 0) {
				grossMarginPrice = new BigDecimal(0.00);
			}else {
				grossMarginPrice = customerPrice.subtract(allOrderPrice).divide(customerPrice,2,BigDecimal.ROUND_HALF_DOWN).setScale(4, RoundingMode.HALF_DOWN);
			}
			settlement.setGrossMarginPrice(grossMarginPrice);
			updateSelective(settlement);
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
	public PageInfo<AppSettlementVO> queryAppSettlement(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryAppSettlement: {},{},{}",map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<TransportSettlement>  settlements = null;
		TransportSettlement settlement ;
		PageInfo<AppSettlementVO> appSettlements;
		AppSettlementVO appSettlement;
		Environment env = Environment.getEnv();
		List<TransportOrder> transports;
		try {
			if(map == null) {
				map = new HashMap<String, Object>();
			}
			//上个月一号
			map.put("startLoadingDate",  DateUtil.dateToString(DateUtil.getMonth(-1)));
			//当前时间
			map.put("endLoadingDate", DateUtil.dateToString(DateUtil.getDate(new Date(),1)));
			map.put("driverId",env.getUser().getUserId());
			map.put("billStatus","6");
			//查询运单已完成状态的运单
			transports = transportOrderService.queryByDriverId(map);
			settlements = new ArrayList<>();
			appSettlements = new PageInfo<AppSettlementVO>(new ArrayList<>());
			for (TransportOrder transportOrder : transports) {
				appSettlement = new AppSettlementVO();
				settlement = queryByTransportOrderId(transportOrder.getTransportOrderId());
				if(settlement != null) {
					settlements.add(settlement);
					appSettlement.setTransportOrderId(transportOrder.getTransportOrderId());
					appSettlement.setTransportOrderCode(transportOrder.getTransportOrderCode());
					appSettlement.setReceivingDate(transportOrder.getReceivingDate());
					appSettlement.setAllOrderPrice(settlement.getAllOrderPrice());
					appSettlements.getList().add(appSettlement);
				}
			}
			appSettlements.setTotal(settlements.size());
			
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return appSettlements;
	}

	@Override
	public AppSettlementDetailVO queryPrograms(Long transportOrderId) {
		log.debug("queryPrograms: {}",transportOrderId);
		TransportSettlement settlement ;
		List<DistributionDetailVO> distributions;
		TransportOrder transportOrder;
		AppSettlementDetailVO detail;
		try {
				settlement = queryByTransportOrderId(transportOrderId);
				transportOrder = transportOrderService.queryById(transportOrderId);
				detail = new AppSettlementDetailVO();
				distributions = new ArrayList<DistributionDetailVO>();
				if(settlement != null) {
					detail.setTransportOrderCode(settlement.getTransportOrderCode());
					detail.setReceivingDate(transportOrder.getReceivingDate());
					detail.setReceivingDate(transportOrder.getReceivingDate());
					detail.setAllOrderPrice(settlement.getAllOrderPrice());
					detail.setSubsidyPrice(settlement.getSubsidyPrice());
					detail.setSubsidys(settlement.getSubsidys());
					detail.setBasicsPrice(settlement.getBasicsPrice());
					if(transportOrder.getDetails() != null) {
						transportOrder.getDetails().stream().forEach( transportDetail ->{
							DistributionDetailVO distribution = new DistributionDetailVO();
							/*String brandName = Optional.ofNullable(transportDetail.getRefBrandName()).orElse(new String());
							String carTypeName = Optional.ofNullable(transportDetail.getRefCategoryName()).orElse(new String());*/
							if (!StringUtil.isEmpty(transportDetail.getRefBrandName())){
								distribution.setBrandName(transportDetail.getRefBrandName()+";");
							}
							if (!StringUtil.isEmpty(transportDetail.getRefCategoryName())){
								distribution.setBrandName(distribution.getBrandName()+transportDetail.getRefCategoryName()+";");
							}
							if (!StringUtil.isEmpty(transportDetail.getRefCarCode())){
								distribution.setBrandName(distribution.getBrandName()+transportDetail.getRefCarCode()+";");
							}

							distribution.setCarVin(transportDetail.getCarVin());
							distribution.setSendUnitAddr(transportDetail.getRefSendUnitAddr());
							distribution.setReceiveUnitName(transportDetail.getRefReceiveUnitAddr());
							if(transportOrder.getDetails() != null && transportOrder.getDetails().size() > 0) {
					   		     //	获取运价编号、运价类型
					   	   		freightService.getPrice(transportOrder);
					   	   		distribution.setFinalPrice(transportDetail.getFreight().getFinalPrice());
					   		    }
							distributions.add(distribution);
						});
					}
					detail.setDistributions(distributions);
				}
			
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return detail;
	}

	@Override
	public void maintainFreighPrice(List<TransportSettlement> transportSettlements, List<TransportOrderDetail> details) {
		log.debug("maintainFreighPrice: {},{}",transportSettlements,details);
		for (TransportSettlement settlement : transportSettlements) {
			BigDecimal basicsPrice = new BigDecimal(0.00);
			BigDecimal grossMarginPrice = new BigDecimal(0.00);
			BigDecimal customerPrice = new BigDecimal(0.00);
			BigDecimal allOrderPrice = new BigDecimal(0.00);
			if (settlement != null){
				settlement = queryById(settlement.getTransportSettlementId());
				settlement.setSubsidys(transportSubsidyService.queryBySettlementId(settlement.getTransportSettlementId()));
				if(settlement.getTransport() != null){
					if (settlement.getTransport().getDetails() != null){
						freightService.getPrice(settlement.getTransport());
						//	获取客户报价金额//	基础运价	客户报价
						transportOrderService.getCustomerPrice(settlement.getTransport());
						for (TransportOrderDetail transportOrderDetail : settlement.getTransport().getDetails()) {
							if (transportOrderDetail.getCustomerPrice() == null) {
								throw new BusinessException("获取客户报价金额失败！");
							}
							for (TransportOrderDetail  detail : details) {
								if (transportOrderDetail.getFreightId().longValue() == detail.getFreightId().longValue()){
									transportOrderDetail.setFinalPrice(detail.getFinalPrice());
								}
							}
							customerPrice = customerPrice.add(transportOrderDetail.getCustomerPrice());
							basicsPrice = basicsPrice.add(transportOrderDetail.getFinalPrice());
							}
						allOrderPrice = allOrderPrice.add(basicsPrice).add(settlement.getSubsidyPrice());
						grossMarginPrice = customerPrice.subtract(allOrderPrice).divide(customerPrice,4,BigDecimal.ROUND_HALF_DOWN).setScale(4, RoundingMode.HALF_DOWN);
						settlement.setGrossMarginPrice(grossMarginPrice);
						}
					}
				}
			updateSelective(transportSettlements);
		}
	}

	@Override
	public void updateSelective(List<TransportSettlement> transportSettlements) {
		log.debug("updateSelective: {}",transportSettlements);
		try {
			transportsettlementmapper.updateByPrimaryKeySelective(transportSettlements);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else {
				throw new RuntimeException("更新失败！", e);
			}
		}
	}


}
