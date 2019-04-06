package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OrderDetailMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.vo.PendingOrderVO;
import com.tilchina.timp.vo.QuotationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* 订单子表
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail> implements OrderDetailService {

	@Autowired
    private OrderDetailMapper orderdetailmapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired 
	private StockCarService stockCarService;
	
	@Autowired 
	private FreightService freightService;
	
	@Autowired 
	private UnitService unitService;
	
	@Autowired 
	private QuotationService quotationService;
	
	@Autowired
	private CorpService corpService;
 
	@Override
	protected BaseMapper<OrderDetail> getMapper() {
		return orderdetailmapper;
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
	protected StringBuilder checkNewRecord(OrderDetail orderdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "orderCode", "订单号", orderdetail.getOrderCode(), 20));
        s.append(CheckUtils.checkLong("NO", "carId", "商品车型号ID", orderdetail.getCarId(), 20));
        s.append(CheckUtils.checkLong("YES", "carTypeId", "商品车类型ID", orderdetail.getCarTypeId(), 20));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", orderdetail.getCarVin(), 20));
        s.append(CheckUtils.checkLong("NO", "brandId", "品牌ID", orderdetail.getBrandId(), 20));
        s.append(CheckUtils.checkString("YES", "instructionNumber", "指示书号", orderdetail.getInstructionNumber(), 20));
        s.append(CheckUtils.checkDate("NO", "claimLoadDate", "指定装车日期", orderdetail.getClaimLoadDate()));
        s.append(CheckUtils.checkDate("YES", "claimDeliveryDate", "指定交车日期", orderdetail.getClaimDeliveryDate()));
        s.append(CheckUtils.checkDate("YES", "eta", "船期", orderdetail.getEta()));
        s.append(CheckUtils.checkString("YES", "cancelReason", "计划取消原因", orderdetail.getCancelReason(), 200));
        s.append(CheckUtils.checkString("YES", "remark", "备注", orderdetail.getRemark(), 200));
        s.append(CheckUtils.checkString("YES", "productNumber", "生产号", orderdetail.getProductNumber(), 20));
        s.append(CheckUtils.checkInteger("YES", "urgent", "加急标志", orderdetail.getUrgent(), 10));
        s.append(CheckUtils.checkInteger("YES", "scheduleType", "排程类型", orderdetail.getScheduleType(), 10));
        s.append(CheckUtils.checkDate("YES", "confirmationDate", "财务确认日期", orderdetail.getConfirmationDate()));
        s.append(checkBigDecimal("YES", "customerQuotedPrice", "客户报价", orderdetail.getCustomerQuotedPrice(), 10, 2));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(OrderDetail orderdetail) {
        StringBuilder s = checkNewRecord(orderdetail);
        s.append(CheckUtils.checkPrimaryKey(orderdetail.getOrderDetailId()));
		return s;
	}

	@Override
	@Transactional
	public void add(OrderDetail record) {

	}

	@Override
	@Transactional
	public void add(Order order, OrderDetail record) {
		log.debug("add: {}",order,record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setOrderCode(order.getOrderCode());
            Unit send=unitService.queryById(record.getSendUnitId());
            if (send==null) {
				throw new BusinessException("发货单位不存在");
			}
            Unit receive=unitService.queryById(record.getReceiveUnitId());
            if (receive==null) {
				throw new BusinessException("收货单位不存在");
			}
            
            QuotationVO quotationVO=new QuotationVO();
            quotationVO.setCustomerId(order.getCorpCustomerId()); 
            quotationVO.setVendorCorpId(order.getCorpCarrierId());
            quotationVO.setSendCityId(send.getCityId());
            quotationVO.setRecvCityId(receive.getCityId());
            quotationVO.setCarModelId(record.getCarId());
            quotationVO.setCarTypeId(record.getCarTypeId());
            quotationVO.setCarBrandId(record.getBrandId());
            quotationVO.setJobType(order.getWorkType());
            quotationVO.setOrderDate(order.getOrderDate());
            Quotation quotation=quotationService.selectByQuotationVO(quotationVO);
            if (quotation==null) {
            	throw new BusinessException("无报价信息,请新增");
			}
            record.setClaimDeliveryDate(DateUtil.getDate(record.getClaimLoadDate(),quotation.getRefLeadTime()));
            //record.setClaimDeliveryDate(DateUtil.getDate(record.getClaimLoadDate(),7));
            record.setQuotedPriceId(quotation.getQuotationId());
            //record.setQuotedPriceId(1l);
            Freight freight=freightService.getFreight(order.getOrderDate(),send.getCityId(),receive.getCityId(),record.getBrandId(),record.getCarTypeId());
            if (freight==null) {
            	throw new BusinessException("无运价信息,请新增");
			}
            record.setFreightPriceId(freight.getFreightId());
            //record.setFreightPriceId(1l);
            orderdetailmapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw  new BusinessException(e);
            }
        }
	}
	
