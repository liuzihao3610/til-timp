package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.GaodeApi;
import com.tilchina.timp.vo.TransportPlanVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
@Slf4j
public class AppTransOrderServiceImpl  implements AppTransOrderService{
	
	@Autowired
    private TransportOrderService transportOrderService;
	
	@Autowired
    private TransportPlanService transportPlanService;
	
	@Autowired
    private TransportOrderDetailService transportOrderDetailService;

	@Autowired
	private UnitService unitService;
	
	@Autowired
	private ContactsOldService contactsService;
	
	@Autowired
	private TransporterService transporterService;
	
	@Autowired
	private TransferOrderService transferOrderService;
	
	@Override
	public List<TransportOrder> appQueryList(Map<String, Object> map) {
		log.debug("appQueryList: {}",map);
		StringBuilder s;
		List<TransportOrder> transportOrders = null;
		List<TransportOrderDetail> detail = null;
		Environment env = Environment.getEnv();
		try {
			if(!map.isEmpty()) {
				s = new StringBuilder();
				s.append(CheckUtils.checkString("YES", "transportOrderCode", "运单号", map.get("transportOrderCode").toString(), 20));
				if (!StringUtils.isBlank(s)) {
	                 throw new BusinessException("数据检查失败：" + s.toString());
	            }
			}
			//驾驶员
			map.put("driverId",env.getUser().getUserId());
			//运单状态
			map.put("appBillStatus", "get");
			transportOrders = transportOrderService.queryByDriverId(map);
			detail = new ArrayList<>();
			//运单信息
			for (TransportOrder transportOrder : transportOrders) {
				map.put("transportOrderId", transportOrder.getTransportOrderId());
				//货物明细
				detail = transportOrderDetailService.appQueryList(map);
				//收发货单位联系人
				for (TransportOrderDetail transportOrderDetail : detail) {
					transportOrderDetail.setEndContactVOs(contactsService.queryByEndUnitId(transportOrderDetail.getReceiveUnitId()));
					transportOrderDetail.setStartContactVOs(contactsService.queryByStartUnitId(transportOrderDetail.getSendUnitId()));
				}
				transportOrder.setDetails(detail);
			}
		} catch (Exception e) {
			throw new RuntimeException("查询失败！"+e.getMessage(), e);
		}
		return transportOrders;
	}
	
