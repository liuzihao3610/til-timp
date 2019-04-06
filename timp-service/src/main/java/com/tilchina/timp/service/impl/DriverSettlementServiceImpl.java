package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.DriverSettlementStatus;
import com.tilchina.timp.enums.TransportOrderBillStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DriverSettlementMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.vo.DriverSettlementVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
* 司机结算
*
* @version 1.0.0
* @author XiaHong
*/
@Service
@Slf4j
public class DriverSettlementServiceImpl implements DriverSettlementService {

	@Autowired
    private DriverSettlementMapper driversettlementmapper;

    @Autowired
    private UnitService unitService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private TransportOrderDetailService transportOrderDetailService;

    @Autowired
    private TransferOrderService transferOrderService;

    @Autowired
    private DriverReportService driverReportService;

    @Autowired
    private TransportPlanService transportPlanService;

    @Autowired
    private TransporterService transporterService;

    @Autowired
    private FreightService freightService;

	protected BaseMapper<DriverSettlement> getMapper() {
		return driversettlementmapper;
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
	
	protected StringBuilder checkNewRecord(DriverSettlement driversettlement) {
		StringBuilder s = new StringBuilder();
        Environment env = Environment.getEnv();
       /* driversettlement.setSettlementCode(DateUtil.dateToStringCode(new Date()));*/
        driversettlement.setCreateDate(new Date());
        driversettlement.setCreator(env.getUser().getUserId());
        driversettlement.setCorpId(env.getCorp().getCorpId());
        s.append(CheckUtils.checkString("NO", "settlementCode", "结算编号", driversettlement.getSettlementCode(), 20));
        s.append(CheckUtils.checkString("NO", "driverCode", "司机编号", driversettlement.getDriverCode(), 20));
        s.append(CheckUtils.checkString("NO", "driverName", "司机名称", driversettlement.getDriverName(), 20));
        s.append(CheckUtils.checkString("NO", "tractirPlateCode", "车头车牌号", driversettlement.getTractirPlateCode(), 10));
        s.append(CheckUtils.checkString("NO", "tractorCode", "车头型号", driversettlement.getTractorCode(), 20));
        s.append(CheckUtils.checkString("NO", "tractorName", "车头名称", driversettlement.getTractorName(), 40));
        s.append(CheckUtils.checkDate("YES", "settlementMonth", "结算月份", driversettlement.getSettlementMonth()));
        s.append(CheckUtils.checkDate("YES", "settlementDateStart", "结算时间起", driversettlement.getSettlementDateStart()));
        s.append(CheckUtils.checkDate("YES", "settlementDateEnd", "结算时间止", driversettlement.getSettlementDateEnd()));
        s.append(CheckUtils.checkInteger("NO", "allPlanCount", "总运输数量", driversettlement.getAllPlanCount(), 10));
        s.append(CheckUtils.checkInteger("NO", "receiptCount", "已回单数量", driversettlement.getReceiptCount(), 10));
        s.append(CheckUtils.checkDate("YES", "settlementDate", "结算日期", driversettlement.getSettlementDate()));
        s.append(CheckUtils.checkInteger("NO", "participationDay", "出勤天数", driversettlement.getParticipationDay(), 10));
        s.append(checkBigDecimal("YES", "allContractPrice", "总承包价", driversettlement.getAllContractPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "etc", "etc", driversettlement.getEtc(), 10, 2));
        s.append(checkBigDecimal("YES", "oilCost", "油费", driversettlement.getOilCost(), 10, 2));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "单据状态", driversettlement.getBillStatus(), 10));
        s.append(checkBigDecimal("YES", "socialSecurityPrice", "代缴社保", driversettlement.getSocialSecurityPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "washCarPrice", "洗车费", driversettlement.getWashCarPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "repairPrice", "修理费", driversettlement.getRepairPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "pourTrailerPrice", "倒板费", driversettlement.getPourTrailerPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "littleTrailerPrice", "小板费", driversettlement.getLittleTrailerPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "bonusPrice", "特别补助", driversettlement.getBonusPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "orerrunPrice", "超限", driversettlement.getOrerrunPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "lossPrice", "质损", driversettlement.getLossPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "qualitycControlPrice", "品控", driversettlement.getQualitycControlPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "otherSubsidiesPrice", "其他补贴", driversettlement.getOtherSubsidiesPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "sreceivePrice", "领用", driversettlement.getSreceivePrice(), 10, 2));
        s.append(checkBigDecimal("YES", "oilCardPrice", "油卡", driversettlement.getOilCardPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "borrowingPrice", "借款", driversettlement.getBorrowingPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "maneuveringPrice", "机动补贴", driversettlement.getManeuveringPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "attendancePrice", "考勤代扣", driversettlement.getAttendancePrice(), 10, 2));
        s.append(checkBigDecimal("YES", "minorRepairPrice", "小修代扣", driversettlement.getMinorRepairPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "shouldTicketPrice", "应票额", driversettlement.getShouldTicketPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "taxesPrice", "应税额", driversettlement.getTaxesPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "actualSalaryPrice", "实发金额", driversettlement.getActualSalaryPrice(), 10, 2));
        s.append(checkBigDecimal("YES", "actualSalaryAllPrice", "实发合计", driversettlement.getActualSalaryAllPrice(), 10, 2));
        s.append(CheckUtils.checkDate("NO", "createDate", "制单日期", driversettlement.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核日期", driversettlement.getCheckDate()));
        s.append(CheckUtils.checkDate("YES", "notarizeDate", "确认时间", driversettlement.getNotarizeDate()));
        s.append(CheckUtils.checkString("YES", "remark", "备注", driversettlement.getRemark(), 200));
		return s;
	}

	protected StringBuilder checkUpdate(DriverSettlement driversettlement) {
        StringBuilder s = checkNewRecord(driversettlement);
        s.append(CheckUtils.checkPrimaryKey(driversettlement.getSettlementId()));
		return s;
	}
	
	@Override
    public DriverSettlement queryById(Long id) {
	     log.debug("queryById: {} ", id);
		DriverSettlement driverSettlement;
        StringBuilder s;
		 try {
			 	s = new StringBuilder();
				s.append(CheckUtils.checkLong("NO", "data", "司机结算ID", id, 20));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
			 driverSettlement = driversettlementmapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return driverSettlement;
    }


    @Override
    public List<DriverSettlementVO> queryDetailsById(Long id) {
        log.debug("queryDetailsById: {} ", id);
        List<DriverSettlementVO> driverVos = new ArrayList<>();
        DriverSettlement settlement = null;
	    try {
            BigDecimal allContractPrice = new BigDecimal(0.00);   //总承包价
            DriverSettlement driverSettlement = Optional.ofNullable(queryById(id)).orElse(new DriverSettlement());
            HashMap<String, Object> map = new HashMap<>();
            settlement = driverSettlement;
            BigDecimal allContractPrice1 = driverSettlement.getAllContractPrice();
            map.put("driverId",driverSettlement.getDriverId());
            map.put("startLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateStart()));
            map.put("endLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateEnd()));
            map.put("billStatus",TransportOrderBillStatus.CCOMPLISH.getIndex());
            List<TransportOrder> transportOrders = transportOrderService.querySettlementByDriverId(map);
            transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails() != null ).collect(Collectors.toList());
            transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails().size() > 0 ).collect(Collectors.toList());
            for (TransportOrder transportOrder : transportOrders) {
                {
                    freightService.getPrice(transportOrder);
                    DriverSettlementVO driverVo = new DriverSettlementVO();
                    driverVo.setTransportOrderId(transportOrder.getTransportOrderId());
                    driverVo.setTransportOrderCode(transportOrder.getTransportOrderCode());
                    driverVo.setReceivingDate(transportOrder.getReceivingDate());
                    driverVo.setJobType(transportOrder.getJobType());
                    driverVo.setTractirPlateCode(transportOrder.getRefTractirPlateCode());
                    Integer refMaxQuantity = Optional.ofNullable(transportOrder.getRefMaxQuantity()).orElse(0);
                    driverVo.setRefMaxQuantity(refMaxQuantity);
                    BigDecimal orderPrice  = new BigDecimal(0.00);	//运费
                    for (TransportOrderDetail transportOrderDetail : transportOrder.getDetails()) {
                        orderPrice = orderPrice.add(transportOrderDetail.getFreight().getFinalPrice());
                        HashMap<String, Object> mapf = new HashMap<>();
                        mapf.put("carVin",transportOrderDetail.getCarVin());
                        mapf.put("billStatus","4");
                        driverSettlement.setReceiptCount(driverSettlement.getReceiptCount()+ transferOrderService.queryList(mapf).size());
                    }
                    driverVo = descriptionAndRefMaxQuantity(transportOrder.getTransportOrderId(),driverVo);
                    driverVo.setOrderPrice(orderPrice);
                    allContractPrice = allContractPrice.add(orderPrice);
                    driverVos.add(driverVo);
                }
            }

        }catch (Exception e){
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
        return driverVos;
    }

    @Override
    public PageInfo<DriverSettlement> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<DriverSettlement>(getMapper().selectList(map));
    }
    
    @Override
    public List<DriverSettlement> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }

    @Override
    public void add(DriverSettlement driversettlement) {
        log.debug("add: {}",driversettlement);
        StringBuilder s;
        try {
            s = checkNewRecord(driversettlement);
            driversettlement.setSettlementCode(DateUtil.dateToStringCode(new Date()));
            driversettlement =  checkPrive(driversettlement);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            getMapper().insertSelective(driversettlement);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }

    @Override
    public void add(List<DriverSettlement> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                DriverSettlement driverSettlement = records.get(i);
                driverSettlement.setBillStatus(DriverSettlementStatus.Unchecked.getIndex());
                driverSettlement.setSettlementDate(new Date());
                driverSettlement =  checkPrive(driverSettlement);
                records.set(i,driverSettlement);
                StringBuilder check = checkNewRecord(driverSettlement);
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
            } else {
                throw new RuntimeException("批量保存失败！", e);
            }
        }
    }