//	@Override
//    public void add(OrderDetail record) {
//        
//    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		orderdetailmapper.deleteList(data);
		
	}
	

	
	 	@Override
	 	@Transactional
	    public void update(List<OrderDetail> orderDetails) {
	        log.debug("update: {}",orderDetails);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	        	if (null==orderDetails || 0==orderDetails.size()) {
	    			throw new BusinessException(LanguageUtil.getMessage("9999"));
	    		}
	        	for (int i = 0; i < orderDetails.size(); i++) {
	        		OrderDetail record = orderDetails.get(i);
	                StringBuilder check = checkUpdate(record);
	                if(!StringUtils.isBlank(check)){
	                    checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
	        	   
	        	   orderdetailmapper.updateByPrimaryKeySelective(record);
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
		@Transactional
	    public List<OrderDetail> queryList(Map<String, Object> map) {
	        log.debug("queryList: {}", map);
	        if(map.get("pageNum")!=null && map.get("pageSize")!=null) {
	        	int pageNum= (int) map.get("pageNum");
		        int pageSize=(int) map.get("pageSize");
	        	PageHelper.startPage(pageNum, pageSize);
	        }
	        
	        List<OrderDetail> orderDetails=orderdetailmapper.selectList(map);
	        
	        return orderDetails;
	    }

		@Override
		public OrderDetail queryByVin(String  carVin) {
			try {
				
				OrderDetail orderDetail=orderdetailmapper.queryByVin(carVin);
				if (orderDetail==null) {
					throw new BusinessException("车架号不存在");
				}
				return orderDetail;
			} catch (Exception e) {
				throw e;
			}
			
		}
		//部分更新
		@Override
	    public void updateSelective(OrderDetail record) {
	        log.debug("updateSelective: {}",record);
	        	StringBuilder s = new StringBuilder();
	        try {
	                s = s.append(CheckUtils.checkLong("NO", "orderDetailId", "订单明细ID", record.getOrderDetailId(), 20));
	                if (!StringUtils.isBlank(s)) {
	                    throw new BusinessException("数据检查失败：" + s.toString());
	                }
	                OrderDetail orderDetail=orderdetailmapper.selectByPrimaryKey(record.getOrderDetailId());
	                if (orderDetail==null) {
	                	throw new BusinessException("订单明细不存在!");
	    			}
	                record.setOrderCode(null);
	                orderdetailmapper.updateByPrimaryKeySelective(record);
	            
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
	    public OrderDetail queryById(Long id) {
	        log.debug("query: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "orderDetailId", "订单明细ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            return orderdetailmapper.selectByPrimaryKey(id);
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
	            s = s.append(CheckUtils.checkLong("NO", "orderDetailId", "订单明细ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            orderdetailmapper.deleteByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	        
	    }

	@Override
	public OrderDetail queryForOutsourcingReconciliation(Map<String, Object> params) {
		try {
			OrderDetail orderDetail = orderdetailmapper.queryForOutsourcingReconciliation(params);
			return orderDetail;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
		public List<OrderDetail> queryByOrderId(Long data) {
			if (data==null) {
				throw new BusinessException("参数为空");
			}
			return orderdetailmapper.queryByOrderId(data);
		}

		@Override
		public void deleteByOrderId(Long orderId) {
			orderdetailmapper.deleteByOrderId(orderId);
			
		}

		@Override
		public List<OrderDetail> checkList(Map<String, Object> map) {
			try {
				if (map==null || map.size()==0) {
					throw new BusinessException("参数为空");
				}
//				if (null==map.get("orderId")) {
//					throw new BusinessException("未传入订单ID");
//				}
//				if (null==map.get("carVins")) {
//					throw new BusinessException("未传入车架号");
//				}
//				Order order=orderService.queryById(Long.parseLong(map.get("orderId").toString()));
//				if (order==null) {
//					throw new BusinessException("订单ID有误,订单不存在");
//				}
				List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
				if (null !=map.get("carVins")) {
					String obj= map.get("carVins").toString();
					String [] carVins=obj.split("\\n| |,|，");
					for (int i = 0; i < carVins.length; i++) {
						StockCar stockCar=stockCarService.queryByCarVin(carVins[i]);
						String carVin=carVins[i];
						map.put("carVin", carVin);
						List<OrderDetail> ods=orderDetailService.queryOrderDetail(map);
						orderDetails.addAll(ods);
					}
					
				}else if (null ==map.get("carVins")) {
					List<OrderDetail> ods=orderDetailService.queryOrderDetail(map);
					orderDetails.addAll(ods);
				}
				return orderDetails;
				
			} catch (Exception e) {
				throw e;
			}
			
		}

		@Override
		public List<OrderDetail> queryOrderDetail(Map<String, Object> map) {
			
			return orderdetailmapper.queryOrderDetail(map);
		}
		
		@Override
		public OrderDetail getByVin(Map<String, Object> map) {
			try {
				//Environment environment=Environment.getEnv();
				//Long corpCarrierId=environment.getCorp().getCorpId();
				OrderDetail orderDetail=orderdetailmapper.getByVin(map);
				if (orderDetail==null) {
					throw new BusinessException("车架号:"+map.get("carVin")+"不在订单中");
				}
				return orderDetail;
			} catch (Exception e) {
				throw e;
			}
			
		}

		@Override
		public List<PendingOrderVO> queryForAssembly(Map<String,Object> map){
			return orderdetailmapper.selectForAssembly(map);
		}

		@Override
		public PageInfo<OrderDetail> getReferenceList(Map<String, Object> map,int pageNum, int pageSize) {
			log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        PageHelper.startPage(pageNum, pageSize);
	        
			try {
				Corp corp=corpService.queryById(Long.parseLong(map.get("customerId").toString()));
				map.put("carStatus", 0);
				List<StockCar> stockCars=stockCarService.queryList(map);
				if (stockCars==null || stockCars.size()==0) {
					throw new  BusinessException(corp.getCorpName()+"没有在库的车架号");
				}
				List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
				OrderDetail orderDetail=new OrderDetail();
				for (StockCar stockCar : stockCars) {
					orderDetail=orderdetailmapper.queryByVin(stockCar.getCarVin());
					orderDetails.add(orderDetail);
				}
				return new PageInfo<OrderDetail>(orderDetails);
			} catch (Exception e) {
				throw e;
			}
			
		}

		@Override
		public OrderDetail getOne(Map<String, Object> map) {
			
			try {
				
				return orderdetailmapper.getOne(map);
			} catch (Exception e) {
				throw e;
			}
		}

	@Override
	public List<OrderDetail> queryByCarVin(String carVin) {

		try {
			List<OrderDetail> orderDetails = orderdetailmapper.queryByCarVin(carVin);
			return orderDetails;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

}