	@Override
	public List<TransportOrder> appQueryListByPrimary(Map<String, Object> map) {
		log.debug("appQueryListByPrimary: {}",map);
		StringBuilder s;
		List<TransportOrder> transportOrders = null;
		List<TransportOrderDetail> detail = null;
		//只显示当前司机
		Environment env = Environment.getEnv();
		//
		Map<String, Object> mapDetail;
		try {
			if(!map.isEmpty()) {
				s = new StringBuilder();
				if(map.get("startLoadingDate") != null) {
					s.append(CheckUtils.checkDate("NO", "startLoadingDate", "开始时间", DateUtil.stringToDateL(map.get("startLoadingDate").toString())));
				}
				if(map.get("endLoadingDate") != null) {
					s.append(CheckUtils.checkDate("NO", "endLoadingDate", "结束时间",  DateUtil.stringToDateL(map.get("endLoadingDate").toString())));
				}
				if(map.get("transportOrderCode") != null) {
					s.append(CheckUtils.checkString("NO", "transportOrderCode", "运单号", map.get("transportOrderCode").toString(), 20));
				}
				if (map.get("tractirPlateCode") != null) {
					s.append(CheckUtils.checkString("NO", "tractirPlateCode", "车牌号", map.get("tractirPlateCode").toString(), 20));
				}
				if (map.get("billStatus") != null) {
					s.append(CheckUtils.checkString("NO", "billStatus", "作业类型间", map.get("billStatus").toString(), 20));
				}
				if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
			}else {
        		throw new BusinessException("9001","传入参数");
            }
			mapDetail = new HashMap<>();
			mapDetail.put("driverId",env.getUser().getUserId());
			/*mapDetail.put("driverId",479);*/
			if(map.get("carVin") != null) {
				mapDetail.put("carVin", map.get("carVin"));
			}
			if(map.get("searchContent") != null) {
				mapDetail.put("searchContent", map.get("searchContent"));
			}
			//驾驶员
			map.put("driverId", env.getUser().getUserId());
			/*map.put("driverId", 479);*/
			//运单状态
			map.put("appBillStatus", "query");		
			//运单信息
			transportOrders = transportOrderService.queryByDriverId(map);
			for (TransportOrder transportOrder : transportOrders) {
				mapDetail.put("transportOrderId", transportOrder.getTransportOrderId());
				//货物明细
				detail = transportOrderDetailService.appQueryList(mapDetail);
				//收发货单位联系人
				for (TransportOrderDetail transportOrderDetail : detail) {
					transportOrderDetail.setStartContactVOs(contactsService.queryByStartUnitId(transportOrderDetail.getSendUnitId()));
					transportOrderDetail.setEndContactVOs(contactsService.queryByEndUnitId(transportOrderDetail.getReceiveUnitId()));
					/*transportOrderDetail.setContacts(contactsService.appQueryByUnitId(transportOrderDetail.getSendUnitId(),transportOrderDetail.getReceiveUnitId()));*/
				}
				transportOrder.setDetails(detail);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transportOrders;
	}
	
	@Override
	@Transactional
	public void updateStatusCheck(TransportOrder record) {
		log.debug("updateStatus: {}",record);
		StringBuilder s;
		Environment env = Environment.getEnv();
		Map<String, Object> map = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单ID", record.getTransportOrderId(), 20));
			if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			map = new HashMap<String, Object>();
			map.put("driverId", env.getUser().getUserId());
			/*map.put("driverId", Long.valueOf(537));*/
			map.put("billStatus", 2);
			if(transportOrderService.queryByDriverId(map).size() <= 0) {
				throw new BusinessException("您目前没有接单，请先接单！");
			}
			record.setDriverId(env.getUser().getUserId());
			/*record.setDriverId(Long.valueOf(537));*/
			transportOrderService.queryById(record.getTransportOrderId());
			//运单状态:已接单
			record.setBillStatus(3);
			transportOrderService.updateSelective(record);
			//生成交接单
			transferOrderService.addTranferOrder(record);
			//维护运单车架号状态状态
			transportOrderService.updateCarStatus(record.getTransportOrderId(), 6, 0);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		
	}
	
	@Override
	public void updateStatusRun(TransportOrder record) {
		log.debug("updateStatus: {}",record);
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transportOrderId", "运单ID", record.getTransportOrderId(), 20));
			if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			transportOrderService.queryById(record.getTransportOrderId());
			//运单状态:在途
			record.setBillStatus(4);
			transportOrderService.updateSelective(record);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}
	
	@Override
	public TransportPlanVO  appQueryListRoute(Map<String, Object> map) {
		log.debug("appQueryListRoute: {}",map);
		Environment env = Environment.getEnv();
		StringBuilder str,description;
		TransportOrder transportOrder = null;
		List<TransportOrderDetail> transportOrderDetails = null;
		Transporter truck = null;
		Double predictKm = 0.00;
		Unit startUnit = null,endUnit = null;
		int unitCount = 0;
		TransportPlanVO transportPlanVO = null;
		List<TransportPlan> transportPlans = null;
		try {
			str = new StringBuilder();
			description = new StringBuilder();
			if(map.get("transportOrderId") != null) {
				str.append(CheckUtils.checkLong("NO", "transportOrderId", "运单ID", Long.parseLong((map.get("transportOrderId").toString())), 20));
				if (!StringUtils.isBlank(str)) {
	                throw new BusinessException("数据检查失败：" + str.toString());
	            }
				//运单主子表
				transportOrder = transportOrderService.queryById(Long.parseLong((map.get("transportOrderId").toString())));
				transportOrderDetails = transportOrder.getDetails();
				if(transportOrderDetails.size() > 0) {
					map.put("driverId",transportOrderDetails.get(0).getDriverId());
				}
			}else{
				//运单主子表
				transportOrderDetails = transportOrderDetailService.queryByDriverId(env.getUser().getUserId());
				if(transportOrderDetails.size() > 0) {
					transportOrder = transportOrderService.queryById(transportOrderDetails.get(0).getTransportOrderId());
					map.put("transportOrderId",transportOrderDetails.get(0).getTransportOrderId());
					map.put("driverId",transportOrderDetails.get(0).getDriverId());
				}
			}
			Optional.ofNullable(transportOrder).orElseThrow(() -> new BusinessException("您目前尚未接受运单，请先接受运单再查看地图信息！"));
			//运单id、运单编号
			transportPlanVO = new TransportPlanVO();
			transportPlanVO.setTransportOrderId(transportOrder.getTransportOrderId());
			transportPlanVO.setTransportOrderCode(transportOrder.getTransportOrderCode());
			//货车信息:车牌号码(省份）、车牌号码（字母及数字）
			truck = transporterService.queryById(transportOrder.getTransporterId());
			truck.setAppTractirPlateProvince(CheckUtil.province(truck.getTractirPlateCode()));
			truck.setAppTractirPlateCode(CheckUtil.lettersAndNumbers(truck.getTractirPlateCode()));
			transportPlanVO.setTruck(truck);
			//停靠点路线
			transportPlans = transportPlanService.queryList(map);
			transportPlanVO.setArrivalPositions(transportPlans);
			//路线描述
			for (int i = 0; i < transportPlans.size(); i++) {
				TransportPlan transportPlan = transportPlans.get(i);
				predictKm += transportPlan.getDriving();
				if(transportPlan.getEndUnitId() != null) {
					endUnit = unitService.queryById(transportPlan.getEndUnitId());
					//单位类型(0=经销店 1=仓库 2=展馆),特殊经销店(0=否 1=是)
					if(endUnit.getUnitType().intValue() == 0 || endUnit.getSpecial().intValue() == 1) {
						unitCount += 1;
					}
				}
				if(i == 0) {
					if(endUnit != null) {
						//起始地
						transportPlanVO.setStartUnitName(endUnit.getRefCityName());
						String loadType = transportPlan.getLoadType() != 0 ? (transportPlan.getLoadType() != 1 ? "非满载":"空载"):"满载";
						description.append(endUnit.getRefCityName()+"出发，"+endUnit.getUnitName()+"装"+transportPlan.getCarCount()+"辆"
									+"【"+loadType+"】行驶"+transportPlan.getDriving()+"公里到达");
					}
				}else {
					description.append(endUnit.getRefCityName()+",");
					if(transportPlan.getCarCount() > 0) {
						//装载
						description.append("装"+"【"+transportPlan.getCarCount()+"】"+"辆商品车；");
					}
					if(transportPlan.getStartUnitId() != null) {
						startUnit = unitService.queryById(transportPlan.getStartUnitId());
						description.append(startUnit.getRefCityName()+"个经销店");
					}
					if(transportPlan.getHandingCount() > 0) {
						//卸载
						description.append("卸"+"【"+transportPlan.getHandingCount()+"】"+"辆商品车；");
					} 
					description.append("继续行驶"+transportPlan.getDriving()+"公里到达");
				}
				if(transportPlans.size() == i+1){
					description.append("行程结束。");
				}
			}
			List<TransportPlan> unique = transportPlans.stream().collect(
		                collectingAndThen(
		                        toCollection(() -> new TreeSet<>(comparingLong(TransportPlan:: getEndUnitId))), ArrayList::new)
		        );
			//城市数量
			transportPlanVO.setCityCount(unique.size());
			//预计公里数
			transportPlanVO.setPredictKm(predictKm);
			//经销店数量
			transportPlanVO.setUnitCount(unitCount);
			//路线描述
			transportPlanVO.setDescription(description);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transportPlanVO;
	}
	
	@Override
	public List<ContactsOld> appQueryUnitContacts(Map<String, Object> map)  throws BusinessException {
		log.debug("appQueryUnitContacts: {}",map);
		List<ContactsOld> contacts = null;
		List<TransportOrder> transportOrders = null;
		List<TransportOrderDetail> transportOrderDetails = null;
		Map<String, Object> mapDetail = null,mapTransport = null;
		List<Unit> units = null;
		String endLonLat = null;
		Double dist;
		Environment env = Environment.getEnv();
		try {
			dist = new Double(0.00);
			contacts = new ArrayList<>();
			units = new ArrayList<>();
			mapDetail = new HashMap<>();
			mapTransport = new HashMap<>();
			transportOrderDetails = new ArrayList<>();
			if(map.get("lnt") != null && map.get("lat") != null) {
				endLonLat = map.get("lnt").toString()+","+map.get("lat").toString();
			}else{
                throw new BusinessException("数据检查失败,请先选择坐标地址！");
            }
			mapTransport.put("driverId", env.getUser().getUserId());
			transportOrders = transportOrderService.queryByDriverId(mapTransport);
			if(transportOrders.size()  <= 0) {
				throw new BusinessException("您目前尚未接受运单，请先接受运单再查看地图信息！");
			}
			for (TransportOrder transportOrder : transportOrders) {
				mapDetail.put("driverId",transportOrder.getDriverId());
				mapDetail.put("transportOrderId",transportOrder.getTransportOrderId());
				transportOrderDetails.addAll(transportOrderDetailService.queryList(mapDetail));
				
			}
			for (TransportOrderDetail transportOrderDetail :transportOrderDetails) {
				if(unitService.queryById(transportOrderDetail.getReceiveUnitId()) != null) {
					units.add(unitService.queryById(transportOrderDetail.getReceiveUnitId()));
				}
			}
			for (Unit unit : units.stream().collect(collectingAndThen( toCollection(() -> new TreeSet<>(comparingLong(Unit::getUnitId))), ArrayList::new))) {
				dist = GaodeApi.getDistance(unit.getLng()+","+unit.getLat(), endLonLat);
				if(dist <= 150) {
					contacts.addAll(contactsService.queryByUnitId(unit.getUnitId()));
				}
			}
			if(contacts.size() <= 0) {
				throw new BusinessException("查询您目前所接运单中经销店地址，该附近没有经销店！请检查目前运输路线是否正确！");
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return contacts;
	}

	@Override
	public List<TransportPlanVO> appQueryListAassistRoute(Map<String, Object> map) {
		log.debug("appQueryListAassistRoute: {}",map);
		Environment env = Environment.getEnv();
		List<TransportPlanVO> transportPlanVOs = null;
		StringBuilder description;
		TransportOrder transportOrder = null;
		List<TransportOrderDetail> transportOrderDetails = null;
		Transporter truck = null;
		Double predictKm = 0.00;
		Unit startUnit = null,endUnit = null;
		int unitCount = 0;
		TransportPlanVO transportPlanVO = null;
		List<TransportPlan> transportPlans = null;
		try {
			description = new StringBuilder();
			//运单主子表
			transportOrderDetails = transportOrderDetailService.queryByDriverId(env.getUser().getUserId());
			transportPlanVOs = new ArrayList<TransportPlanVO>();
			for (TransportOrderDetail detail : transportOrderDetails) {
				transportOrder = transportOrderService.queryById(detail.getTransportOrderId());
				map.put("transportOrderId",detail.getTransportOrderId());
				map.put("driverId",detail.getDriverId());
				//运单id、运单编号
				transportPlanVO = new TransportPlanVO();
				transportPlanVO.setTransportOrderId(transportOrder.getTransportOrderId());
				transportPlanVO.setTransportOrderCode(transportOrder.getTransportOrderCode());
				//货车信息:车牌号码(省份）、车牌号码（字母及数字）
				truck = transporterService.queryById(transportOrder.getTransporterId());
				truck.setAppTractirPlateProvince(CheckUtil.province(truck.getTractirPlateCode()));
				truck.setAppTractirPlateCode(CheckUtil.lettersAndNumbers(truck.getTractirPlateCode()));
				transportPlanVO.setTruck(truck);
				//停靠点路线
				transportPlans = transportPlanService.querySequenceList(map);
				transportPlanVO.setArrivalPositions(transportPlans);
				//路线描述
				for (int i = 0; i < transportPlans.size(); i++) {
					TransportPlan transportPlan = transportPlans.get(i);
					predictKm += transportPlan.getDriving();
					if(transportPlan.getEndUnitId() != null) {
						endUnit = unitService.queryById(transportPlan.getEndUnitId());
						//单位类型(0=经销店 1=仓库 2=展馆),特殊经销店(0=否 1=是)
						if(endUnit.getUnitType().intValue() == 0 || endUnit.getSpecial().intValue() == 1) {
							unitCount += 1;
						}
					}
					if(i == 0) {
						if(endUnit != null) {
							//起始地
							transportPlanVO.setStartUnitName(endUnit.getRefCityName());
							String loadType = transportPlan.getLoadType() != 0 ? (transportPlan.getLoadType() != 1 ? "非满载":"空载"):"满载";
							description.append(endUnit.getRefCityName()+"出发，"+endUnit.getUnitName()+"装"+transportPlan.getCarCount()+"辆"
										+"【"+loadType+"】行驶"+transportPlan.getDriving()+"公里到达");
						}
					}else {
						description.append(endUnit.getRefCityName()+",");
						if(transportPlan.getCarCount() > 0) {
							//装载
							description.append("装"+"【"+transportPlan.getCarCount()+"】"+"辆商品车；");
						}
						if(transportPlan.getStartUnitId() != null) {
							startUnit = unitService.queryById(transportPlan.getStartUnitId());
							description.append(startUnit.getRefCityName()+"个经销店");
						}
						if(transportPlan.getHandingCount() > 0) {
							//卸载
							description.append("卸"+"【"+transportPlan.getHandingCount()+"】"+"辆商品车；");
						} 
						description.append("继续行驶"+transportPlan.getDriving()+"公里到达");
					}
					if(transportPlans.size() == i+1){
						description.append("行程结束。");
					}
				}
				List<TransportPlan> unique = transportPlans.stream().collect(
			                collectingAndThen(
			                        toCollection(() -> new TreeSet<>(comparingLong(TransportPlan::getCorpId))), ArrayList::new)
			        );
				//城市数量
				transportPlanVO.setCityCount(unique.size());
				//预计公里数
				transportPlanVO.setPredictKm(predictKm);
				//经销店数量
				transportPlanVO.setUnitCount(unitCount);
				//路线描述
				transportPlanVO.setDescription(description);
				transportPlanVOs.add(transportPlanVO);
			}
			
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transportPlanVOs;
	}
	


}