    DriverSettlement checkPrive(DriverSettlement driverSettlement){
        BigDecimal etc = Optional.ofNullable(driverSettlement.getEtc()).orElse(BigDecimal.ZERO);
        BigDecimal oilCost = Optional.ofNullable(driverSettlement.getOilCost()).orElse(BigDecimal.ZERO);
        BigDecimal socialSecurityPrice = Optional.ofNullable(driverSettlement.getSocialSecurityPrice()).orElse(BigDecimal.ZERO);
        BigDecimal washCarPrice = Optional.ofNullable(driverSettlement.getWashCarPrice()).orElse(BigDecimal.ZERO);
        BigDecimal repairPrice = Optional.ofNullable(driverSettlement.getRepairPrice()).orElse(BigDecimal.ZERO);

        BigDecimal pourTrailerPrice = Optional.ofNullable(driverSettlement.getPourTrailerPrice()).orElse(BigDecimal.ZERO);
        BigDecimal littleTrailerPrice = Optional.ofNullable(driverSettlement.getLittleTrailerPrice()).orElse(BigDecimal.ZERO);
        BigDecimal bonusPrice = Optional.ofNullable(driverSettlement.getBonusPrice()).orElse(BigDecimal.ZERO);
        BigDecimal orerrunPrice = Optional.ofNullable(driverSettlement.getOrerrunPrice()).orElse(BigDecimal.ZERO);
        BigDecimal lossPrice = Optional.ofNullable(driverSettlement.getLossPrice()).orElse(BigDecimal.ZERO);

        BigDecimal qualitycControlPrice = Optional.ofNullable(driverSettlement.getQualitycControlPrice()).orElse(BigDecimal.ZERO);
        BigDecimal otherSubsidiesPrice = Optional.ofNullable(driverSettlement.getOtherSubsidiesPrice()).orElse(BigDecimal.ZERO);
        BigDecimal sreceivePrice = Optional.ofNullable(driverSettlement.getSreceivePrice()).orElse(BigDecimal.ZERO);
        BigDecimal oilCardPrice = Optional.ofNullable(driverSettlement.getOilCardPrice()).orElse(BigDecimal.ZERO);
        BigDecimal borrowingPrice = Optional.ofNullable(driverSettlement.getBorrowingPrice()).orElse(BigDecimal.ZERO);
        BigDecimal maneuveringPrice = Optional.ofNullable(driverSettlement.getManeuveringPrice()).orElse(BigDecimal.ZERO);

        BigDecimal attendancePrice = Optional.ofNullable(driverSettlement.getAttendancePrice()).orElse(BigDecimal.ZERO);
        BigDecimal minorRepairPrice = Optional.ofNullable(driverSettlement.getMinorRepairPrice()).orElse(BigDecimal.ZERO);
        BigDecimal shouldTicketPrice = Optional.ofNullable(driverSettlement.getShouldTicketPrice()).orElse(BigDecimal.ZERO);     // 应票额
        BigDecimal taxesPrice = Optional.ofNullable(driverSettlement.getTaxesPrice()).orElse(BigDecimal.ZERO);  //  应税额
        BigDecimal actualSalaryPrice = Optional.ofNullable(driverSettlement.getActualSalaryPrice()).orElse(BigDecimal.ZERO);    //  实发金额

        BigDecimal actualSalaryAllPrice = Optional.ofNullable(driverSettlement.getActualSalaryAllPrice()).orElse(BigDecimal.ZERO);  //  实发合计
        BigDecimal allContractPrice = Optional.ofNullable(driverSettlement.getAllContractPrice()).orElse(BigDecimal.ZERO);  //   总承包价

        shouldTicketPrice = allContractPrice.add(etc).add(oilCost).add(socialSecurityPrice).add(washCarPrice).add(repairPrice).add(pourTrailerPrice)
                .add(littleTrailerPrice).add(bonusPrice).add(orerrunPrice).add(lossPrice).add(qualitycControlPrice).add(otherSubsidiesPrice)
                .add(sreceivePrice).add(oilCardPrice).add(borrowingPrice).add(maneuveringPrice).add(attendancePrice).add(minorRepairPrice);

        taxesPrice = shouldTicketPrice.multiply(new BigDecimal(0.07)).setScale(2, RoundingMode.HALF_DOWN);

        actualSalaryPrice = shouldTicketPrice.subtract(taxesPrice);
        actualSalaryAllPrice = actualSalaryPrice;

        driverSettlement.setEtc(etc);
        driverSettlement.setOilCost(oilCost);
        driverSettlement.setSocialSecurityPrice(socialSecurityPrice);
        driverSettlement.setWashCarPrice(washCarPrice);
        driverSettlement.setRepairPrice(repairPrice);

        driverSettlement.setPourTrailerPrice(pourTrailerPrice);
        driverSettlement.setLittleTrailerPrice(littleTrailerPrice);
        driverSettlement.setBonusPrice(bonusPrice);
        driverSettlement.setOrerrunPrice(orerrunPrice);
        driverSettlement.setLossPrice(lossPrice);

        driverSettlement.setQualitycControlPrice(qualitycControlPrice);
        driverSettlement.setOtherSubsidiesPrice(otherSubsidiesPrice);
        driverSettlement.setSreceivePrice(sreceivePrice);
        driverSettlement.setOilCardPrice(oilCardPrice);
        driverSettlement.setBorrowingPrice(borrowingPrice);

        driverSettlement.setManeuveringPrice(maneuveringPrice);
        driverSettlement.setAttendancePrice(attendancePrice);
        driverSettlement.setMinorRepairPrice(minorRepairPrice);
        driverSettlement.setShouldTicketPrice(shouldTicketPrice);
        driverSettlement.setTaxesPrice(taxesPrice);

        driverSettlement.setActualSalaryAllPrice(actualSalaryAllPrice);
        driverSettlement.setActualSalaryPrice(actualSalaryPrice);
        driverSettlement.setAllContractPrice(allContractPrice);
        return driverSettlement;
    }

