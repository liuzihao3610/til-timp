package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.TransportRecordOuter;
import com.tilchina.timp.service.TransportRecordOuterService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransportRecordOuterMapper;

/**
* 运输记录表(外)
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransportRecordOuterServiceImpl extends BaseServiceImpl<TransportRecordOuter> implements TransportRecordOuterService {

	@Autowired
    private TransportRecordOuterMapper transportrecordoutermapper;
	
	@Override
	protected BaseMapper<TransportRecordOuter> getMapper() {
		return transportrecordoutermapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransportRecordOuter transportrecordouter) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "transportOrderCode", "运单号", transportrecordouter.getTransportOrderCode(), 20));
        s.append(CheckUtils.checkDate("NO", "reportDate", "上报时间", transportrecordouter.getReportDate()));
        s.append(CheckUtils.checkInteger("NO", "carNumber", "商品车数量", transportrecordouter.getCarNumber(), 10));
        s.append(CheckUtils.checkInteger("NO", "transportStatus", "状态:0=行驶,1=停靠", transportrecordouter.getTransportStatus(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransportRecordOuter transportrecordouter) {
        StringBuilder s = checkNewRecord(transportrecordouter);
        s.append(CheckUtils.checkPrimaryKey(transportrecordouter.getTransportRecordOuterId()));
		return s;
	}

	@Override
	public PageInfo<TransportRecordOuter> queryListByCode(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryListByCode: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TransportRecordOuter> pageInfo = null;
        try {
        	if(map.isEmpty()) {
        		throw new BusinessException("9001","传入参数");
        	}
        	map.put("transportStatus", 0);
        	pageInfo = new PageInfo<TransportRecordOuter>(transportrecordoutermapper.selectList(map));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return pageInfo;
	}

}
