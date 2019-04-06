package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.RailtransOrderDetailMapper;
import com.tilchina.timp.model.RailtransCabinStatus;
import com.tilchina.timp.model.RailtransOrderDetail;
import com.tilchina.timp.model.RailtransParsedTextModel;
import com.tilchina.timp.service.RailtransOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 铁路运输记录子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class RailtransOrderDetailServiceImpl extends BaseServiceImpl<RailtransOrderDetail> implements RailtransOrderDetailService {

	@Autowired
    private RailtransOrderDetailMapper railtransorderdetailmapper;
	
	@Override
	protected BaseMapper<RailtransOrderDetail> getMapper() {
		return railtransorderdetailmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(RailtransOrderDetail railtransorderdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "transOrder", "运输指令", railtransorderdetail.getTransOrder(), 50));
        s.append(CheckUtils.checkString("YES", "carVin", "底盘编号", railtransorderdetail.getCarVin(), 50));
        s.append(CheckUtils.checkString("YES", "carModel", "车型", railtransorderdetail.getCarModel(), 50));
        s.append(CheckUtils.checkString("YES", "dealerNo", "经销商编号", railtransorderdetail.getDealerNo(), 50));
        s.append(CheckUtils.checkString("YES", "dealerName", "经销商名称", railtransorderdetail.getDealerName(), 50));
        s.append(CheckUtils.checkDate("YES", "toDate", "运输指令下达日", railtransorderdetail.getToDate()));
        s.append(CheckUtils.checkDate("YES", "plannedDispatchedDate", "计划发运日期", railtransorderdetail.getPlannedDispatchedDate()));
        s.append(CheckUtils.checkDate("YES", "actualPickupDate", "实际提车日期", railtransorderdetail.getActualPickupDate()));
        s.append(CheckUtils.checkDate("YES", "actualDispatchedDate", "实际发运日期", railtransorderdetail.getActualDispatchedDate()));
        s.append(CheckUtils.checkDate("YES", "estTohubDate", "预计到达Hub仓库日期", railtransorderdetail.getEstTohubDate()));
        s.append(CheckUtils.checkDate("YES", "dueDate", "交货期限", railtransorderdetail.getDueDate()));
        s.append(CheckUtils.checkString("YES", "zone", "位置", railtransorderdetail.getZone(), 200));
        s.append(CheckUtils.checkString("YES", "remark", "备注", railtransorderdetail.getRemark(), 200));
        s.append(CheckUtils.checkString("YES", "startingStation", "始发站", railtransorderdetail.getStartingStation(), 20));
        s.append(CheckUtils.checkString("YES", "terminalStation", "终点站", railtransorderdetail.getTerminalStation(), 20));
        s.append(CheckUtils.checkString("YES", "cabinNumber", "车厢号", railtransorderdetail.getCabinNumber(), 20));
        s.append(CheckUtils.checkString("YES", "latestProvince", "车厢最新停靠站所在省市", railtransorderdetail.getLatestProvince(), 20));
        s.append(CheckUtils.checkString("YES", "latestCity", "车厢最新停靠站所在城市", railtransorderdetail.getLatestCity(), 20));
        s.append(CheckUtils.checkString("YES", "latestStation", "车厢最新停靠站所在站台", railtransorderdetail.getLatestStation(), 20));
        s.append(CheckUtils.checkString("YES", "latestStatus", "车厢最新停靠状态", railtransorderdetail.getLatestStatus(), 20));
        s.append(CheckUtils.checkInteger("YES", "isArrived", "到达状态", railtransorderdetail.getIsArrived(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", railtransorderdetail.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", railtransorderdetail.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "单据状态", railtransorderdetail.getBillStatus(), 10));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", railtransorderdetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(RailtransOrderDetail railtransorderdetail) {
        StringBuilder s = checkNewRecord(railtransorderdetail);
        s.append(CheckUtils.checkPrimaryKey(railtransorderdetail.getTransOrderDetailId()));
		return s;
	}

	@Override
	public void deleteByOrderId(Long orderId) {
		railtransorderdetailmapper.deleteByOrderId(orderId);
	}

    @Override
    public void delete(Long orderDetailId) {

        try {

            railtransorderdetailmapper.delete(orderDetailId);

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("操作失败");
            }
        }
    }

    @Override
    public void deleteList(Long[] orderDetailIds) {

        for (Long orderDetailId : orderDetailIds) {
            delete(orderDetailId);
        }
    }

    public void update(RailtransOrderDetail railtransOrderDetail) {

        try {

            railtransorderdetailmapper.updateByPrimaryKey(railtransOrderDetail);

        } catch (Exception e) {
	        log.error("{}", e.getMessage());
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("操作失败");
            }
        }
    }

    @Override
    public int updateByCarVin(RailtransParsedTextModel model) {

        Map<String, String> params = new HashMap(){{
            put("carVin", model.getCarVin());
            put("cabinNumber", model.getCabinNumber());
            put("terminalStation", model.getTerminalStation());
        }};

        int rows = 0;

        try {

            rows = railtransorderdetailmapper.updateByCarVin(params);
            return rows;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("操作失败");
            }
        }

    }

    @Override
    public String updateRangeByCarVin(List<RailtransParsedTextModel> models) {
        StringBuilder s = new StringBuilder();
        for (RailtransParsedTextModel model : models) {
            int rows = updateByCarVin(model);
            if(rows == 0){
                s.append(model.getCarVin());
                s.append(",");
            }
        }
        return s.toString();
    }



    @Override
    public void updateRangeCabinStatusByCabinNumber(List<RailtransCabinStatus> railtransCabinStatuses) {
        railtransorderdetailmapper.updateCabinStatusByCabinNumber(railtransCabinStatuses);
        //railtransCabinStatuses.forEach(status -> updateCabinStatusByCabinNumber(status));
    }

    @Override
    public void updateCabinStatusByCabinNumber(RailtransCabinStatus railtransCabinStatus) {
        try {
            railtransorderdetailmapper.updateCabinStatusByCabinNumber(railtransCabinStatus);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<RailtransOrderDetail> getByTransOrderId(Map<String, Object> params) {

        try {
            return railtransorderdetailmapper.getByTransOrderId(params);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<RailtransOrderDetail> getByCarVin(List<String> carVins) {

        try {

            return railtransorderdetailmapper.getByCarVin(carVins);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<RailtransOrderDetail> getByCabinNumber(List<String> cabinNumbers) {

        try {

            return railtransorderdetailmapper.getByCabinNumber(cabinNumbers);

        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public List selectDistinctCabinNumber() {

        try {
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.MINUTE, -10);
            return railtransorderdetailmapper.selectDistinctCabinNumber(nowTime.getTime());

        } catch (Exception e) {
            throw e;
        }
    }
}