    @Override
    public void updateSelective(DriverSettlement driverSettlement) {
        log.debug("updateSelective: {}",driverSettlement);
        StringBuilder s;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "settlementId", "结算Id", driverSettlement.getSettlementId(), 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            driverSettlement =  checkPrive(driverSettlement);
            DriverSettlement driverSettlement1 = Optional.ofNullable(queryById(driverSettlement.getSettlementId())).orElse(new DriverSettlement());
            Integer billStatus = Optional.ofNullable(driverSettlement1.getBillStatus()).orElse(0);
            if (DriverSettlementStatus.Check.getIndex() == billStatus){
                throw new BusinessException("该司机结算单："+driverSettlement.getSettlementCode()+"已审核完成，不能进行修改！若想修改，请先取消审核！");
            }
            getMapper().updateByPrimaryKeySelective(driverSettlement);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

    @Override
    public void voidorderReceiving(Long driverSettlementId) {
        log.debug("voidorderReceiving: {}",driverSettlementId);
        StringBuilder s;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "settlementId", "结算Id",driverSettlementId, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            DriverSettlement driverSettlement = new DriverSettlement();
            driverSettlement.setSettlementId(driverSettlementId);
            driverSettlement.setBillStatus(2);
            driverSettlement.setNotarizeDate(new Date());
            List<DriverSettlementVO> driverSettlementVOS = queryDetailsById(driverSettlementId);
            driverSettlementVOS.forEach(driverSettlementVO -> {
                TransportOrder transportOrder = new TransportOrder();
                transportOrder.setTransportOrderId(driverSettlementVO.getTransportOrderId());
                transportOrder.setSettleStatus(TransportOrderBillStatus.LREADYSETTLE.getIndex());
                transportOrderService.updateSelective(transportOrder);
            });
            getMapper().updateByPrimaryKeySelective(driverSettlement);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

    @Override
    public void update(DriverSettlement driverSettlement) {
        log.debug("update: {}",driverSettlement);
        StringBuilder s;
        try {
            s = checkUpdate(driverSettlement);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            driverSettlement =  checkPrive(driverSettlement);
            getMapper().updateByPrimaryKey(driverSettlement);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }

    @Override
    public void update(List<DriverSettlement> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                DriverSettlement record = records.get(i);
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
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("批量更新失败！", e);
            }
        }

    }

    @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        try{
            DriverSettlement driverSettlement = queryById(id);
            if (driverSettlement.getBillStatus().intValue() >= DriverSettlementStatus.Check.getIndex()){
                throw new BusinessException("改结算单不能删除，请先将结算单号："+driverSettlement.getSettlementCode()+"回退未制单状态，再进行删除操作！！");
            }
            getMapper().deleteByPrimaryKey(id);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }

    @Override
    public List<DriverSettlement> queryByDriverIds(List<Long> driverIds) {
        log.debug("queryByDriverIds: {}",driverIds);
        try {
            List<DriverSettlement> driverSettlements = new ArrayList<>();
            if(driverIds.size() > 0){
                driverSettlements = driversettlementmapper.selectByDriverIds(driverIds);
            }
            return driverSettlements;
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }

    DriverSettlementVO descriptionAndRefMaxQuantity(Long transportOrderId,DriverSettlementVO driverSettlementVO){
        log.debug("detail: {},{}",transportOrderId,driverSettlementVO);
        StringBuilder description;
        Integer refMaxQuantity = 0;
        try {
            List<TransportPlan> transportPlans  = transportPlanService.queryByOrderId(transportOrderId);
            description = new StringBuilder();
            //路线描述
            for (int i = 0; i < transportPlans.size(); i++) {
                TransportPlan transportPlan = transportPlans.get(i);
                Unit endUnit = unitService.queryById(transportPlan.getEndUnitId());
                if(i == 0) {
                    refMaxQuantity += transportPlan.getCarCount();
                //起始地
                description.append(endUnit.getRefCityName()+"（+"+transportPlan.getCarCount()+"）");
                }else {
                    description.append(endUnit.getRefCityName());
                    if(transportPlan.getCarCount() > 0) {
                        refMaxQuantity += transportPlan.getCarCount();
                        //装载
                        description.append("（+"+transportPlan.getCarCount()+"）");
                    }
                    if(transportPlan.getHandingCount() > 0) {
                        //卸载
                        description.append("（-"+transportPlan.getHandingCount()+"）");
                    }
                }
            }
            List<TransportPlan> unique = transportPlans.stream().collect(
                    collectingAndThen(
                            toCollection(() -> new TreeSet<>(comparingLong(TransportPlan:: getEndUnitId))), ArrayList::new)
            );
            driverSettlementVO.setRefMaxQuantity(refMaxQuantity);
            driverSettlementVO.setDetail(description.toString());
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
	    return driverSettlementVO;
    }

	@Override
    public void settlement(DriverSettlement driverSettlement) {
        log.debug("settlement: {}",driverSettlement);
        DriverSettlement driver;
        int month;
        User uDriver;
        StringBuilder s;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员ID", driverSettlement.getDriverId(), 20))
                .append(CheckUtils.checkDate("NO", "settlementMonth", "结算月份", driverSettlement.getSettlementMonth()))
                .append(CheckUtils.checkDate("NO", "settlementDate", "结算时间", driverSettlement.getSettlementDate()))
                .append(CheckUtils.checkString("NO", "tractirPlateCode", "车牌号", driverSettlement.getTractirPlateCode(),20))
                .append(CheckUtils.checkString("NO", "tractorCode", "车头型号", driverSettlement.getTractorName(),50));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            driver = queryByDriverId(driverSettlement.getDriverId(), driverSettlement.getSettlementMonth());
            if(DateUtil.getMonth (driverSettlement.getSettlementMonth()) >= DateUtil.getMonth(new Date())){
                throw new BusinessException("请选择有效的结算月份，您目前只能选择小于等于该:"+DateUtil.dateToMonthString(new Date())+"月份的结算月份！");
            }
            if(driver == null) {
                uDriver = driverService.queryById(driverSettlement.getDriverId());
                if(uDriver == null) {
                    throw new BusinessException("请选择有效的司机，该司机ID:"+driverSettlement.getDriverId()+"不存在！");
                }
                if(uDriver.getJoinDate() == null) {
                    throw new BusinessException("请维护好该司机:"+uDriver.getUserName()+"的入职时间。");
                }
                month = DateUtil.getMonth(new Date());
                if(DateUtil.getMonth(uDriver.getJoinDate())  == month) {
                    driverSettlement.setSettlementDateStart(uDriver.getJoinDate());
                }else {
                    driverSettlement.setSettlementDateStart(DateUtil.getMonthStartAndEnd(driverSettlement.getSettlementMonth(),0));
                }
                driverSettlement.setSettlementCode(DateUtil.dateToStringCode(new Date()));
                driverSettlement.setDriverCode(uDriver.getUserCode());
                driverSettlement.setDriverName(uDriver.getUserName());
                driverSettlement.setSettlementDateEnd(DateUtil.getMonthStartAndEnd(driverSettlement.getSettlementMonth(),1));

                Integer leaveDay = driverReportService.getParticipationDay(driverSettlement);
                Integer day = DateUtil.differentDays(driverSettlement.getSettlementDateStart(), driverSettlement.getSettlementDateEnd());
                driverSettlement.setParticipationDay( day -leaveDay + 1);
                HashMap<String, Object> map = new HashMap<>();
                map.put("driverId",driverSettlement.getDriverId());
                map.put("startLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateStart()));
                map.put("endLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateEnd()));
                map.put("billStatus",TransportOrderBillStatus.CCOMPLISH.getIndex());
                List<TransportOrder> transportOrders = transportOrderService.querySettlementByDriverId(map);
                transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails() != null).collect(Collectors.toList());
                List<DriverSettlementVO> driverVos = new ArrayList<>();
                driverSettlement.setReceiptCount(0);
                BigDecimal allContractPrice  = new BigDecimal(0.00);	//总承包价
                int allPlanCount = 0;
                for (TransportOrder transportOrder : transportOrders) {
                    freightService.getPrice(transportOrder);
                    BigDecimal orderPrice  = new BigDecimal(0.00);	//运费
                    for (TransportOrderDetail transportOrderDetail : transportOrder.getDetails()) {
                        orderPrice = orderPrice.add(transportOrderDetail.getFreight().getFinalPrice());
                        HashMap<String, Object> mapf = new HashMap<>();
                        mapf.put("carVin",transportOrderDetail.getCarVin());
                        mapf.put("billStatus","4");
                        driverSettlement.setReceiptCount(driverSettlement.getReceiptCount()+ transferOrderService.queryList(mapf).size());
                    }
                    allPlanCount += transportOrder.getDetails().size();
                    allContractPrice = allContractPrice.add(orderPrice);
                }
                driverSettlement.setAllPlanCount(allPlanCount);
                driverSettlement.setAllContractPrice(allContractPrice);
                add(driverSettlement);
            }else {
                throw new BusinessException("司机："+driver.getDriverName()+"，结算月份："+DateUtil.dateToMonthString(driver.getSettlementMonth())+"，结算单已存在。");
            }
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }

        }
    }
    DriverSettlement settlement(Long driverId) {
        DriverSettlement driver;
        int month;
        User uDriver;
        DriverSettlement driverSettlement;
        try {

            driverSettlement = new DriverSettlement();
            driverSettlement.setSettlementMonth(DateUtil.getMonth(-1));
            driver = queryByDriverId(driverId,driverSettlement.getSettlementMonth());
            if(driver == null) {
                uDriver = driverService.queryById(driverId);
                if(uDriver == null) {
                    throw new BusinessException("请选择有效的司机，该司机ID:"+driverId+"不存在！");
                }
                if(uDriver.getJoinDate() == null) {
                    throw new BusinessException("请维护好该司机:"+uDriver.getUserName()+"的入职时间。");
                }

                driverSettlement.setSettlementMonth(DateUtil.getMonth(-1));
                month = DateUtil.getMonth( driverSettlement.getSettlementMonth());
                if(DateUtil.getMonth(uDriver.getJoinDate())  == month) {
                    driverSettlement.setSettlementDateStart(uDriver.getJoinDate());
                }else {
                    driverSettlement.setSettlementDateStart(DateUtil.getMonthStartAndEnd(driverSettlement.getSettlementMonth(),0));
                }
                driverSettlement.setSettlementCode(DateUtil.dateToStringCode(new Date()));
                driverSettlement.setSettlementDateEnd(DateUtil.getMonthStartAndEnd(driverSettlement.getSettlementMonth(),1));
                driverSettlement.setDriverId(driverId);
                driverSettlement.setDriverCode(uDriver.getUserCode());
                driverSettlement.setDriverName(uDriver.getUserName());

                Integer leaveDay = driverReportService.getParticipationDay(driverSettlement);
                Integer day = DateUtil.differentDays(driverSettlement.getSettlementDateStart(), driverSettlement.getSettlementDateEnd());
                driverSettlement.setParticipationDay( day -leaveDay + 1);
                HashMap<String, Object> map = new HashMap<>();
                map.put("driverId",driverSettlement.getDriverId());
                map.put("startLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateStart()));
                map.put("endLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateEnd()));
                map.put("billStatus",TransportOrderBillStatus.CCOMPLISH.getIndex());
                List<TransportOrder> transportOrders = transportOrderService.querySettlementByDriverId(map);
                transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails() != null).collect(Collectors.toList());
                List<DriverSettlementVO> driverVos = new ArrayList<>();
                driverSettlement.setReceiptCount(0);
                Transporter transporter = transporterService.queryByContractorId(driverSettlement.getDriverId());
                if(transporter == null){
                    throw new BusinessException("请维护好该司机:"+uDriver.getUserName()+"的轿运车档案。");
                }
                driverSettlement.setTractorId(transporter.getTractorId());
                driverSettlement.setTractorCode(transporter.getRefTractorCode());
                driverSettlement.setTractorName(transporter.getRefTractorName());
                driverSettlement.setTractirPlateCode(transporter.getTractirPlateCode());
                driverSettlement.setTransporterId(transporter.getTransporterId());
                BigDecimal allContractPrice  = new BigDecimal(0.00);	//总承包价
                int allPlanCount = 0;
                for (TransportOrder transportOrder : transportOrders) {
                    freightService.getPrice(transportOrder);
                    BigDecimal orderPrice  = new BigDecimal(0.00);	//运费
                    for (TransportOrderDetail transportOrderDetail : transportOrder.getDetails()) {
                        orderPrice = orderPrice.add(transportOrderDetail.getFreight().getFinalPrice());
                        HashMap<String, Object> mapf = new HashMap<>();
                        mapf.put("carVin",transportOrderDetail.getCarVin());
                        mapf.put("billStatus","4");
                        driverSettlement.setReceiptCount(driverSettlement.getReceiptCount()+ transferOrderService.queryList(mapf).size());
                    }
                    allPlanCount += transportOrder.getDetails().size();
                    allContractPrice = allContractPrice.add(orderPrice);
                }
                driverSettlement.setAllPlanCount(allPlanCount);
                driverSettlement.setOrders(driverVos);
                driverSettlement.setAllContractPrice(allContractPrice);
                return driverSettlement;
            }else{
                return null;
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
    public void settlementList() {
        log.debug("settlementList: {}");
        Map<String,Object> map = new HashMap<>();
        map.put("userType","1");
        map.put("driverType","1");
        try{
            List<User> drivers = driverService.queryList(map);
            List<DriverSettlement> driverSettlements = new ArrayList<>();
            drivers.forEach(driver ->{
                driverSettlements.add(settlement(driver.getUserId()));
            });
            add( driverSettlements.stream().filter(driverSettlement -> driverSettlement != null).collect(Collectors.toList()));
        }catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }

    }

    @Override
	public DriverSettlement queryByDriverId(Long driverId,Date settlementMonth) {
		 log.debug("settlement: {}",driverId);
		 DriverSettlement driverSettlement = null;
		 StringBuilder s;
		 try {
			 	s = new StringBuilder();

				s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员ID", driverId, 20))
                .  append(CheckUtils.checkDate("NO", "settlementMonth", "结算月份", settlementMonth));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
			 driverSettlement = driversettlementmapper.selectByDriverId(driverId,settlementMonth);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return driverSettlement;
	}

    @Override
    public PageInfo<DriverSettlement> appQueryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<DriverSettlement> driverSettlements;
        Environment env = Environment.getEnv();
        try {
            map.put("settlementDate",DateUtil.dateToString(new Date()));
            map.put("driverId",env.getUser().getUserId());
            map.put("appBillStatus","query");
           driverSettlements = new PageInfo<DriverSettlement> (driversettlementmapper.appSelectList(map));
        }catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
        return driverSettlements;
    }

    @Override
    public void updateCheck(Long id,int i) {
        log.debug("updateCheck:{}",id);
        Environment env = Environment.getEnv();
        try {
            DriverSettlement driverSettlement = queryById(id);
            if (i == 0){
                if (driverSettlement.getBillStatus().intValue() == DriverSettlementStatus.Check.getIndex()){
                        //  审核
                    throw new BusinessException("已审核通过，请勿重复审核！");
                }
                driverSettlement.setBillStatus(DriverSettlementStatus.Check.getIndex());
                driverSettlement.setChecker(env.getUser().getUserId());
                driverSettlement.setCheckDate(new Date());
                driversettlementmapper.updateCheck(driverSettlement);
            }else  if (i == 1){
                if (driverSettlement.getBillStatus().intValue() == DriverSettlementStatus.Unchecked.getIndex()){
                        //  取消审核
                    throw new BusinessException("未通过审核，不需要取消审核！");
                }else if(driverSettlement.getBillStatus().intValue() == DriverSettlementStatus.Verify.getIndex()){
                    //  取消审核
                    throw new BusinessException("司机已确认接单，确认后不可取消审核！");
                }
                driverSettlement.setBillStatus(DriverSettlementStatus.Unchecked.getIndex());
                driverSettlement.setChecker(null);
                driverSettlement.setCheckDate(null);
                driversettlementmapper.updateCheck(driverSettlement);
            }

        }catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }

    @Override
    public void maintainPrice(List<DriverSettlement> driverSettlements,List<TransportOrderDetail> details) {
        log.debug("maintainPrice:{}",driverSettlements);
        try {
            for (DriverSettlement driverSettlement : driverSettlements) {
                HashMap<String, Object> map = new HashMap<>();
                BigDecimal allContractPrice = new BigDecimal(0.00);
                map.put("driverId",driverSettlement.getDriverId());
                map.put("startLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateStart()));
                map.put("endLoadingDate",DateUtil.dateToString(driverSettlement.getSettlementDateEnd()));
                map.put("billStatus",TransportOrderBillStatus.CCOMPLISH.getIndex());
                List<TransportOrder> transportOrders = transportOrderService.querySettlementByDriverId(map);
                transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails() != null ).collect(Collectors.toList());
                transportOrders = transportOrders.stream().filter(transportOrder -> transportOrder.getDetails().size() > 0 ).collect(Collectors.toList());
                for (TransportOrder transportOrder : transportOrders) {
                    freightService.getPrice(transportOrder);
                    BigDecimal orderPrice  = new BigDecimal(0.00);	//运费
                    for (TransportOrderDetail transportOrderDetail : transportOrder.getDetails()) {
                        for (TransportOrderDetail detail : details) {
                            if (detail.getFreightId().longValue() == transportOrderDetail.getFreightId()){
                                transportOrderDetail.setFinalPrice(transportOrderDetail.getFinalPrice());
                            }
                        }
                        orderPrice = orderPrice.add(transportOrderDetail.getFreight().getFinalPrice());
                    }
                    allContractPrice = allContractPrice.add(orderPrice);
                }
                driverSettlement.setAllContractPrice(allContractPrice);
                driverSettlement.setTaxesPrice(null);
                driverSettlement.setActualSalaryPrice(null);
                driverSettlement.setActualSalaryAllPrice(null);
                driverSettlement.setShouldTicketPrice(null);
                driverSettlement = checkPrive(driverSettlement);
            }
            updateSelective(driverSettlements);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }
    @Override
    public List<String> querySettlementMonth() {
        try {
            List<String> settlementMonths = new ArrayList<>();
            List<Date> querySettlementMonths = driversettlementmapper.selectSettlementMonth();
            querySettlementMonths.forEach(settlementMonth ->settlementMonths.add(DateUtil.dateToMonthString(settlementMonth)));
            return settlementMonths;
        } catch (Exception e) {
            throw new RuntimeException("操作失败！", e);
        }

    }

    @Override
    public List<DriverSettlement> queryBySettlementMonth(String settlementMonth) {
        log.debug("queryBySettlementMonth:{}",settlementMonth);
        String filePath;
        try {
            List<DriverSettlement> driverSettlements = driversettlementmapper.selectBySettlementMonth(settlementMonth);
            return driverSettlements;
        } catch (Exception e) {
            throw new RuntimeException("操作失败！", e);
        }
    }


    @Override
    public void importSubsidy(MultipartFile file,String settlementMonth) throws Exception {
        log.debug("importAssembly:{}",file);
        String filePath;
        try {
           /* settlementMonth = "2018-05";*/
            filePath = FileUtil.uploadFile(file, "DriverSettlementSubsidy");
            Workbook workbook = ExcelUtil.getWorkbook(filePath);
            parseExcelForSubsidy(workbook,settlementMonth);
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败！", e);
            }
        }
    }


    private void parseExcelForSubsidy(Workbook workbook,String settlementMonth) {
        Sheet sheet = workbook.getSheetAt(0);
        int colCount = 18;
        int rowCount = sheet.getLastRowNum();

        List<DriverSettlement> queryDriverSettlements = queryBySettlementMonth(settlementMonth);
        List<DriverSettlement> driverSettlements = new ArrayList<>();
        Map<String,DriverSettlement> settlementMap = new HashMap<String,DriverSettlement>();
        queryDriverSettlements.forEach(driverSettlement -> settlementMap.put(driverSettlement.getDriverName(),driverSettlement));

        Integer number = null;
        String phoneNumber = null;

        for (int i = 0; i <= rowCount; i++) {
            Cell[] cells = new Cell[colCount + 1];
            for (int j = 0; j <= colCount; j++) {
                if(cells[j] != null){
                    cells[j].setCellType(CellType.NUMERIC);
                    if(j == 18){
                        cells[j].setCellType(CellType.STRING);
                    }
                }
                cells[j] = Optional.ofNullable(sheet.getRow(i).getCell(j)).orElse(null);
            }

            if (i >= 1){
                Double etc = new Double(0.00);
                Double oilCost     = new Double(0.00);
                Double socialSecurityPrice      = new Double(0.00);
                Double washCarPrice           = new Double(0.00);
                Double repairPrice       = new Double(0.00);
                Double pourTrailerPrice           = new Double(0.00);
                Double littleTrailerPrice         = new Double(0.00);
                Double bonusPrice             = new Double(0.00);
                Double orerrunPrice      = new Double(0.00);
                Double lossPrice       = new Double(0.00);
                Double qualitycControlPrice             = new Double(0.00);
                Double otherSubsidiesPrice           = new Double(0.00);
                Double sreceivePrice           = new Double(0.00);
                Double oilCardPrice       = new Double(0.00);
                Double borrowingPrice             = new Double(0.00);
                Double maneuveringPrice           = new Double(0.00);
                Double attendancePrice           = new Double(0.00);
                Double minorRepairPrice           = new Double(0.00);
                if (cells[0] != null){
                    etc = cells[0].getNumericCellValue();
                }
                if (cells[1] != null){
                    oilCost = cells[1].getNumericCellValue();
                }
                if (cells[2] != null){
                    socialSecurityPrice = cells[2].getNumericCellValue();
                }
                if (cells[3] != null){
                    washCarPrice = cells[3].getNumericCellValue();
                }
                if (cells[4] != null){
                    repairPrice = cells[4].getNumericCellValue();
                }
                if (cells[5] != null){
                    pourTrailerPrice = cells[5].getNumericCellValue();
                }
                if (cells[6] != null){
                    littleTrailerPrice = cells[6].getNumericCellValue();
                }
                if (cells[7] != null){
                    bonusPrice = cells[7].getNumericCellValue();
                }
                if (cells[8] != null){
                    orerrunPrice = cells[8].getNumericCellValue();
                }
                if (cells[9] != null){
                    lossPrice = cells[9].getNumericCellValue();
                }
                if (cells[10] != null){
                    qualitycControlPrice = cells[10].getNumericCellValue();
                }
                if (cells[11] != null){
                    otherSubsidiesPrice = cells[11].getNumericCellValue();
                }
                if (cells[12] != null){
                    sreceivePrice = cells[12].getNumericCellValue();
                }
                if (cells[13] != null){
                    oilCardPrice = cells[13].getNumericCellValue();
                }
                if (cells[14] != null){
                    borrowingPrice = cells[14].getNumericCellValue();
                }
                if (cells[15] != null){
                    maneuveringPrice = cells[15].getNumericCellValue();
                }if (cells[16] != null){
                    attendancePrice = cells[16].getNumericCellValue();
                }
                if (cells[17] != null){
                    minorRepairPrice = cells[17].getNumericCellValue();
                }
                DriverSettlement settlement = new DriverSettlement();
                if (cells[18] != null){
                    String driverName  = cells[18].getStringCellValue();

                    settlement = Optional.ofNullable(settlementMap.get(driverName))
                            .orElseThrow(() -> new BusinessException("该司机：" + driverName + "在" + settlementMonth + "结算月份中没有数据，请先结算再进行补贴导入。"));
                    if(settlement.getBillStatus().intValue() > 0){
                        throw new BusinessException("该司机：" + driverName + "在" + settlementMonth + "结算月份中已经不是制单状态了，不能再进行补贴导入。（友情提示：只有再制单状态才能进行补贴导入）");
                    }
                }else {
                    throw new BusinessException("第"+(i+1)+"列第17行没有司机名称信息，请先维护好司机名称再进行补贴导入。");
                }
                settlement.setEtc( settlement.getEtc().add(new BigDecimal(etc)));
                settlement.setOilCost( settlement.getOilCost().add(new BigDecimal(oilCost)));
                settlement.setSocialSecurityPrice( settlement.getSocialSecurityPrice().add(new BigDecimal(socialSecurityPrice)));
                settlement.setWashCarPrice( settlement.getWashCarPrice().add(new BigDecimal(washCarPrice)));
                settlement.setRepairPrice( settlement.getRepairPrice().add(new BigDecimal(repairPrice)));
                settlement.setPourTrailerPrice( settlement.getPourTrailerPrice().add(new BigDecimal(pourTrailerPrice)));
                settlement.setLittleTrailerPrice(settlement.getLittleTrailerPrice().add(new BigDecimal(littleTrailerPrice)));
                settlement.setBonusPrice( settlement.getBonusPrice().add(new BigDecimal(bonusPrice)));
                settlement.setOrerrunPrice( settlement.getOrerrunPrice().add(new BigDecimal(orerrunPrice)));
                settlement.setLossPrice( settlement.getLossPrice().add(new BigDecimal(lossPrice)));
                settlement.setQualitycControlPrice(settlement.getQualitycControlPrice().add(new BigDecimal(qualitycControlPrice)));
                settlement.setOtherSubsidiesPrice( settlement.getOtherSubsidiesPrice().add(new BigDecimal(otherSubsidiesPrice)));
                settlement.setSreceivePrice(settlement.getSreceivePrice().add(new BigDecimal(sreceivePrice)));
                settlement.setOilCardPrice(settlement.getOilCardPrice().add(new BigDecimal(oilCardPrice)));
                settlement.setBorrowingPrice( settlement.getBorrowingPrice().add(new BigDecimal(borrowingPrice)));
                settlement.setManeuveringPrice( settlement.getManeuveringPrice().add(new BigDecimal(maneuveringPrice)));
                settlement.setAttendancePrice(settlement.getAttendancePrice().add(new BigDecimal(attendancePrice)));
                settlement.setMinorRepairPrice( settlement.getMinorRepairPrice().add(new BigDecimal(minorRepairPrice)));
                driverSettlements.add(checkPrive(settlement));
            }
        }
        updateSelective(driverSettlements);
    }

    @Override
    public void updateSelective(List<DriverSettlement> driverSettlements) {
        log.debug("updateSelective: {}",driverSettlements);
        StringBuilder s;
        try {
            s = new StringBuilder();
            Boolean checkResult = true;
            for (int i = 0; i < driverSettlements.size(); i++) {
                DriverSettlement driverSettlement = driverSettlements.get(i);
                StringBuilder check = new StringBuilder();
                check.append(CheckUtils.checkLong("NO", "settlementId", "结算Id", driverSettlement.getSettlementId(), 20));
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
                DriverSettlement driverSettlement1 = Optional.ofNullable(queryById(driverSettlement.getSettlementId())).orElse(new DriverSettlement());
                Integer billStatus = Optional.ofNullable(driverSettlement1.getBillStatus()).orElse(0);
                if (DriverSettlementStatus.Check.getIndex() == billStatus){
                    throw new BusinessException("该司机结算单："+driverSettlement.getSettlementCode()+"已审核完成，不能进行修改！若想修改，请先取消审核！");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            driversettlementmapper.updateByPrimaryKeySelective(driverSettlements);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }


}
