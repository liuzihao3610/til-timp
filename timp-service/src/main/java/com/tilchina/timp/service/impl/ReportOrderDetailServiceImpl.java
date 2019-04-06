package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.ReportOrderDetail;
import com.tilchina.timp.service.ReportOrderDetailService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ReportOrderDetailMapper;

/**
* 在途提报运单子表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class ReportOrderDetailServiceImpl extends BaseServiceImpl<ReportOrderDetail> implements ReportOrderDetailService {

	@Autowired
    private ReportOrderDetailMapper reportorderdetailmapper;
	
	@Override
	protected BaseMapper<ReportOrderDetail> getMapper() {
		return reportorderdetailmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(ReportOrderDetail reportorderdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", reportorderdetail.getCarVin(), 20));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(ReportOrderDetail reportorderdetail) {
        StringBuilder s = checkNewRecord(reportorderdetail);
        s.append(CheckUtils.checkPrimaryKey(reportorderdetail.getReportOrderDetailId()));
		return s;
	}

	@Override
	public List<ReportOrderDetail> queryByReportIdList(Long reportId) {
        log.debug("queryByReportIdList: {}",reportId);
        List<ReportOrderDetail> reportOrderDetails;
        try {
        	reportOrderDetails = reportorderdetailmapper.selectByReportIdList(reportId);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return reportOrderDetails;
    }

	@Override
	public List<ReportOrderDetail> queryByCarVinList(String carVin) {
        log.debug("queryByCarVinList: {}",carVin);
        List<ReportOrderDetail> reportOrderDetails;
        try {
        	reportOrderDetails = reportorderdetailmapper.selectByCarVinList(carVin);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return reportOrderDetails;
    }
}
