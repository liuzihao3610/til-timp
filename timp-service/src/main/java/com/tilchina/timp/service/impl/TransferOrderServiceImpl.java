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
import com.tilchina.timp.mapper.TransferOrderMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
* 客户交接单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class TransferOrderServiceImpl extends BaseServiceImpl<TransferOrder> implements TransferOrderService {

	@Autowired
    private TransferOrderMapper transferordermapper;

	@Autowired
	private TransportOrderService transportOrderService;

	@Autowired
	private CarStatusService carStatusService;

	@Autowired
	private TransferOrderService transferOrderService;

	@Autowired
	private TransportOrderDetailService transportOrderDetailService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private UnitService unitService;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private AppTransOrderService apptransOrderservice;

	@Autowired
	private StockCarService stockCarService;

	@Autowired
	private OrderDetailMapper orderDetailMapper;


	@Override
	protected BaseMapper<TransferOrder> getMapper() {
		return transferordermapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransferOrder transferorder) {
		StringBuilder s = new StringBuilder();
		transferorder.setTransferOrderCode(DateUtil.dateToStringCode(new Date()));
        s.append(CheckUtils.checkString("YES", "transferOrderCode", "交接单编号", transferorder.getTransferOrderCode(), 20));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", transferorder.getCarVin(), 20));
        s.append(CheckUtils.checkString("YES", "scanPath", "扫描件路径", transferorder.getScanPath(), 100));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transferorder.getRemark(), 100));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=未签收", transferorder.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "receptionDate", "签收时间", transferorder.getReceptionDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransferOrder transferorder) {
        StringBuilder s = checkNewRecord(transferorder);
        s.append(CheckUtils.checkPrimaryKey(transferorder.getTransferOrderId()));
		return s;
	}

	@Override
	@Transactional
    public void add(TransferOrder transferOrder) {
        log.debug("add: {}",transferOrder);
        try {
        	StringBuilder s = new StringBuilder();
			s=checkNewRecord(transferOrder);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Order order=orderService.queryById(transferOrder.getOrderId());
            if (order.getOrderType()!=0) {
				throw new BusinessException("此订单不是原始订单,请核实!");
			}
            long corpCustomerId=order.getCorpCustomerId();
            long customerId=transferOrder.getCustomerId();
            if (corpCustomerId != customerId) {
            	throw new BusinessException("客户信息有误,请核实!");
			}
            Map<String, Object> map=new HashMap<>();
            map.put("carVin", transferOrder.getCarVin());
            map.put("orderId", transferOrder.getOrderId());
            OrderDetail orderDetail=orderDetailService.getByVin(map);
            if (orderDetail==null) {
				throw new BusinessException("车架号:"+transferOrder.getCarVin()+"不在原始订单中");
			}
            //获取运单子表
            map.remove("orderId");
            map.put("transportOrderId", transferOrder.getTransportOrderId());
            TransportOrderDetail transportOrderDetail=transportOrderDetailService.getTransOrder(map);
            if (transportOrderDetail==null) {
				throw new BusinessException("车架号:"+transferOrder.getCarVin()+"不在运单中");
			}
            //获取店
            Unit unit=unitService.queryById(transportOrderDetail.getReceiveUnitId());
            if (unit==null) {
				throw new BusinessException("收货单位:"+transportOrderDetail.getReceiveUnitId()+"不存在");
			}
// TODO LSQ - 隶属公司字段作废后代码逻辑需要更新
//            long superorCorpId=unit.getSuperorCorpId();
            long dealerCorpId=transferOrder.getDealerCorpId();
           /* if (superorCorpId != dealerCorpId) {
				throw new BusinessException("经销公司有误,请核实");
			}*/
            Environment environment=Environment.getEnv();
            transferOrder.setCorpId(environment.getCorp().getCorpId());

			transferordermapper.insertSelective(transferOrder);
        } catch (Exception e) {
        	log.error("交接单添加失败！",e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
            	throw new RuntimeException("操作失败");
            }
        }
    }
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		transferordermapper.deleteList(data);

	}



	 	@Override
	 	@Transactional
	    public void update(List<TransferOrder> records) {
	        log.debug("updateBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	        	if (null==records || 0==records.size()) {
	    			throw new BusinessException(LanguageUtil.getMessage("9999"));
	    		}
	        	for (int i = 0; i < records.size(); i++) {
	        		TransferOrder record = records.get(i);
	                StringBuilder check = checkUpdate(record);
	                if(!StringUtils.isBlank(check)){
	                    checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
	            	transferordermapper.updateByPrimaryKeySelective(records.get(i));
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


	 	//APP接口
	 	@Override
	 	@Transactional
		public List<Map<String, Object>> getCarStatus() {
	 		Environment environment=Environment.getEnv();
	 		Map<String , Object> map=new HashMap<String, Object>();
	 		map.put("driverId", environment.getUser().getUserId());
	 		map.put("billStatus", 2);
			List<TransportOrder> transportOrders=transportOrderService.queryList(map);
			if (transportOrders==null || transportOrders.size()==0) {
				throw new BusinessException("驾驶员没有未完成订单");
			}
			List<Map<String , Object>> list=new ArrayList<Map<String , Object>>();
			for (TransportOrder transportOrder : transportOrders) {
				Map<String , Object> m=new HashMap<String, Object>();
				long transportOrderId=transportOrder.getTransportOrderId();
				m.put("transportOrderId", transportOrderId);
				List<Map<String , Object>> carStatus=carStatusService.selectVinList(m);

				list.addAll(carStatus);
			}

			return list;

		}
	 	/**
	 	 * 更新交接单状态
	 	 */
		@Override
		@Transactional
		public void updateByVin(List<TransferOrder> transferOrders) {
			List<Map<String, Object>> maps=new ArrayList<>();
			for (TransferOrder transferOrder : transferOrders) {
				Map<String, Object> map=new HashMap<>();
				map.put("carVin", transferOrder.getCarVin());
				map.put("billStatus", transferOrder.getBillStatus());
				maps.add(map);
			}
			for (Map<String, Object> map : maps) {
				if (map==null || map.size()==0) {
					throw new BusinessException("参数为空");
				}
				Environment environment=Environment.getEnv();
				long driverId=environment.getUser().getUserId();
				String carVin=map.get("carVin").toString();
				map.put("driverId", driverId);
				//通过车架号和驾驶员ID查询运单
				TransportOrderDetail transportOrderDetail=transportOrderDetailService.getDetail(map);
				if (transportOrderDetail==null) {
					throw new BusinessException("车架号:"+carVin+"不在运单中");
				}
				map.put("transportOrderId", transportOrderDetail.getTransportOrderId());
				//通过车架号查询交接单
				TransferOrder transferOrder=transferordermapper.selectByVin(map);
				if (transferOrder==null) {
					throw new BusinessException("交接单不存在");
				}
				//通过车架号查询库存表
				StockCar stockCar=stockCarService.queryByTransOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
				if (stockCar.getCarStatus()!=10) {
					throw new BusinessException("车辆未到店,不能操作交接单");
				}
				//更新交接单
				transferordermapper.updateByVin(map);
				//更新库存表车架号状态
				stockCar.setTransferOrderStatus(Integer.parseInt(map.get("billStatus").toString()));
				stockCarService.updateTransStatus(stockCar);
				map.put("orderId", stockCar.getOrderId());
				Map<String, Object> m=new HashMap<>();
				m.put("orderId", stockCar.getOrderId());
				//通过车架号查询所有的子订单,更新交接单状态
				OrderDetail orderDetail=orderDetailService.getOne(map);
				orderDetail.setTransferOrderStatus(Integer.parseInt(map.get("billStatus").toString()));
				orderDetailService.updateSelective(orderDetail);
				//通过订单号查询所有的车架号
				boolean flag=false;
				List<StockCar> list=stockCarService.queryList(m);
				for (StockCar sto : list) {
					if (sto.getTransferOrderStatus() != null && sto.getTransferOrderStatus() == 0) {
						flag=true;
					}
				}
				if (!flag) {
					//若所有的交接单状态都不是未签收,则更新运单完成
					TransportOrder transportOrder=transportOrderService.queryById(transportOrderDetail.getTransportOrderId());
					for (TransportOrderDetail trans : transportOrder.getDetails()) {
						trans.setRowStatus(0);
					}
					transportOrder.setBillStatus(6);
					transportOrderService.updateSelective(transportOrder);
				}
			}


		}

		//部分更新
		@Override
		@Transactional
	    public void updateSelective(TransferOrder record) {
	        log.debug("updateSelective: {}",record);
	        	StringBuilder s = new StringBuilder();
	        try {
	                s = s.append(CheckUtils.checkLong("NO", "transferOrderId", "交接单ID", record.getTransferOrderId(), 20));
	                if (!StringUtils.isBlank(s)) {
	                    throw new BusinessException("数据检查失败：" + s.toString());
	                }
	                TransferOrder transferOrder=transferordermapper.selectByPrimaryKey(record.getTransferOrderId());
	                if (transferOrder==null) {
	                	throw new BusinessException("这条记录不存在!");
	    			}
	    			if (transferOrder.getReceptionDate()!=null){
						TransportOrderDetail transportOrderDetail=transportOrderDetailService.queryById(transferOrder.getTransportOrderDetailId());
						transportOrderDetail.setSignDate(record.getReceptionDate());
						transportOrderDetailService.updateSelective(transportOrderDetail);
					}
	                record.setTransferOrderCode(null);
	                transferordermapper.updateByPrimaryKeySelective(record);

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
	    public TransferOrder queryById(Long id) {
	        log.debug("query: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "transferOrderId", "交接单ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            return transferordermapper.selectByPrimaryKey(id);
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
	            s = s.append(CheckUtils.checkLong("NO", "transferOrderId", "交接单ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            transferordermapper.deleteByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }

	    }

		@Override
		public TransferOrder selectByVin(Map<String, Object> map) {

			return transferordermapper.selectByVin(map);
		}
		//获取所有的交接单
		 @Override
		 @Transactional
	    public PageInfo<TransferOrder> queryList(Map<String, Object> map, int pageNum, int pageSize) {
	        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        PageHelper.startPage(pageNum, pageSize);
			 try {
				 if (map!=null){
					 DateUtil.addTime(map);
				 }
			 } catch (Exception e) {
				 throw new BusinessException(e.getMessage());
			 }
	        List<TransferOrder> transferOrders=getMapper().selectList(map);
	        String path=null;
	        String scanPath=null;
	        for (TransferOrder transferOrder : transferOrders) {
				path=transferOrder.getScanPath();
				if (path!=null) {
					if (path.indexOf("img")!=-1) {
						scanPath=String.format("http://10.8.12.123/%s", path.substring(6, path.length()).replace('\\', '/'));
						transferOrder.setScanPath(scanPath);
					}
				}

			}
	        return new PageInfo<TransferOrder>(transferOrders);
	    }

	    @Override
	    @Transactional
	    public List<TransferOrder> queryList(Map<String, Object> map) {
	        log.debug("queryList: {}", map);
	        List<TransferOrder> transferOrders=getMapper().selectList(map);
	        String path=null;
	        String scanPath=null;
	        for (TransferOrder transferOrder : transferOrders) {
				path=transferOrder.getScanPath();
				if (path!=null) {
					if (path.indexOf("img")!=-1) {
						scanPath=String.format("http://10.8.12.123/%s", path.substring(6, path.length()).replace('\\', '/'));
						transferOrder.setScanPath(scanPath);
					}
				}
			}
	        return transferOrders;
	    }
	  //司机接单后生成交接单
	    @Override
	    @Transactional
	    public void addTranferOrder(TransportOrder transportOrder) {
	        log.debug("addTranferOrder: {}", transportOrder);
	        StockCar criteriaStockCar;
	        try {
				if (transportOrder==null) {
					throw new BusinessException("参数为空");
				}
				Environment env=Environment.getEnv();
				Map<String, Object> map=new HashMap<>();
				map.put("transportOrderId", transportOrder.getTransportOrderId());
				List<TransportOrderDetail> transportOrderDetails=transportOrderDetailService.queryByOrderId(map);
				if (transportOrderDetails==null) {
					throw new BusinessException("无运单子表");
				}
				TransferOrder transferOrder=new TransferOrder();
				Order order =new Order();
				List<OrderDetail> orderDetail=new ArrayList<>();
				Unit unit =new Unit();
				List<Contacts> contacts=new ArrayList<>();
				for (int i=0;i<transportOrderDetails.size();i++) {
					transferOrder.setTransferOrderCode(DateUtil.dateToStringCode(new Date()));
					map.put("carVin", transportOrderDetails.get(i).getCarVin());
					map.put("sendUnitId", transportOrderDetails.get(i).getSendUnitId());
					map.put("receiveUnitId", transportOrderDetails.get(i).getReceiveUnitId());
					criteriaStockCar = new StockCar();
					criteriaStockCar.setCarVin(transportOrderDetails.get(i).getCarVin());
					criteriaStockCar.setOrderDetailId(transportOrderDetails.get(i).getTransportOrderDetailId());//.setCarrierId(transportOrder.getCarriageCorpId());
					StockCar stockCar = stockCarService.queryByTransOrderDetailId(transportOrderDetails.get(i).getTransportOrderDetailId());

					/*StockCar stockCar=stockCarService.queryByCarVin(transportOrderDetails.get(i).getCarVin());*/
					//获取子订单
					List<Long> orderDetailIds=new ArrayList<>();
					orderDetailIds.add(stockCar.getOrderDetailId());
					orderDetail=orderDetailMapper.selectDetailByDetailIds(orderDetailIds);
					//获取原始订单
					order=orderService.queryById(orderDetail.get(0).getOriginalOrderId());
					transferOrder.setCustomerId(order.getCorpCustomerId());
					transferOrder.setCarVin(transportOrderDetails.get(i).getCarVin());
					transferOrder.setOrderId(order.getOrderId());
					Map<String, Object> m=new HashMap<>();
					m.put("carVin", transportOrderDetails.get(i).getCarVin());
					m.put("orderId", order.getOrderId());
					OrderDetail detail=orderDetailService.getByVin(m);
					transferOrder.setOrderDetailId(detail.getOrderDetailId());
					transferOrder.setTransportOrderId(transportOrder.getTransportOrderId());
					transferOrder.setTransportOrderDetailId(transportOrderDetails.get(i).getTransportOrderDetailId());
					unit=unitService.queryById(transportOrderDetails.get(i).getReceiveUnitId());
// TODO LSQ - 隶属公司字段作废后代码逻辑需要更新
//					transferOrder.setDealerCorpId(unit.getSuperorCorpId());
					transferOrder.setDealerUnitId(transportOrderDetails.get(i).getReceiveUnitId());
					transferOrder.setScanPath(null);
					transferOrder.setRemark(null);
					transferOrder.setBillStatus(0);
					transferOrder.setReceptionDate(null);
					transferOrder.setRefUserName(null);
					transferOrder.setCorpId(transportOrderDetails.get(i).getCorpId());
					transferordermapper.insertSelective(transferOrder);
				}
			} catch (Exception e) {
				throw e;
			}
	    }

		/**
		 * 获取所有的未签收交接单
		 */
		@Override
		@Transactional
		public List<Map<String, Object>> getOrders(Map<String, Object> map) {
			try {
				Environment environment=Environment.getEnv();
				map.put("driverId", environment.getUser().getUserId());
				List<Map<String, Object>> transferOrders=transferordermapper.getOrders(map);
				for (int i = 0; i < transferOrders.size(); i++) {
					int carStatus=Integer.parseInt(transferOrders.get(i).get("carStatus").toString());
					int transferOrderStatus=Integer.parseInt(transferOrders.get(i).get("transferOrderStatus").toString());
					if (carStatus==10 && transferOrderStatus==0) {
						transferOrders.get(i).put("otStatus", "到店");
					}else if (carStatus!=10 && transferOrderStatus==0) {
						if (carStatus==6) {
							transferOrders.get(i).put("otStatus", "已接单");
						}else if (carStatus==7) {
							transferOrders.get(i).put("otStatus", "已预约");
						}else if (carStatus==8) {
							transferOrders.get(i).put("otStatus", "已装车");
						}else if (carStatus==9) {
							transferOrders.get(i).put("otStatus", "在途");
						}
					}else if (carStatus==10 && transferOrderStatus==1) {
						transferOrders.get(i).put("otStatus", "压单");
					}else if (carStatus==10 && transferOrderStatus==2) {
						transferOrders.get(i).put("otStatus", "甩单");
					}else if (carStatus==10 && transferOrderStatus==3) {
						transferOrders.get(i).put("otStatus", "已签收");
					}else if (carStatus==10 && transferOrderStatus==4) {
						transferOrders.get(i).put("otStatus", "回单");
					}
				}
				return transferOrders;
			} catch (Exception e) {
				throw e;
			}
		}
		/**
		 * 添加照片
		 */
		@Override
		@Transactional
		public void addPhoto(MultipartFile file,Long transferOrderId) {
			try {
				StringBuilder s = new StringBuilder();
		        s.append(CheckUtils.checkLong("NO", "transferOrderId", "交接单ID", transferOrderId, 20));
		        if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
		        TransferOrder transferOrder=transferOrderService.queryById(transferOrderId);
		        if (transferOrder==null) {
					throw new BusinessException("交接单不存在");
				}
				String path=FileUtil.uploadFile(file, "transferOrder");
				transferOrder.setScanPath(path);
				transferordermapper.updateByPrimaryKeySelective(transferOrder);

			} catch (Exception e) {
				throw new BusinessException(e);
			}

		}
		/**
		 * 添加照片和交接单信息
		 */
		@Override
		@Transactional
		public void addInfoAndPhoto(TransferOrder transferOrder, MultipartFile file) {
			try {
				StringBuilder s = new StringBuilder();
				s.append(CheckUtils.checkString("NO", "carVin", "车架号", transferOrder.getCarVin(), 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new RuntimeException("数据检查失败：" + s.toString());
	            }
	            Environment environment=Environment.getEnv();
	            //获取原始客户订单
	            Map<String, Object> map=new HashMap<>();
	            map.put("carVin", transferOrder.getCarVin());
	            map.put("orderType", 0);
	            Order order=orderService.getOriginOrder(map);
	            if (order==null) {
					throw new BusinessException("车架号:"+transferOrder.getCarVin()+"不存在原始订单中");
				}
	            //获取原始订单的子订单,车架号=传入车架号
	            map.put("orderId", order.getOrderId());
	            OrderDetail orderDetail=orderDetailService.getByVin(map);
	            if (orderDetail==null) {
					throw new BusinessException("车架号:"+transferOrder.getCarVin()+"不在原始订单明细中");
				}
	            transferOrder.setCustomerId(order.getCorpCustomerId());
	            transferOrder.setOrderId(order.getOrderId());
	            transferOrder.setOrderDetailId(orderDetail.getOrderDetailId());
	            //获取运单子表
	            map.put("driverId", environment.getUser().getUserId());
	            //map.put("driverId", 499);
	            TransportOrderDetail transportOrderDetail=transportOrderDetailService.getTransOrder(map);
	            if (transportOrderDetail==null) {
					throw new BusinessException("车架号:"+transferOrder.getCarVin()+"不在运单中");
				}
	            transferOrder.setTransportOrderId(transportOrderDetail.getTransportOrderId());
	            transferOrder.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
	            //获取经销公司
	            Unit unit=unitService.queryById(transportOrderDetail.getReceiveUnitId());
	            if (unit==null) {
					throw new BusinessException("收货单位:"+transportOrderDetail.getReceiveUnitId()+"不存在");
				}
// TODO LSQ - 隶属公司字段作废后代码逻辑需要更新
//	            transferOrder.setDealerCorpId(unit.getSuperorCorpId());
	            transferOrder.setDealerUnitId(transportOrderDetail.getReceiveUnitId());
	            transferOrder.setCorpId(environment.getCorp().getCorpId());
	            //transferOrder.setCorpId(1l);
	            if (file!=null && file.getSize()!=0) {
	            	//生成照片路径
					String path=FileUtil.uploadFile(file, "transferOrder");
					transferOrder.setScanPath(path);
				}
				transferOrder.setTransferOrderCode(DateUtil.dateToStringCode(new Date()));

				transferordermapper.insertSelective(transferOrder);
			} catch (Exception e) {
				 if (e.getMessage().indexOf("IDX_") != -1) {
		                throw new RuntimeException("数据重复，请查证后重新保存！", e);
		            } else {
		                throw new BusinessException(e);
		            }
			}

		}

	/**
	 * 通过车架号.当前所在订单主.子表ID新增交接单
	 */
	@Override
	@Transactional
	public void addOrder(TransferOrder transferOrder) {
		try {
			//获取当前车架号所在的订单主表
			Order ord=orderService.queryById(transferOrder.getOrderId());
			//获取当前车架号所在的订单子表
			List<Long> orderDetailIds=new ArrayList<>();
			orderDetailIds.add(transferOrder.getOrderDetailId());
			List<OrderDetail> orderDetails=orderDetailMapper.selectDetailByDetailIds(orderDetailIds);
			Order originalOrder=null;
			for (OrderDetail orderDetail : orderDetails) {
				//获取车架号所在的原始订单
				originalOrder=orderService.queryById(orderDetail.getOriginalOrderId());
			}
			transferOrder.setTransferOrderCode(DateUtil.dateToStringCode(new Date()));
			transferOrder.setOrderId(originalOrder.getOrderId());
			transferOrder.setCustomerId(originalOrder.getCorpCustomerId());
			//获取车架号所在的原始订单子表
			Map<String, Object> ma=new HashMap<>();
			ma.put("orderId", originalOrder.getOrderId());
			ma.put("carVin", transferOrder.getCarVin());
			OrderDetail originalOrderDetail=orderDetailService.getByVin(ma);
			transferOrder.setOrderDetailId(originalOrderDetail.getOrderDetailId());
			//获取经销店和经销公司
			Unit unit=unitService.queryById(originalOrderDetail.getReceiveUnitId());
			transferOrder.setDealerUnitId(unit.getUnitId());
// TODO LSQ - 隶属公司字段作废后代码逻辑需要更新
//			transferOrder.setDealerCorpId(unit.getSuperorCorpId());
			//获取车架号当前所在的运单子表
			Map<String, Object> m=new HashMap<>();
			m.put("carriageCorpId", ord.getCorpCarrierId());
			m.put("carVin", transferOrder.getCarVin());
			TransportOrderDetail transportOrderDetail=transportOrderDetailService.getDetail(m);
			transferOrder.setTransportOrderDetailId(transportOrderDetail.getTransportOrderDetailId());
			transferOrder.setTransportOrderId(transportOrderDetail.getTransportOrderId());
			transferOrder.setBillStatus(0);
			Environment environment=Environment.getEnv();
			transferOrder.setCorpId(environment.getCorp().getCorpId());
			transferordermapper.insertSelective(transferOrder);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	@Transactional
	public void deleteByTransportOrderId(Long transportOrderId) {
		transferordermapper.deleteByTransportOrderId(transportOrderId);
	}

	@Override
	@Transactional
	public void updateReceive(Map<String, Object> map) {
		String transferOrderIds=map.get("transferOrderIds").toString();
		if (transferOrderIds==null){
			throw new BusinessException("交接单ID为空");
		}
		String[] tIds=transferOrderIds.split(",|,");
		TransferOrder transferOrder=null;
		for(int i=0;i<tIds.length;i++){
			long transferOrderId=Long.parseLong(tIds[i]);
			transferOrder=transferordermapper.selectByPrimaryKey(transferOrderId);
			if (transferOrder==null){
				throw new BusinessException("交接单ID:"+transferOrderId+"的交接单不存在为空");
			}
			transferOrder.setBillStatus(4);
			transferordermapper.updateByPrimaryKeySelective(transferOrder);
		}
	}


}
